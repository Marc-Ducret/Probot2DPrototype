package net.slimevoid.probot.game.components.script;

import net.slimevoid.probot.game.components.WheelBehaviour;

public class WheelInterface implements ScriptInterface {

	private WheelBehaviour wb;
	
	public WheelInterface(WheelBehaviour wb) {
		this.wb = wb;
	}
	
	public void setTorque(float torque) {
		wb.wheelTorque = torque;
	}
	
	public float getSpeed() {
		return wb.wheelSpeed;
	}

	@Override
	public String getName() {
		return wb.compName;
	}
}
