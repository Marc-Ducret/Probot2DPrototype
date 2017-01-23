package net.slimevoid.probot.game.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.slimevoid.probot.game.components.script.PlayerInterface;
import net.slimevoid.probot.network.p2p.InputData;
import net.slimevoid.probot.network.p2p.P2PSocket;

public class PlayerControllerBuffred extends PlayerController {

	/**
	 * Elements of buffer should start with firstTickID + 1
	 */
	private int firstTickID;
	private List<InputData> buffer;
	private InputData current;
	
	public PlayerControllerBuffred(int playerID) {
		super(playerID);
		buffer = new ArrayList<>();
		firstTickID = 0;
	}

	@Override
	public boolean getKeyState(int key) {
		int id = Arrays.binarySearch(PlayerInterface.KEYS, key);
		return id >= 0 && current.states[id];
	}

	@Override
	public boolean ready(long tickID) {
		return !buffer.isEmpty() && firstTickID + P2PSocket.INPUT_BUFFER <= tickID;
	}

	public void pop() {
		assert(!buffer.isEmpty());
		current = buffer.remove(0);
		firstTickID = current.tickID;
	}
	
	public void add(InputData input) {
		assert(input != null);
		if(input.tickID == lastTickID() + 1)
			buffer.add(input);
		if(input.tickID > lastTickID())
			System.out.println("MISSING "+(lastTickID() + 1));
	}
	
	public int lastTickID() {
		return firstTickID + buffer.size();
	}
}
