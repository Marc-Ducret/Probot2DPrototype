package net.slimevoid.probot.game.components;

import java.awt.Color;

public enum WireType {
	SCRIPT(new Color(0x5796DA)), POWER(new Color(0xDA5757));
	
	public final Color color;
	
	private WireType(Color color) {
		this.color = color;
	}
}
