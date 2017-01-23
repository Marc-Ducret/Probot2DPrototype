package net.slimevoid.probot.game.components.script;

import net.slimevoid.probot.game.components.CPUBehaviour;

public class CPUInterface implements ScriptInterface {

	private final CPUBehaviour cpub;
	
	public CPUInterface(CPUBehaviour cpub) {
		this.cpub = cpub;
	}
	
	public double getTime() {
		return cpub.time;
	}
	
	@Override
	public String getName() {
		return "cpu";
	}
	
	public void print(String s) {
		System.out.println(s);
	}
}
