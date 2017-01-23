package net.slimevoid.probot.render;

import java.awt.Graphics2D;

public interface Drawable extends Comparable<Drawable> {
	public void draw(Graphics2D g2d);
	public int getLevel();
	public float getOpacity();
	public Drawable opacity(float opacity);
}
