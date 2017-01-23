package net.slimevoid.probot.client.gui.lab.tool;

import static java.lang.Math.atan2;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.List;

import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.client.gui.lab.GuiLabEditor;
import net.slimevoid.probot.render.Drawable;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.JointType;

public class ToolMove extends ToolAbstract {

	private Blueprint selected;
	private Vec2 startp;
	private float starta;
	private int lastPressed;
	
	public ToolMove(String name, GuiLabEditor lab) {
		super(name, lab);
		cursorOnBP = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
		case MouseEvent.BUTTON3:
			lastPressed = e.getButton();
			Vec2 m = mouseInWorld(e);
			if(underMouse != null) {
				while(underMouse.parent != null && underMouse.jointType == JointType.WELD) underMouse = underMouse.parent;
				if(underMouse.jointType == JointType.REVOLUTE) lastPressed = MouseEvent.BUTTON3;
				selected = underMouse;
				startp = m.sub(new Vec2(underMouse.x, underMouse.y));
				Vec2 delta = m.sub(underMouse.getCenterOfRotation());
				starta = (float) atan2(delta.y, delta.x) - underMouse.a;
			}
			break;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
		case MouseEvent.BUTTON3:
			selected = null;
			break;
		}
	}
	
	@Override
	public void tick(double dt) {
		super.tick(dt);
		if(selected != null) selected.sel = Color.MAGENTA;
	}
	
	@Override
	public void draw(List<Drawable> toDraw) {
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		if(selected != null) {
			Vec2 m = mouseInWorld(e);
			switch(lastPressed) {
			case MouseEvent.BUTTON1:
				float dx = m.x - startp.x - selected.x;
				float dy = m.y - startp.y - selected.y;
				selected.setPos(m.x - startp.x, m.y - startp.y);
				for(Blueprint bp : lab.bps) {
					if(bp.hasParent(selected)) {
						bp.setPos(bp.x + dx, bp.y + dy);
					}
				}
				break;
				
			case MouseEvent.BUTTON3:
				Vec2 center = selected.getCenterOfRotation();
				Vec2 rm = m.sub(center);
				float da = (float) (atan2(rm.y, rm.x) - starta) - selected.a;
				selected.rotate(da, center);
				for(Blueprint bp : lab.bps) {
					if(bp.hasParent(selected)) {
						bp.rotate(da, center);
					}
				}
				break;
			}
		}
	}
}
