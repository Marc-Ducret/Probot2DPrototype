package net.slimevoid.probot.client.gui.lab.tool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.List;

import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.client.gui.lab.GuiLabEditor;
import net.slimevoid.probot.render.Drawable;
import net.slimevoid.probot.render.RenderObject;

import org.jbox2d.common.Vec2;

public class ToolAbstract implements MouseListener, MouseMotionListener, KeyListener, RenderObject {
	
	public final String name;
	public final GuiLabEditor lab;
	public Blueprint underMouse;
	public MouseEvent mousePos;
	public Cursor cursor;
	public Cursor cursorOnBP;
	
	public ToolAbstract(String name, GuiLabEditor lab) {
		this.name = name;
		this.lab = lab;
		cursor = Cursor.getDefaultCursor();
		cursorOnBP = Cursor.getDefaultCursor();
	}
	
	protected Vec2 mouseInWorld(MouseEvent e) {
		Point2D p = new Point2D.Float(e.getX(), e.getY());
		try {
			p = lab.screen.rEngine.trans.inverseTransform(p, new Point2D.Float());
		} catch (NoninvertibleTransformException e1) {
			e1.printStackTrace();
		}
		return new Vec2((float)p.getX(), (float)p.getY());
	}
	
	public void tick(double dt) {
		underMouse = null;
		if(mousePos != null) {
			for(Blueprint bp : lab.bps) {
				if(bp.isInside(mouseInWorld(mousePos)) && acceptSelect(bp)) {
					underMouse = bp;
				}
			}
			for(Blueprint bp : lab.bps) {
				if(bp.sel != null) {
					if(bp.sel.getAlpha() - 10 > 0) bp.sel = new Color(bp.sel.getRed(), bp.sel.getGreen(), bp.sel.getBlue(), bp.sel.getAlpha() - 10);
					else bp.sel = null;
				}
			}
		}
		if(underMouse != null && (underMouse.sel == null ||( underMouse.sel.getRGB() & 0xFFFFFF) == (Color.BLUE.getRGB() & 0xFFFFFF))) underMouse.sel = Color.BLUE;
		lab.screen.setCursor(underMouse != null ? cursorOnBP : cursor);
	}
	
	public boolean acceptSelect(Blueprint bp) {
		int lvl = bp.getLevel();
		return lvl < lab.currentLevel && (underMouse == null || underMouse.getLevel() <= lvl);
	}
	
	@Override
	public void draw(List<Drawable> toDraw) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_CONTROL) lab.changeTool(new ToolScreen("Screen", lab, this));
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mousePos = e;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePos = e;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
