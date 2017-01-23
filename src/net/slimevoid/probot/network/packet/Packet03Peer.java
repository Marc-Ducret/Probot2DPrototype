package net.slimevoid.probot.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.client.gui.lab.GuiLabEditor;

public class Packet03Peer extends Packet {
	
	private String peerAddr;
	private int peerPort;
	private int playerNumber;
	private List<Blueprint> robot;
	
	public Packet03Peer(String peerAddr, int peerPort, int playerNumber, List<Blueprint> robot) {
		this.peerAddr = peerAddr;
		this.peerPort = peerPort;
		this.playerNumber = playerNumber;
		this.robot = robot;
//		System.out.println("Sending packet peer (addr: "+peerAddr+" ; port: "+peerPort);
	}
	
	public Packet03Peer() {
	}
	
	@Override
	public void read(DataInputStream in) throws IOException {
		peerAddr = in.readUTF();
		peerPort = in.readInt();
		playerNumber = in.read();
		try {
			robot = GuiLabEditor.load(in);
		} catch (ClassNotFoundException e) {
			System.out.println("Missing class | "+e.getMessage());
		}
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(peerAddr);
		out.writeInt(peerPort);
		out.write(playerNumber);
		GuiLabEditor.save(out, robot);
	}
	
	public String getPeerAddr() {
		return peerAddr;
	}
	
	public int getPeerPort() {
		return peerPort;
	}
	
	public int getPlayerNumber() {
		return playerNumber;
	}
	
	public List<Blueprint> getRobot() {
		return robot;
	}
}
