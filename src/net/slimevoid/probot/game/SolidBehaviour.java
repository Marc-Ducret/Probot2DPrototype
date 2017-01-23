package net.slimevoid.probot.game;

import java.awt.geom.AffineTransform;
import java.util.List;

import net.slimevoid.probot.game.components.Behaviour;
import net.slimevoid.probot.game.components.Wire;
import net.slimevoid.probot.render.Drawable;

public class SolidBehaviour implements Behaviour {
	
	public Entity owner;

	@Override
	public void tick(double dt) {
	}

	@Override
	public boolean renderBody() {
		return true;
	}

	@Override
	public void draw(List<Drawable> toDraw, AffineTransform trans) {
	}

	@Override
	public Behaviour setOwner(Entity e) {
		this.owner = e;
		return this;
	}

	@Override
	public void init() {
	}

	@Override
	public void onWireRemoved(Wire w) {}
}
