package net.slimevoid.probot.game.components;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.List;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import net.slimevoid.probot.game.Entity;
import net.slimevoid.probot.game.components.CompDB.CompEntry;
import net.slimevoid.probot.game.components.script.LaserInterface;
import net.slimevoid.probot.render.DShape;
import net.slimevoid.probot.render.Drawable;

public class LaserBehaviour extends ComponentBehaviour {
	
	public static final float LENGTH = 50;
	
	private int fire;
	private Vec2 hit;
	private Fixture fixHit;

	public LaserBehaviour(CompEntry comp, String compName) {
		super(comp, compName);
		scriptI = new LaserInterface(this);
		fire = 0;
	}
	
	public void fire() {
		if(fire == 0) fire = 60;
	}

	@Override
	public void tick(double dt) {
		if(fire > 0) {
			fire --;
			final Vec2 p1 = posRadial(.17F);
			Vec2 p2 = posRadial(5);
			hit = null;
			owner.world.raycast(new RayCastCallback() {
				
				@Override
				public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
					hit = point;
					fixHit = fixture;
					return fraction;
				}
			}, p1, p2);
			if(fire == 5) {
				owner.body.applyLinearImpulse(owner.body.getWorldVector(new Vec2(0, 2)), owner.body.getWorldCenter());
				if(hit != null) {
					for(Entity e : owner.game.entities) {
						if(e.body == fixHit.getBody()) e.split(e.body.getLocalPoint(p1), e.body.getLocalPoint(p2));;
					}
				}
			}
		}
	}
	
	private Vec2 posRadial(float r) {
		Vec2 pos = owner.body.getWorldCenter();
		float angle = -owner.body.getAngle();
		return pos.add(new Vec2((float)(-r*sin(angle)), (float)(-r*cos(angle))));
	}
	
	@Override
	public void draw(List<Drawable> toDraw, AffineTransform trans) {
		super.draw(toDraw, trans);
		if(fire > 0 && fire < 10) {
			Vec2 p1 = posRadial(.17F);
			Vec2 p2 = posRadial(5);
			if(hit != null) p2 = hit;
			toDraw.add(new DShape(new Line2D.Float(p1.x, p1.y, p2.x, p2.y), Color.RED, null, .03F));
		}
	}
}