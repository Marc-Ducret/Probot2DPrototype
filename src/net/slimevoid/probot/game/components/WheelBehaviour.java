package net.slimevoid.probot.game.components;

import static java.lang.Math.cos;
import static java.lang.Math.signum;
import static java.lang.Math.sin;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.List;

import org.jbox2d.common.Vec2;

import net.slimevoid.probot.game.components.CompDB.CompEntry;
import net.slimevoid.probot.game.components.script.WheelInterface;
import net.slimevoid.probot.render.DShape;
import net.slimevoid.probot.render.Drawable;

public class WheelBehaviour extends ComponentBehaviour {
	
	public float wheelSpeed;
	public float wheelAngle;
	public float wheelTorque;

	public WheelBehaviour(CompEntry comp, String compName) {
		super(comp, compName);
		wheelAngle = 0;
		wheelTorque = 0;
		scriptI = new WheelInterface(this);
	}

	@Override
	public void tick(double dt) {
		float r = comp.getFloat("wheelR");
		wheelSpeed = Vec2.dot(owner.body.getLinearVelocity(), owner.body.getWorldVector(new Vec2(0, -1))) / r;
		wheelAngle += wheelSpeed * dt;
		Vec2 t = owner.body.getWorldVector(new Vec2(1, 0));
		float tspeed = Vec2.dot(owner.body.getLinearVelocity(), t);
		owner.body.applyForce(owner.body.getWorldVector(new Vec2(0, 1).mul(-wheelTorque * r)), owner.body.getWorldCenter());
		float weight = owner.getTotalMass() * 9.8F / 4; // TODO count wheels;
		owner.body.applyForceToCenter(t.mul(-weight*.5F*signum(tspeed)));
	}
	
	@Override
	public void draw(List<Drawable> toDraw, AffineTransform trans) {
		super.draw(toDraw, trans);
		float r = comp.getFloat("wheelR");
		float w = comp.getFloat("wheelW");
		float y = (float) (cos(wheelAngle) * r * signum(sin(wheelAngle)));
		toDraw.add(new DShape(new Line2D.Float(-w/2, y, w/2, y), new Color(0xa5b0a1), trans, .01F).level(owner.level+1));
	}
}
