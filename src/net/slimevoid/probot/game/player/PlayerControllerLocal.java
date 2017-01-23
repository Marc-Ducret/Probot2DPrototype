package net.slimevoid.probot.game.player;

import java.util.HashMap;
import java.util.Map;

public class PlayerControllerLocal extends PlayerController {

	private Map<Integer, Boolean> keyStates;
	
	public PlayerControllerLocal(int playerID) {
		super(playerID);
		keyStates = new HashMap<>();
	}

	public void updateKey(int key, boolean state) {
		keyStates.put(key, state);
	}
	
	@Override
	public boolean getKeyState(int key) {
		return keyStates.containsKey(key) && keyStates.get(key);
	}

	@Override
	public boolean ready(long tickID) {
		return true;
	}
}
