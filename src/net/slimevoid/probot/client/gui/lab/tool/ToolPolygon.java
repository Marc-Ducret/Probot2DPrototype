package net.slimevoid.probot.client.gui.lab.tool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;

import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.client.gui.lab.GuiLabEditor;
import net.slimevoid.probot.game.Material;
import net.slimevoid.probot.render.DShape;
import net.slimevoid.probot.render.Drawable;

public class ToolPolygon extends ToolAbstract {
	
	private static final double SNAP_DIST = .05;
	
	private List<Vec2> verts;
	private Vec2 m;

	public ToolPolygon(String name, GuiLabEditor lab) {
		super(name, lab);
		cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		cursorOnBP = cursor;
		verts = new ArrayList<>();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			Vec2 m = mouseInWorld(e);
			if(snap(m)) {
				// TODO test polygon validity
				Vec2 p = new Vec2();
				for(Vec2 v : verts) {
					p = p.add(v);
				}
				p = p.mul(1F/verts.size());
				float[] verts = new float[this.verts.size()*2];
				for(int i = 0; i < this.verts.size(); i ++) {
					verts[2*i  ] = this.verts.get(i).x - p.x;
					verts[2*i+1] = this.verts.get(i).y - p.y;
				}
				Blueprint bp = new Blueprint(Material.PVC, verts);
				bp.setPos(p.x, p.y);
				lab.bps.add(bp);
				this.verts.clear();
			} else {
				verts.add(m);
			}
			break;
		
		case MouseEvent.BUTTON3:
			if(!verts.isEmpty()) verts.remove(verts.size()-1);
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		m = mouseInWorld(e);
	}
	
	@Override
	public void draw(List<Drawable> toDraw) {
		Vec2 prev = null;
		for(Vec2 v : verts) {
			if(prev != null) toDraw.add(new DShape(new Line2D.Float(prev.x, prev.y, v.x, v.y), Color.BLACK, null, .01F));
			prev = v;
		}
		if(prev != null && m != null) {
			Vec2 v = snapTransform(m);
			toDraw.add(new DShape(new Line2D.Float(prev.x, prev.y, v.x, v.y), snap(m) ? Color.GREEN : Color.BLUE, null, .01F));
		}
	}
	
	private Vec2 snapTransform(Vec2 m) {
		if(snap(m)) return verts.get(0);
		return m;
	}
	
	private boolean snap(Vec2 m) {
		if(verts.isEmpty()) return false;
		Vec2 v = verts.get(0);
		return m.sub(v).lengthSquared() < SNAP_DIST*SNAP_DIST;
	}
}
