package net.slimevoid.probot.network;

import java.io.IOException;
import java.net.Socket;

import net.slimevoid.probot.client.PlayNetClient;
import net.slimevoid.probot.client.gui.play.PeerPlayer;
import net.slimevoid.probot.network.packet.Packet03Peer;

public class PacketManagerClient extends PacketManager {
	
    /**
     * Crée une PacketManager pour le socket spécifié.
     * @param socket Le socket avec qui le PacketManager va intéragir
     * @throws IOException L'exception est "lancée" quand il y a un probléme de conection avec le socket
     */
    public PacketManagerClient(Socket socket) throws IOException {
        super(socket);
    }
    
    @Override
    protected void handlePeer(Packet03Peer packet) {
    	super.handlePeer(packet);
    	try {
    		System.out.println("Received: "+packet.getPeerAddr()+" : "+packet.getPeerPort());
    		new Thread(new PeerPlayer(packet.getPeerAddr(), packet.getPeerPort(), 
    				socket.getLocalPort(), packet.getPlayerNumber() == 1, 
    				PlayNetClient.instance.robot, packet.getRobot())
    				).start();
		} catch (Exception e) {
			System.out.println("fail: "+e);
			e.printStackTrace(System.out);
		}
    }
    
	@Override
	protected void onDisconnect(String msg) {
		System.out.println("Disconnected "+msg);
	}
}