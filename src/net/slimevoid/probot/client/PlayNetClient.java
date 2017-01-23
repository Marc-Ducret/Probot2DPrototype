package net.slimevoid.probot.client;

import java.util.List;

import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.client.gui.mainmenu.GuiMainMenu;
import net.slimevoid.probot.network.NetworkEngineClient;
import net.slimevoid.probot.network.packet.Packet01Login;

public class PlayNetClient {
	
	public static PlayNetClient instance;
	
	public final NetworkEngineClient net;
	public final GuiMainMenu mainMenu;
	private final String username;
	private boolean run;
	public List<Blueprint> robot;
	
	private PlayNetClient(GuiMainMenu mainMenu) {
		username = System.getProperty("user.name")+"["+hashCode()%100+"]";
		net = new NetworkEngineClient();
		this.mainMenu = mainMenu;
	}
	
	public boolean connect(String addr, int port) {
		return net.connect(addr, port);
	}
	
	public void run() {
		run = true;
		net.server.sendPacket(new Packet01Login(username));
		while(run) {
			net.tick();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		if(run) {
			run = false;
			net.server.destroy("Disconnect");
		}
	}
	
	public static PlayNetClient startNet(GuiMainMenu mainMenu) {
		if(instance != null) instance.stop();
		instance = new PlayNetClient(mainMenu);
		return instance;
	}
}
