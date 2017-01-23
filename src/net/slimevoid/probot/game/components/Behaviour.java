package net.slimevoid.probot.game.components;

import java.awt.geom.AffineTransform;
import java.util.List;

import net.slimevoid.probot.game.Entity;
import net.slimevoid.probot.render.Drawable;

public interface Behaviour {
	public Behaviour setOwner(Entity e);
	public void init();
	public void tick(double dt);
	public boolean renderBody();
	public void draw(List<Drawable> toDraw, AffineTransform trans);
	public void onWireRemoved(Wire w);
}
