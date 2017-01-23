package net.slimevoid.probot.client.gui.lab.tool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.List;

import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.client.gui.lab.GuiLabEditor;
import net.slimevoid.probot.render.DShape;
import net.slimevoid.probot.render.Drawable;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.JointType;

public class ToolAxis extends ToolAbstract {

	private Blueprint selected;
	private Vec2 anchorPoint;
	
	public ToolAxis(String name, GuiLabEditor lab) {
		super(name, lab);
		cursorOnBP = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			if(underMouse != null) {
				if(selected != null && underMouse != selected) {
					if(selected.link(underMouse, JointType.REVOLUTE, new float[]{anchorPoint.x, anchorPoint.y})) {
						selected.sel = Color.GREEN;
						underMouse.sel = Color.GREEN;
						selected = null;
					} else {
						underMouse.sel = Color.RED;
					}
				} else if(underMouse.parent == null) {
					selected = underMouse;
					anchorPoint = mouseInWorld(e).sub(new Vec2(selected.x, selected.y));
				}
			} else {
				selected = null;
			}
			break;
			
		case MouseEvent.BUTTON3:
			selected = null;
			break;
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		Vec2 m = mouseInWorld(e);
		if(selected != null) {
			float dx = m.x - anchorPoint.x - selected.x;
			float dy = m.y - anchorPoint.y - selected.y;
			selected.setPos(m.x - anchorPoint.x, m.y - anchorPoint.y);
			for(Blueprint bp : lab.bps) {
				if(bp.hasParent(selected)) {
					bp.setPos(bp.x + dx, bp.y + dy);
				}
			}
		}
	}
	
	@Override
	public void tick(double dt) {
		super.tick(dt);
		if(selected != null) selected.sel = Color.YELLOW;
	}
	
	@Override
	public void draw(List<Drawable> toDraw) {
		super.draw(toDraw);
		float hs = .05F;
		if(selected != null) {
			toDraw.add(new DShape(new Ellipse2D.Float(anchorPoint.x + selected.x - hs, anchorPoint.y + selected.y - hs, hs * 2, hs * 2), Color.GRAY, null).level(100));
		}
	}
	
	@Override
	public boolean acceptSelect(Blueprint bp) {
		return super.acceptSelect(bp) && bp != selected;
	}
}
