package net.slimevoid.probot.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.client.gui.lab.GuiLabEditor;

public class Packet04PlayRequest extends Packet {
	
	public List<Blueprint> robot;
	
	public Packet04PlayRequest(List<Blueprint> robot) {
		this.robot = robot;
	} 

	public Packet04PlayRequest() {
	} 
	
	
	@Override
	public void read(DataInputStream in) throws IOException {
		System.out.println("read: "+in.readUTF());
		try {
			robot = GuiLabEditor.load(in);
		} catch (ClassNotFoundException e) {
			System.out.println("Missing Class | "+e.getMessage());
		}
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF("HELLO MuderFUCKER");
		GuiLabEditor.save(out, robot);
	}
}
