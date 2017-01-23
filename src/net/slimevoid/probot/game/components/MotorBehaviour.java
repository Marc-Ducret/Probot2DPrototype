package net.slimevoid.probot.game.components;

import org.jbox2d.dynamics.joints.RevoluteJoint;

import net.slimevoid.probot.game.components.CompDB.CompEntry;
import net.slimevoid.probot.game.components.script.MotorInterface;

public class MotorBehaviour extends ComponentBehaviour {
	
	public float speed;

	public MotorBehaviour(CompEntry comp, String compName) {
		super(comp, compName);
		scriptI = new MotorInterface(this);
	}

	@Override
	public void tick(double dt) {
		RevoluteJoint j = (RevoluteJoint) owner.parentJoint;
		j.enableMotor(speed != 0);
		j.setMotorSpeed(speed);
		j.setMaxMotorTorque(2F);
	}
}