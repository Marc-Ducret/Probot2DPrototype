package net.slimevoid.probot.client.gui.lab.tool;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;

import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.client.gui.lab.GuiLabEditor;
import net.slimevoid.probot.game.Material;
import net.slimevoid.probot.render.DShape;
import net.slimevoid.probot.render.Drawable;

import org.jbox2d.common.Vec2;

public class ToolRectangle extends ToolAbstract {
	
	private Vec2 start;
	private Vec2 dest;

	public ToolRectangle(String name, GuiLabEditor lab) {
		super(name, lab);
		start = null;
		cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		cursorOnBP = cursor;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			start = mouseInWorld(e);
			break;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			dest = mouseInWorld(e);
			if(isValid()) {
				Vec2 he = start.sub(dest).abs().mul(.5F);
				Vec2 p = start.add(dest).mul(.5F);
				float[] verts = new float[]{
					-he.x, -he.y,
					-he.x, +he.y,
					+he.x, +he.y,
					+he.x, -he.y };
				Blueprint bp = new Blueprint(Material.PVC, verts);
				bp.setPos(p.x, p.y);
				lab.bps.add(bp);
			}
			start = null;
			dest = null;
			break;
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(start != null) dest = mouseInWorld(e);
	}
	
	@Override
	public void draw(List<Drawable> toDraw) {
		if(start != null && dest != null) {
			Rectangle2D shape = new Rectangle2D.Double(min(start.x, dest.x), min(start.y, dest.y), abs(dest.x - start.x), abs(dest.y - start.y));
			toDraw.add(new DShape(shape, isValid() ? Color.white : Color.red, null, .01F).level(100));
		}
	}
	
	private boolean isValid() {
		Vec2 he = start.sub(dest).abs();
		return he.x > .1F && he.y > .1F;
	}
}
