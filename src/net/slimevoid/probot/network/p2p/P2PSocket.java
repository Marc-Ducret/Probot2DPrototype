package net.slimevoid.probot.network.p2p;

import static java.lang.Math.max;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.slimevoid.probot.client.gui.play.GuiPlay;

// TODO optimize buffers: reduce allocations by maintaining a list of buffers

/**
 * Manages a peer to peer UDP connection established using the hole
 * punching technique
 */
public class P2PSocket {

	public static enum ClientType {A, B}

	private static enum PakID {KEEP_ALIVE, INPUT, ACK, TIME_REQ, TIME_ANS, START_GAME};
	
	private static final double FORCED_PACKET_LOSS = 0;
	private static final Random RAND = new Random();

	private static final byte MSG_HELLO = 42;
	private static final byte MSG_REC_OK = 57;

	private static final int BUFF_SIZE = 4096;

	private static final int TIMEOUT = 5000;

	private static final int TIME_REQ_TIMEOUT = 1000;
	private static final int TIME_REQ_LIFESPAN = 5000;
	private static final int TIME_REQ_PERIOD = 200;
	
	public static final int INPUT_BUFFER = 3;

	public final ClientType type;
	private String addr;
	private int port;
	private int localPort;
	private DatagramSocket sok;
	private long lastContactTime; // TODO imple timeout
	private List<byte[]> sendQueue;
	private boolean alive;
	private GuiPlay gui;
	private List<TimeRequest> timeReqs;
	public int pingEst;
	public long timeOffsetEst; // would be 0 for B
	private long lastTimeReq;
	private int ackStatus;
	private List<InputData> inputToSend;
	private int lastReqID;

	public P2PSocket(String addr, int port, int localPort, ClientType type) {
		this.addr = addr;
		this.port = port;
		this.localPort = localPort;
		this.type = type;
		sendQueue = new ArrayList<>();
		alive = false;
		timeReqs = new ArrayList<>();
		pingEst = -1;
		timeOffsetEst = 0;
		ackStatus = -1;
		inputToSend = new ArrayList<>();
		lastReqID = 0;
		System.out.println("P2P SOCKET type "+type);
	}

	/**
	 * Initiate the connection to the specified peer using the 
	 * hole punching technique
	 */
	public boolean initiateConnection(int tries) {
		System.out.println("Initiate connection to "+addr+":"+port+" from "+localPort);
		try {
			this.sok = new DatagramSocket(localPort);
			sok.setSoTimeout(200);
			DatagramPacket sendPak = new DatagramPacket(new byte[]{42}, 1, InetAddress.getByName(addr), port);
			DatagramPacket recePak = new DatagramPacket(new byte[1], 1);
			boolean success = false;
			for(int i = 0; i < tries; i ++) {
				try {
					sok.send(sendPak);
					sok.receive(recePak);
					byte msg = recePak.getData()[0];
					if(msg == MSG_HELLO || msg == MSG_REC_OK) sendPak.getData()[0] = MSG_REC_OK;
					if(msg == MSG_REC_OK) {
						success = true;
						for(int j = 0; j < 20; j++) sok.send(sendPak); // make sure peer knows I received
						break;
					}
				} catch(Exception e) {}
			}
			if(!success) System.out.println("p2p connection failed after "+tries+" tries");
			else {
				System.out.println("p2p connection successful");
				initThreads();
			}
			return success;
		} catch(Exception e) {
			System.out.println("p2p connection init FAILED "+e);
			return false;
		}
	}

