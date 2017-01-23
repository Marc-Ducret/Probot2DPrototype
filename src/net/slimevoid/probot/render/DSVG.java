package net.slimevoid.probot.render;

import static java.lang.Math.max;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class DSVG implements Drawable {

	private static final int RASTER_RES = 128;
	private static final Map<String, BufferedImage> RASTERS = new HashMap<>();
	
	private static final boolean FAST_RENDER = true;

	private final int level;
	private Class<?> svgClass;
	private final AffineTransform trans;
	private float opacity;
	private boolean renderFast;
	
	public DSVG(String name, float scale, int level) {
		this(name, scale, level, FAST_RENDER);
	}

	public DSVG(String name, float scale, int level, boolean renderFast) {
		this.level = level;
		this.renderFast = renderFast;
		try {
			svgClass = ClassLoader.getSystemClassLoader().loadClass(name);
		} catch (Exception e) {
			e.printStackTrace();
			svgClass = null;
		}
		trans = new AffineTransform();
		float size = max(getW(), getH());
		if(renderFast) 	scale /= RASTER_RES;
		else			scale /= size;
		trans.scale(scale, scale);
		if(renderFast) 	trans.translate(scale*(-getX()+size/2F)  , scale*(-getY()+size/2F));
		else			trans.translate((-getX()-getW()/2F), (-getY()-getH()/2F));
		opacity = 1;
	}

	@Override
	public int compareTo(Drawable o) {
		return this.getLevel() - o .getLevel();
	}

	@Override
	public void draw(Graphics2D g2d) {
		if(renderFast) {
			if(!RASTERS.containsKey(svgClass.getName())) {
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsDevice gd = ge.getDefaultScreenDevice();
				GraphicsConfiguration gc = gd.getDefaultConfiguration();
				BufferedImage raster = gc.createCompatibleImage(RASTER_RES, RASTER_RES);
				raster = new BufferedImage(RASTER_RES, RASTER_RES, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = raster.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g.setComposite(AlphaComposite.Clear);
				g.fillRect(0, 0, RASTER_RES, RASTER_RES);
				g.setComposite(AlphaComposite.Src);
				AffineTransform at = new AffineTransform();
				float size = max(getW(), getH());
				float s = RASTER_RES/size;
				at.scale(s, s);
				at.translate(-getX()+(size-getW())/2F, -getY()+(size-getH())/2F);
				g.setTransform(at);
				try {
					svgClass.getMethod("paint", Graphics2D.class).invoke(null, g);
				} catch (Exception e) {
					e.printStackTrace();
				}
				g.dispose();
				RASTERS.put(svgClass.getName(), raster);
			}
			BufferedImage raster = RASTERS.get(svgClass.getName());
			AffineTransform t = g2d.getTransform();
			g2d.transform(trans);
			g2d.drawImage(raster, -RASTER_RES/2, -RASTER_RES/2, null);
			g2d.setTransform(t);
		} else {
			AffineTransform at = g2d.getTransform();
			g2d.transform(trans);
			try {
				svgClass.getMethod("paint", Graphics2D.class).invoke(null, g2d);
			} catch (Exception e) {
				e.printStackTrace();
			}
			g2d.setTransform(at);
		}
	}

	public int getX() {
		try {
			return (Integer) svgClass.getMethod("getOrigX").invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int getY() {
		try {
			return (Integer) svgClass.getMethod("getOrigY").invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int getW() {
		try {
			return (Integer) svgClass.getMethod("getOrigWidth").invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int getH() {
		try {
			return (Integer) svgClass.getMethod("getOrigHeight").invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int getLevel() {
		return level;
	}

	public Drawable trans(AffineTransform trans) {
		AffineTransform t = new AffineTransform(trans);
		t.concatenate(this.trans);
		this.trans.setTransform(t);
		return this;
	}

	@Override
	public float getOpacity() {
		return opacity;
	}

	@Override
	public Drawable opacity(float opacity) {
		this.opacity = opacity;
		return this;
	}
}
