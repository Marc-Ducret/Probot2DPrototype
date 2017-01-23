package net.slimevoid.probot.render;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jbox2d.common.Vec2;

public class RenderEngine {
	
	private int w;
	private int h;
	
	private Canvas screen;
	private BufferStrategy buff;
	private BufferedImage buffImg;
	public final AffineTransform trans;
	public final AffineTransform identity;
	public Vec2 off;
	public float scale;
	
	public RenderEngine() {
		w = 1280;
		h = 720;
		trans = new AffineTransform();
		identity = new AffineTransform();
		identity.setToIdentity();
		off = new Vec2();
		scale = 200;
	}
	
	public void resize(int w, int h) {
		this.w = w;
		this.h = h;
		if(screen != null) {
//			System.out.println("fuck buffer");
			screen.setBounds(0, 0, w, h);
			if(buff != null) synchronized(buff) {
				buff = null;
			}
		}
	}
	
	public void init() {
		screen = new Canvas();
		screen.setBounds(0, 0, w, h);
		screen.setIgnoreRepaint(true);
		if(buff != null) synchronized(buff) {
			buff = null;
		}
	}
	
	public void draw(RenderObject...objs) {
		trans.setToIdentity();
		trans.translate(w / 2 + off.x, h / 2 + off.y);
		trans.scale(scale, scale);
		List<Drawable> toDraw = new ArrayList<>();
		for(RenderObject ro : objs) ro.draw(toDraw);
		draw(toDraw);
	}
	
	public void draw(List<Drawable> toDraw) {
		if(buff == null) {
//			System.out.println("new buffer");
			screen.createBufferStrategy(2);
			buff = screen.getBufferStrategy();
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			buffImg = gc.createCompatibleImage(w, h);
		}
		if(buff != null) synchronized (buff) {
			Graphics2D g = buffImg.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setTransform(identity);
			g.setColor(new Color(0xa0a0a0));
//			g.setPaint(new RadialGradientPaint(new Point2D.Float(w/2F, h/2F), w/2F, new float[]{0, 1},  new Color[]{new Color(0x808080), new Color(0xa0a0a0)}));
			g.fillRect(0, 0, w, h);
			g.setTransform(trans);
			Collections.sort(toDraw);
			for(Drawable d : toDraw) {
				AlphaComposite ac = (AlphaComposite) g.getComposite();
				if(d.getOpacity() != ac.getAlpha()) g.setComposite(ac.derive(d.getOpacity()));
				d.draw(g);
				g.setComposite(ac);
			}
			g = (Graphics2D) buff.getDrawGraphics();
			g.drawImage(buffImg, 0, 0, null);
			g.dispose();
			buff.show();
		}
	}
	
	public Canvas getScreen() {
		return screen;
	}
	
	public int getW() {
		return w;
	}
	public int getH() {
		return h;
	}
}
