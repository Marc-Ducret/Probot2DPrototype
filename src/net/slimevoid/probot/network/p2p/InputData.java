package net.slimevoid.probot.network.p2p;

import static java.lang.Math.ceil;

import java.nio.ByteBuffer;

import net.slimevoid.probot.game.components.script.PlayerInterface;

public class InputData {
	
	public final boolean[] states;
	public final int tickID;
	
	public InputData(boolean[] states, int tickID) {
		this.states = states;
		this.tickID = tickID;
	}
	
	public void write(ByteBuffer buff) {
		buff.putInt(tickID);
		int ct = 0;
		byte b = 0;
		for(int i = 0; i < states.length; i ++) {
			if(states[i]) {
				b = (byte) (b | (0x1 << ct));
			}
			ct++;
			if(ct == 8) {
				buff.put(b);
				b = 0;
				ct = 0;
			}
		}
		if(ct > 0) buff.put(b);
	}
	
	public static InputData read(ByteBuffer buff) {
		int tickID = buff.getInt();
		boolean[] states = new boolean[PlayerInterface.KEYS.length];
		int ct = 0;
		byte b = buff.get();
		for(int i = 0; i < states.length; i ++) {
			byte filter = (byte) (0x1 << ct);
			states[i] = (b & filter) == filter;
			ct++;
			if(ct == 8) {
				b = buff.get();
				ct = 0;
			}
		}
		return new InputData(states, tickID);
	}
	
	public static int inputSize() {
		return Integer.BYTES + (int)ceil(PlayerInterface.KEYS.length / 8.0);
	}
}
