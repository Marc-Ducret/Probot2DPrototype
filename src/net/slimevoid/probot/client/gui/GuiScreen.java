package net.slimevoid.probot.client.gui;

import static java.lang.Math.log10;
import static java.lang.Math.pow;
import static java.lang.Math.round;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jbox2d.common.Vec2;

import net.slimevoid.probot.client.ProbotClient;
import net.slimevoid.probot.render.DShape;
import net.slimevoid.probot.render.Drawable;
import net.slimevoid.probot.render.RenderEngine;
import net.slimevoid.probot.render.RenderObject;

public class GuiScreen extends Gui implements RenderObject {

	private static final long serialVersionUID = 1L;
	
	public RenderEngine rEngine;
	private boolean grabXY;
	private boolean grabZ;
	private Vec2 startXY;
	private float startZ;
	
	public GuiScreen() {
		super(false);
		rEngine = new RenderEngine();
	}
	
	public void init() {
		rEngine.init();
		removeAll();
		add(rEngine.getScreen());
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			
			@Override
			public void componentResized(ComponentEvent e) {
				rEngine.resize(getSize().width, getSize().height);
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {}
			
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
		grabXY = false;
		grabZ = false;
		startXY = new Vec2();
		startZ = 0;
		rEngine.getScreen().addMouseListener(this);
		rEngine.getScreen().addMouseMotionListener(this);
		rEngine.resize(getSize().width, getSize().height);
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(!e.isControlDown()) return;
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			grabXY = true;
			startXY = new Vec2(e.getX(), e.getY()).sub(rEngine.off);
			break;
			
		case MouseEvent.BUTTON3:
			grabZ = true;
			startZ = e.getY();
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			grabXY = false;
		}
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			grabXY = false;
			break;
			
		case MouseEvent.BUTTON3:
			grabZ = false;
			break;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(grabXY) {
			rEngine.off.set(e.getX() - startXY.x, e.getY() - startXY.y);
		}
		if(grabZ) {
			float c = 1 + 0.02F * (-e.getY() + startZ);
			startZ = e.getY();
			rEngine.off = rEngine.off.mul(c);
			rEngine.scale *= c;
		}
	}

	@Override
	public void draw(List<Drawable> toDraw) {
		AffineTransform trans = new AffineTransform(rEngine.trans);
		try {
			trans.invert();
		} catch (NoninvertibleTransformException e) {
		}
		int magnitude = -round((float)log10(rEngine.scale))+2;
		float w = (float) (rEngine.scale * pow(10, magnitude));
		float h = 3;
		float h2 = 10 + h;
		float m = 30;
		toDraw.add(new DShape(new Rectangle2D.Float(rEngine.getW() - w - m * 1.5F, rEngine.getH() - h2/2-h/2 - m, h, h2), new Color(0x101520), trans).level(5000));
		toDraw.add(new DShape(new Rectangle2D.Float(rEngine.getW() - h - m * 1.5F, rEngine.getH() - h2/2-h/2 - m, h, h2), new Color(0x101520), trans).level(5000));
		toDraw.add(new DShape(new Rectangle2D.Float(rEngine.getW() - w - m * 1.5F, rEngine.getH() - h - m, w, h), new Color(0x101520), trans).level(5000));
		GlyphVector gv = new Font("Consolas", Font.PLAIN, 12).createGlyphVector(new FontRenderContext(null, true, true), "10^"+magnitude+" m");
		Shape shape = gv.getOutline((float) (rEngine.getW() - m * 1.5F - w / 2 - gv.getVisualBounds().getWidth() / 2), rEngine.getH() - h - m - 8);
		toDraw.add(new DShape(shape, new Color(0x101520), trans).level(5000));
		gv = new Font("Consolas", Font.PLAIN, 12).createGlyphVector(new FontRenderContext(null, true, true), "fps: "+ProbotClient.instance.fps);
		shape = gv.getOutline((float) (rEngine.getW() - m - gv.getVisualBounds().getWidth()), m);
		toDraw.add(new DShape(shape, new Color(0x101520), trans).level(5000));
	}
}
