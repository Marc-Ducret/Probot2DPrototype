package net.slimevoid.probot.game.components.script;

import net.slimevoid.probot.game.components.LaserBehaviour;

public class LaserInterface implements ScriptInterface {
	
	private final LaserBehaviour lb;
	
	public LaserInterface(LaserBehaviour lb) {
		this.lb = lb;
	}
	
	public void fire() {
		lb.fire();
	}

	@Override
	public String getName() {
		return lb.compName;
	}
}
