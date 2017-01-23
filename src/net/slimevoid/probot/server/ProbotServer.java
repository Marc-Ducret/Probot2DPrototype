package net.slimevoid.probot.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.network.Client;
import net.slimevoid.probot.network.NetworkEngineServer;
import net.slimevoid.probot.network.packet.Packet03Peer;

public class ProbotServer {
	
	public static ProbotServer instance;
	
	public NetworkEngineServer net;
	public List<Client> clients;
	private Client waiting;
	private List<Blueprint> robotWaiting;
	
	public ProbotServer(int port) {
		net = new NetworkEngineServer(port);
		net.init();
		clients = new ArrayList<>();
		waiting = null;
	}
	
	public void newClient(Client client) {
		clients.add(client);
	}
	
	public void playRequest(Client c, List<Blueprint> robot) {
		System.out.println(c.getUsername()+" wants to play");
		if(waiting != null) {
			System.out.println("Initiate hole punching between "+waiting.getUsername()+" and "+c.getUsername());
			Socket sok = net.getPacketManager(waiting).getSocket();
			net.sendPacket(new Packet03Peer(getAddr(sok), sok.getPort(), 1, robotWaiting), c);
			sok = net.getPacketManager(c).getSocket();
			net.sendPacket(new Packet03Peer(getAddr(sok), sok.getPort(), 2, robot), waiting);
			waiting = null;
		} else {
			waiting = c;
			robotWaiting = robot;
		}
	}
	
	public static String getAddr(Socket sok) {
		String addr = sok.getInetAddress().getHostAddress();
		return addr.startsWith("127.0.0.1") ? "85.170.74.129" : addr;
	}
	
	public void run() {
		while(true) {
			net.tick();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		instance = new ProbotServer(8004);
		instance.run();
	}
}
