package net.slimevoid.probot.client.gui.lab.tool;

import static java.lang.Math.atan2;
import static java.lang.Math.pow;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;

import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.client.gui.lab.GuiLabEditor;
import net.slimevoid.probot.game.components.CompDB.CompEntry;
import net.slimevoid.probot.render.DSVG;
import net.slimevoid.probot.render.Drawable;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.JointType;

public class ToolComponent extends ToolAbstract {

	private final CompEntry comp;
	private final AffineTransform trans;
	private boolean show;
	
	public ToolComponent(CompEntry comp, GuiLabEditor lab) {
		super(comp.getString("name"), lab);
		this.comp = comp;
		trans = new AffineTransform();
		show = false;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		super.mouseClicked(e);
		float[] loc = computeCompLoc(mouseInWorld(e));
		Blueprint bp = new Blueprint(comp);
		bp.setPos(loc[0], loc[1]);
		bp.setAngle(loc[2]);
		if(underMouse != null) {
			bp.parent = underMouse;
			bp.jointType = comp.getProp("jointType", JointType.class);
			switch(bp.jointType) {
			case REVOLUTE:
				Vec2 axis = comp.getVec2("jointAxis");
				bp.jointArgs = new float[]{axis.x, axis.y};
				break;
			default:
				bp.jointArgs = new float[]{};
				break;
			}
		}
		lab.bps.add(bp);
	}
	
	private List<Drawable> debugDraw;
	private float[] computeCompLoc(Vec2 m) {
		Vec2 pos;
		float a;
		switch(comp.getProp("placeType", PlaceType.class)) {
		case EDGE:
			Vec2 n = new Vec2();
			Vec2 bestH = null;
			Blueprint bestBp = null;
			float d = -1;
			for(Blueprint bp : lab.bps) {
				if(underMouse != null && underMouse != bp) continue;
				if(bp.comp != null) continue;
				for(int i = 0; i < bp.verts.length/2; i ++) {
					Point2D p1 = new Point2D.Float(bp.verts[i*2 + 0], bp.verts[i*2 + 1]);
					Point2D p2 = new Point2D.Float(bp.verts[(i*2 + 2) % bp.verts.length], bp.verts[(i*2 + 3) % bp.verts.length]);
					p1 = bp.trans.transform(p1, p1);
					p2 = bp.trans.transform(p2, p2);
					Vec2 v1 = new Vec2((float)p1.getX(), (float)p1.getY());
					Vec2 v2 = new Vec2((float)p2.getX(), (float)p2.getY());
					Vec2 u = v2.sub(v1);
					double t = -((m.x*v1.x-pow(v1.x,2)-m.x*v2.x+v1.x*v2.x+m.y*v1.y-pow(v1.y,2)-m.y*v2.y+v1.y*v2.y)/(pow(v1.x,2)-2*v1.x*v2.x+pow(v2.x,2)+pow(v1.y,2)-2*v1.y*v2.y+pow(v2.y,2)));
					Vec2 h = v1.add(u.mul((float)t));
					float newD = h.sub(m).lengthSquared();
					if(t > 0 && t < 1 && (d < 0 || newD < d)) {
						n = new Vec2(-u.y, u.x);
						bestH = h;
						d = newD;
						bestBp = bp;
					}
				}
			}
			if(bestBp != null) underMouse = bestBp;
			a = (float) atan2(n.y, n.x);
			pos = bestH;
			if(pos != null) {
				n.normalize();
				pos = pos.add(n.mul(comp.getFloat("placeEdgeNDist")));
			}
			break;
		case FREE:
			pos = m;
			a = 0;
			break;
		default:
			pos = null;
			a = 0;
			break;
		}
		if(pos == null) return null;
		return new float[]{pos.x, pos.y, a};
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		trans.setToIdentity();
		Vec2 m = mouseInWorld(e);
		float[] location = computeCompLoc(m);
		if(location == null) {
			trans.scale(0, 0);
		} else {
			trans.translate(location[0], location[1]);
			trans.rotate(location[2]);
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		show = true;
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		show = false;
	}
	
	@Override
	public void draw(List<Drawable> toDraw) {
		super.draw(toDraw);
		if(show) toDraw.add(new DSVG(comp.getString("icon"), comp.getFloat("size"), 100).trans(trans));
		if(debugDraw != null) toDraw.addAll(debugDraw);
	}
}