	private void initThreads() {
		alive = true;
		lastContactTime = getLocalTime();
		lastTimeReq = lastContactTime;
		new Thread(new Runnable() {
			public void run() {
				byte[] buff = new byte[BUFF_SIZE];
				DatagramPacket recPak = new DatagramPacket(buff, BUFF_SIZE);
				try {
					sok.setSoTimeout(5000);
				} catch (SocketException e) {e.printStackTrace();}
				while(alive) {
					try {
						sok.receive(recPak);
						readPacket(buff);
					} catch (Exception e) {
						System.out.println("Error while receiving p2p packet: "+e);
						e.printStackTrace();
					}
				}
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				while(alive) {
					try {
						synchronized(sendQueue) {
							while(!sendQueue.isEmpty()) {
								byte[] buff = sendQueue.remove(0);
								DatagramPacket pak = new DatagramPacket(buff, buff.length, 
										InetAddress.getByName(addr), port);
								sok.send(pak);
							}
						}
						Thread.sleep(1);
					} catch (Exception e) {
						System.out.println("Error while sending p2p packet: "+e);
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void readPacket(byte[] data) {
		if((FORCED_PACKET_LOSS > 0) || (data.length < 0/*eviter le warning*/) && RAND.nextFloat() < FORCED_PACKET_LOSS) return;
		byte id = data[0];
		lastContactTime = getLocalTime();
		if(id >= 0 && id < PakID.values().length) {
			switch(PakID.values()[id]) {
			case INPUT:
				ByteBuffer buff = ByteBuffer.allocate(data.length);
				buff.put(data);
				buff.position(1);
				int n = buff.getInt();
				int ack = 0;
				for(int i = 0; i < n; i ++) {
					InputData input = InputData.read(buff);
					gui.pchim.add(input);
				}
				sendAck(gui.pchim.lastTickID());
				break;

			case ACK:
				buff = ByteBuffer.allocate(data.length);
				buff.put(data);
				buff.position(1);
				ack = buff.getInt();
				ackStatus = max(ackStatus, ack);
				break;

			case TIME_REQ:
				TimeRequest req = TimeRequest.read(data);
				pingEst = req.pingEst;
				sendTimeAns(req.reqID);
				break;

			case TIME_ANS:
				buff = ByteBuffer.allocate(data.length);
				buff.put(data);
				buff.position(1);
				int reqID = buff.getInt();
				long timeCorrect = buff.getLong();
				synchronized(timeReqs) {
					for(TimeRequest tr : timeReqs) {
						if(tr.reqID == reqID) tr.answer(timeCorrect, getLocalTime());
					}
				}
				break;
				
			case START_GAME:
				if(gui.timeStart < 0) System.out.println("RECEIVED GAME START!!");
				buff = ByteBuffer.allocate(data.length);
				buff.put(data);
				buff.position(1);
				gui.timeStart = buff.getLong();
				sendAck(0);
				break;
				
			default:
				break;
			}
		}
	}


	public void update() {
		if(getLocalTime() - lastContactTime > TIMEOUT) alive = false;
		if(alive) {
			if(type == ClientType.A) {
				synchronized(timeReqs) {
					for(int i = 0; i < timeReqs.size(); i ++) {
						TimeRequest tr = timeReqs.get(i);
						if(type == ClientType.A && getLocalTime() - tr.timeSent > 
						(tr.isAnswered() ? TIME_REQ_LIFESPAN : TIME_REQ_TIMEOUT)) {
							timeReqs.remove(i);
							i--;
						}
					}
					int ping = 0;
					long offset = 0;
					int ct = 0;
					for(TimeRequest tr : timeReqs) {
						if(tr.isAnswered()) {
							ping += (tr.timeRcv - tr.timeSent) / 2;
							offset += tr.timeCorrect - (tr.timeSent + tr.pingEst);
							ct++;
						}
					}
					if(ct > 0) {
						pingEst = ping / ct;
						timeOffsetEst = offset / ct;
					} else {
						pingEst = -1;
						timeOffsetEst = -1;
					}
				}
			}
			if(type == ClientType.A && getLocalTime() - lastTimeReq > TIME_REQ_PERIOD)
				sendTimeReq(lastReqID ++);
			if(type == ClientType.B && pingEst >= 0 && ackStatus < 0)
				sendStartGame(gui.timeStart < 0 ? getGameTime() + 5000 : gui.timeStart);
		}
	}

	private void queuePacket(PakID id, byte[] data) {
		data[0] = (byte) id.ordinal();
		synchronized(sendQueue) {
			sendQueue.add(data);
		}
	}

	public void sendKeepAlive() {
		queuePacket(PakID.KEEP_ALIVE, new byte[1]);
	}

	public boolean isAlive() {
		return alive;
	}

	public void sendInput(InputData input) {
		inputToSend.add(input);
		for(int i = 0; i < inputToSend.size(); i++) {
			if(inputToSend.get(i).tickID <= ackStatus) {
				inputToSend.remove(i);
				i--;
			}
		}
		sendInput(inputToSend);
	}
	
	private void sendInput(List<InputData> data) {
		ByteBuffer buf = ByteBuffer.allocate(1+Integer.BYTES+InputData.inputSize()*data.size());
		buf.position(1);
		buf.putInt(data.size());
		for(InputData id : data)
			id.write(buf);
		queuePacket(PakID.INPUT, buf.array());
	}

	private void sendAck(int tickID) {
		ByteBuffer buff = ByteBuffer.allocate(1 + Integer.BYTES);
		buff.position(1);
		buff.putInt(tickID);
		queuePacket(PakID.ACK, buff.array());
	}
	
	private void sendTimeReq(int reqID) {
		long t1 = getLocalTime();
		lastTimeReq = t1;
		TimeRequest req = new TimeRequest(reqID, pingEst, t1 + timeOffsetEst + pingEst, t1);
		ByteBuffer data = ByteBuffer.allocate(1+Integer.BYTES*2 + Long.BYTES*2);
		data.position(1);
		req.write(data);
		synchronized(timeReqs) {
			timeReqs.add(req);	
		}
		queuePacket(PakID.TIME_REQ, data.array());
	}

	private void sendTimeAns(int reqID) {
		long timeCorrect = getLocalTime();
		ByteBuffer data = ByteBuffer.allocate(1 + Integer.BYTES + Long.BYTES);
		data.position(1);
		data.putInt(reqID);
		data.putLong(timeCorrect);
		queuePacket(PakID.TIME_ANS, data.array());
	}
	
	private void sendStartGame(long startTime) {
		if(gui.timeStart < 0)
			System.out.println("LETS START");
		ByteBuffer data = ByteBuffer.allocate(1+Long.BYTES);
		data.position(1);
		data.putLong(startTime);
		queuePacket(PakID.START_GAME, data.array());
		gui.timeStart = startTime;
	}

	public void setGui(GuiPlay gui) {
		this.gui = gui;
	}
	
	public long getLocalTime() {
		return System.currentTimeMillis();
	}

	public long getGameTime() {
		return getLocalTime() + timeOffsetEst;
	}
}
