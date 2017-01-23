package net.slimevoid.probot.client.gui.lab.components;

import net.slimevoid.probot.game.Material;
import net.slimevoid.probot.game.components.Behaviour;
import net.slimevoid.probot.game.components.CompDB.CompEntry;
import net.slimevoid.probot.game.components.DisplayBehaviour;

public class PropsDisplay extends PropsComp {

	private static final long serialVersionUID = 1L;

	public PropsDisplay(CompEntry comp) {
		super(Material.PVC, comp);
		hasScriptInterface = true;
	}

	@Override
	public Behaviour createBehaviour() {
		return new DisplayBehaviour(comp, compName);
	}
}
