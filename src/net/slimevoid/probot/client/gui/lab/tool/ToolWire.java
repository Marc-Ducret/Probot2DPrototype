package net.slimevoid.probot.client.gui.lab.tool;

import java.awt.event.MouseEvent;

import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.client.gui.lab.GuiLabEditor;
import net.slimevoid.probot.client.gui.lab.components.PropsCPU;
import net.slimevoid.probot.client.gui.lab.components.PropsComp;
import net.slimevoid.probot.game.components.Wire;
import net.slimevoid.probot.game.components.WireType;

import org.jbox2d.common.Vec2;

public class ToolWire extends ToolAbstract {
	
	public ToolWire(String name, GuiLabEditor lab) {
		super(name, lab);
	}

	private Blueprint selected;
	private Vec2 selP;
	
	@Override
	public void mousePressed(MouseEvent e) {
		super.mouseClicked(e);
		Vec2 m = mouseInWorld(e);
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			if(underMouse != null) {
				if(selected == null) {
					selected = underMouse;
					selP = m.sub(new Vec2(underMouse.x, underMouse.y)); //TODO rotation
				} else {
					((PropsCPU)underMouse.props).addWire(new Wire(WireType.SCRIPT, (PropsComp)selected.props, selP, (PropsComp)underMouse.props, m.sub(new Vec2(underMouse.x, underMouse.y))));
					selected = null;
				}
			}
			break;
		}
	}
	
	@Override
	public boolean acceptSelect(Blueprint bp) {
		if(super.acceptSelect(bp)) {
			if(selected == null) {
				return bp.props instanceof PropsComp && ((PropsComp)bp.props).hasScriptInterface;
			} else {
				return bp.comp != null && bp.comp.getString("type").equalsIgnoreCase("CPU");
			}
		}
		return false;
	}
}
