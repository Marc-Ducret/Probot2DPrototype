package net.slimevoid.probot.game.player;

public abstract class PlayerController {
	
	public final int playerID;
	
	public PlayerController(int playerID) {
		this.playerID = playerID;
	}
	
	public abstract boolean getKeyState(int key);
	public abstract boolean ready(long tickID);
}
