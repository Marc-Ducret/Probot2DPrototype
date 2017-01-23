package net.slimevoid.probot.client.gui.play;

import java.io.IOException;
import java.util.List;

import net.slimevoid.probot.client.ProbotClient;
import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.network.p2p.P2PSocket;
import net.slimevoid.probot.network.p2p.P2PSocket.ClientType;

public class PeerPlayer implements Runnable {
	
	private String destAddr;
	private int destPort;
	private int locPort;
	private boolean first;
	private List<Blueprint> rMe;
	private List<Blueprint> rHim;
	
	public PeerPlayer(String destAddr, int destPort, int locPort, boolean first, List<Blueprint> rMe, List<Blueprint> rHim) {
		this.destAddr = destAddr;
		this.destPort = destPort;
		this.locPort = locPort;
		this.first = first;
		this.rMe = rMe;
		this.rHim = rHim;
	}

	@Override
	public void run() {
		P2PSocket sok = new P2PSocket(destAddr, destPort, locPort, first ? ClientType.A : ClientType.B);
		if(sok.initiateConnection(20)) {
			GuiPlay gui;
			try {
				gui = new GuiPlay(sok, first, rMe, rHim);
				sok.setGui(gui);
				ProbotClient.instance.changeGui(gui);
			} catch (ClassNotFoundException | IOException e) {}
		}
	}
}
