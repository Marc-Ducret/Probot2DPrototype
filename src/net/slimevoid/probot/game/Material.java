package net.slimevoid.probot.game;

import java.awt.Color;

public enum Material {
	PVC(	Color.WHITE, 	.1F, 	.6F, 	1F),
	STEEL(	Color.GRAY, 	1F, 	.6F, 	10F),
	WOOD(	Color.YELLOW, 	.05F, 	.6F, 	.5F);
	
	private Material(Color color, float density, float friction, float hardness) {
		this.color = color;
		this.density = density;
		this.friction = friction;
		this.hardness = hardness;
	}
	
	public final Color color;
	public final float  density;
	public final float friction;
	public final float hardness;
}
