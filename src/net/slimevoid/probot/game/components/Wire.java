package net.slimevoid.probot.game.components;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.List;

import net.slimevoid.probot.render.DShape;
import net.slimevoid.probot.render.Drawable;
import net.slimevoid.probot.render.RenderObject;

import org.jbox2d.common.Vec2;

public class Wire implements RenderObject, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public final WireType type;
	public final WireInterface src;
	public final WireInterface dst;
	public final Vec2 pSrc;
	public final Vec2 pDst;
	private int level;
	
	public Wire(WireType type, WireInterface src, Vec2 pSrc, WireInterface dst, Vec2 pDst) {
		this.type = type;
		this.src = src;
		this.dst = dst;
		this.pSrc = pSrc;
		this.pDst = pDst;
	}
	
	public Wire level(int level) {
		this.level = level;
		return this;
	}
	
	@Override
	public void draw(List<Drawable> toDraw) {
		Point2D ps = new Point2D.Float(pSrc.x, pDst.y);
		src.getWireTransform().transform(ps, ps);
		Point2D pd = new Point2D.Float(pDst.x, pDst.y);
		dst.getWireTransform().transform(pd, pd);
		toDraw.add(new DShape(new Line2D.Float(ps, pd), type.color, null, .01F).level(level));
	}
}
