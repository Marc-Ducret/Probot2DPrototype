package net.slimevoid.probot.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

public class DShape implements Drawable {

	private final Shape shape;
	private final Color color;
	private AffineTransform trans;
	private final float width;
	private int level;
	private float opacity;
	
	public DShape(Shape shape, Color color, AffineTransform trans) {
		this(shape, color, trans, 0);
	}
	
	public DShape(Shape shape, Color color, AffineTransform trans, float width) {
		this.shape = shape;
		this.color = color;
		this.trans = trans;
		this.width = width;
		this.level = 0;
		this.opacity = 1;
	}
	
	public DShape level(int lev) {
		level = lev;
		return this;
	}
	
	@Override
	public void draw(Graphics2D g2d) {
		AffineTransform prevTrans = g2d.getTransform();
		if(trans != null) {
			g2d.transform(trans);
		}
		g2d.setColor(color);
		if(width>0)	{
			g2d.setStroke(new BasicStroke(width));
			g2d.draw(shape);
		} else {
			g2d.fill(shape);
		}
		if(trans != null) {
			g2d.setTransform(prevTrans);
		}
	}

	@Override
	public int compareTo(Drawable o) {
		return this.getLevel() - o.getLevel();
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public float getOpacity() {
		return opacity;
	}

	@Override
	public Drawable opacity(float opacity) {
		this.opacity = opacity;
		return this;
	}
	
	public Drawable trans(AffineTransform trans) {
		this.trans = trans;
		return this;
	}
}
