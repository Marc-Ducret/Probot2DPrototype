package net.slimevoid.probot.client.gui.lab.components;

import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;

import net.slimevoid.probot.game.Material;
import net.slimevoid.probot.game.components.Behaviour;
import net.slimevoid.probot.game.components.MotorBehaviour;
import net.slimevoid.probot.game.components.CompDB.CompEntry;

public class PropsMotor extends PropsComp {
	
	private static final long serialVersionUID = 1L;

	public PropsMotor(CompEntry comp) {
		super(Material.PVC, comp);
		hasScriptInterface = true;
	}
	
	@Override
	public Behaviour createBehaviour() {
		return new MotorBehaviour(comp, compName);
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		super.internalFrameClosed(e);
	}

	@Override
	public JPanel populatePropsFrame(JPanel pan) {
		return super.populatePropsFrame(pan);
	}
}