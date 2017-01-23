package net.slimevoid.probot.client.gui.lab.tool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.client.gui.lab.GuiLabEditor;

import org.jbox2d.dynamics.joints.JointType;

public class ToolWeld extends ToolAbstract {

	private Blueprint selected;
	
	public ToolWeld(String name, GuiLabEditor lab) {
		super(name, lab);
		cursorOnBP = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			if(underMouse != null) {
				if(selected != null && underMouse != selected) {
					if(selected.link(underMouse, JointType.WELD, new float[]{})) {
						selected.sel = Color.GREEN;
						underMouse.sel = Color.GREEN;
						selected = null;
					} else {
						underMouse.sel = Color.RED;
					}
				} else if(underMouse.parent == null) {
					selected = underMouse;
				}
			} else {
				selected = null;
			}
			break;
			
		case MouseEvent.BUTTON3:
			if(underMouse != null && underMouse.parent != null) {
				underMouse.sel = Color.RED;
				underMouse.parent.sel = Color.RED;
				underMouse.unlink();
			}
			break;
		}
	}
	
	@Override
	public void tick(double dt) {
		super.tick(dt);
		if(selected != null) selected.sel = Color.YELLOW;
	}
}
