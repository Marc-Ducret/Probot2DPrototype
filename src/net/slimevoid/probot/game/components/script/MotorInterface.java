package net.slimevoid.probot.game.components.script;

import net.slimevoid.probot.game.components.MotorBehaviour;

public class MotorInterface implements ScriptInterface {
	
	private final MotorBehaviour mb;
	
	public MotorInterface(MotorBehaviour mb) {
		this.mb = mb;
	}
	
	public void setSpeed(float s) {
		mb.speed = s;
	}

	@Override
	public String getName() {
		return mb.compName;
	}
}
