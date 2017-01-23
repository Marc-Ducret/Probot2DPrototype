package net.slimevoid.probot.client.gui.lab;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.List;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.joints.WeldJointDef;

import net.slimevoid.probot.client.gui.lab.components.PropsComp;
import net.slimevoid.probot.game.Entity;
import net.slimevoid.probot.game.Game;
import net.slimevoid.probot.game.Material;
import net.slimevoid.probot.game.components.Behaviour;
import net.slimevoid.probot.game.components.CompDB.CompEntry;
import net.slimevoid.probot.game.components.Wire;
import net.slimevoid.probot.render.DSVG;
import net.slimevoid.probot.render.DShape;
import net.slimevoid.probot.render.Drawable;
import net.slimevoid.probot.render.RenderObject;
import net.slimevoid.probot.utils.AABB;

public class Blueprint implements Serializable, RenderObject {
	
	private static final long serialVersionUID = 1L;
	
	public static final int LEVEL_GAP = 10;
	
	private static float[] compVerts(CompEntry comp) {
		float h = comp.getFloat("size") / 2;
		return new float[]{
				-h, -h,
				h, -h,
				h, h,
				-h, h};
	}
	
	private static BPProperties compProps(CompEntry comp) {
		try {
			Class<?> pclass = ClassLoader.getSystemClassLoader().loadClass("net.slimevoid.probot.client.gui.lab.components.Props"+comp.getString("type"));
			return (BPProperties) pclass.getConstructor(CompEntry.class).newInstance(comp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Blueprint parent;
	public JointType jointType;
	public float[] jointArgs;
	public float x;
	public float y;
	public float a;
	public float[] verts;
	public BPProperties props;
	public CompEntry comp;
	public transient AffineTransform trans;
	private transient Path2D shape;
	public transient Color sel;
	public transient float opacity;
	
	public Blueprint(CompEntry comp) {
		this(compProps(comp), compVerts(comp));
		this.comp = comp;
	}
	
	public Blueprint(Material mat, float[] verts) {
		this(new SolidProps(mat), verts);
	}
	
	private Blueprint(BPProperties props, float[] verts) {
		this.props = props;
		this.verts = verts;
		init();
	}
	
	public void init() {
		Path2D path = new Path2D.Float();
		for(int i = 0; i < verts.length/2; i ++) {
			int i2 = (i+1) % (verts.length/2);
			path.append(new Line2D.Float(verts[i*2], verts[i*2+1], verts[i2*2], verts[i2*2+1]), true);
		}
		path.closePath();
		shape = path;
		trans = new AffineTransform();
		opacity = 1;
		props.setBp(this);
		updateTrans();
	}

	public boolean isInside(Vec2 p) {
		Point2D pos = new Point2D.Float(p.x, p.y);
		try {
			pos = trans.inverseTransform(pos, pos);
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		return shape.contains(pos);
	}
	
	@Override
	public void draw(List<Drawable> toDraw) {
		int level = getLevel();
		if(comp != null) {
			toDraw.add(new DSVG(comp.getString("icon"), comp.getFloat("size"), level).trans(trans).opacity(opacity));
		} else {
			toDraw.add(new DShape(shape, props.mat.color, trans).level(level).opacity(opacity));
			toDraw.add(new DShape(shape, Color.BLACK, trans, .004F).level(level).opacity(opacity));
		}
		if(sel != null) toDraw.add(new DShape(shape, sel, trans, 0.01F).level(level).opacity(opacity));
		if(parent != null) {
			switch(jointType) {
			case REVOLUTE:
				Vec2 c = getCenterOfRotation();
				float hs = .05F;
				toDraw.add(new DShape(new Ellipse2D.Float(c.x - hs, c.y - hs, hs * 2, hs * 2), Color.GRAY, null).level(level+1).opacity(opacity));
				break;
				
			default:
				break;
			}
		}
		props.draw(toDraw);
	}
	
	public void setPos(float x, float y) {
		this.x = x;
		this.y = y;
		updateTrans();
	}
	
	public void setAngle(float a) {
		this.a = a;
		updateTrans();
	}
	
	public void updateTrans() {
		trans.setToIdentity();
		trans.translate(x, y);
		trans.rotate(a);
	}
 	
	public boolean link(Blueprint bp, JointType type, float[] args) {
		if(this == bp || bp.hasParent(this) || parent != null) return false;
		switch(type) {
		case WELD:
			if(!inter(bp)) return false;
			break;
			
		case REVOLUTE:
			if(args.length < 2) return false;
			Point2D axisA = new Point2D.Float(args[0], args[1]);
			Point2D axisB = trans.transform(axisA, new Point2D.Float());
			try {
				axisB = bp.trans.inverseTransform(axisB, axisB);
			} catch (NoninvertibleTransformException e) {
				e.printStackTrace();
			}
			if(!shape.contains(axisA) || !bp.shape.contains(axisB)) return false;
			break;
		default:
			return false;
		}
		parent = bp;
		jointType = type;
		jointArgs = args;
		return true;
	}
	
	private boolean inter(Blueprint bp) {
		for(int i = 0; i < this.verts.length/2; i++) {
			Point2D p = new Point2D.Float(this.verts[i*2], this.verts[i*2+1]);
			p = this.trans.transform(p, p);
			try {
				p = bp.trans.inverseTransform(p, p);
			} catch (NoninvertibleTransformException e) {
				e.printStackTrace();
			}
			if(bp.shape.contains(p)) {
				return true;
			}
		}
		for(int i = 0; i < bp.verts.length/2; i++) {
			Point2D p = new Point2D.Float(bp.verts[i*2], bp.verts[i*2+1]);
			p = bp.trans.transform(p, p);
			try {
				p = this.trans.inverseTransform(p, p);
			} catch (NoninvertibleTransformException e) {
				e.printStackTrace();
			}
			if(this.shape.contains(p)) {
				return true;
			}
		}
		return false;
	}
	
	private Vec2 interMassCenter(Blueprint bp) {
		Vec2 sum = new Vec2();
		int ct = 0;
		for(int i = 0; i < this.verts.length/2; i++) {
			Point2D p = new Point2D.Float(this.verts[i*2], this.verts[i*2+1]);
			Point2D wP = new Point2D.Float();
			wP = this.trans.transform(p, wP);
			try {
				p = bp.trans.inverseTransform(wP, p);
			} catch (NoninvertibleTransformException e) {
				e.printStackTrace();
			}
			if(bp.shape.contains(p)) {
				sum = sum.add(new Vec2((float)wP.getX(), (float)wP.getY()));
				ct ++;
			}
		}
		for(int i = 0; i < bp.verts.length/2; i++) {
			Point2D p = new Point2D.Float(bp.verts[i*2], bp.verts[i*2+1]);
			Point2D wP = new Point2D.Float();
			wP = bp.trans.transform(p, wP);
			try {
				p = this.trans.inverseTransform(wP, p);
			} catch (NoninvertibleTransformException e) {
				e.printStackTrace();
			}
			if(this.shape.contains(p)) {
				sum = sum.add(new Vec2((float)wP.getX(), (float)wP.getY()));
				ct ++;
			}
		}
		if(ct == 0) return null;
		else 		return sum.mul(1F/ct);
	}

	public void unlink() {
		parent = null;
	}
	
	public boolean hasParent(Blueprint bp) {
		if(parent != null) {
			return parent == bp || parent.hasParent(bp);
		}
		return false;
	}

	private transient Entity e;
	public void enterGame(Game game, int robotIndex) {
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position = new Vec2(x, y);
		bd.angle = a;
		bd.angularDamping = .0F;
		if(getLevel() == 0) {
			bd.linearDamping = props.mat.friction;
			bd.angularDamping = props.mat.friction;
		}
		FixtureDef fd = new FixtureDef();
		fd.density = props.mat.density;
		fd.friction = props.mat.friction;
		PolygonShape shape = new PolygonShape();
		int count = verts.length/2;
		Vec2[] vertices = new Vec2[count];
		for(int i = 0; i < count; i ++) vertices[i] = new Vec2(verts[i*2], verts[i*2+1]);
		shape.set(vertices, count);
		fd.shape = shape;
		Behaviour behaviour = props.createBehaviour();
		e = new Entity(robotIndex, bd, fd).color(props.mat.color).behaviour(behaviour).level(getLevel()).hardness(props.mat.hardness);
		behaviour.setOwner(e);
		game.addEntity(e);
	}
	
	public void initEntity(Game game, int robotIndex) {
		if(parent != null) {
			JointDef jd;
			Vec2 anchor;
			switch(jointType) {
			case WELD:
				WeldJointDef wjd = new WeldJointDef();
				wjd.initialize(e.body, parent.e.body, parent.e.body.getWorldCenter());
				anchor = e.body.getLocalPoint(interMassCenter(parent));
				jd = wjd;
				break;
				
			case REVOLUTE:
				RevoluteJointDef rjd = new RevoluteJointDef();
				rjd.initialize(e.body, parent.e.body, e.body.getWorldPoint(new Vec2(jointArgs[0], jointArgs[1])));
				anchor = new Vec2(jointArgs[0], jointArgs[1]);
				jd = rjd;
				break;
				
			default:
				jd = new JointDef();
				anchor = null;
				break;
			}
			jd.bodyA = e.body;
			jd.bodyB = parent.e.body;
			jd.type = jointType;
			e.jointDef = jd;
			e.parentJoint = game.addJoint(jd);
			e.parent = parent.e;
			e.anchorPoint = anchor;
			parent.e.childs.add(e);
		}
		if(comp != null) {
			PropsComp pc = (PropsComp) props;
			for(Wire w : pc.wires) {
				e.addWire(new Wire(w.type, ((PropsComp)w.src).bp.e, w.pSrc, ((PropsComp)w.dst).bp.e, w.pDst).level(getLevel()));
			}
		}
		e.behaviour.init();
	}

	public void rotate(float da, Vec2 center) {
		float a = (float) atan2(y - center.y, x-center.x);
		float d = (float) sqrt(pow(x-center.x, 2)+pow(y-center.y, 2));
		setPos((float) (center.x + d * cos(a + da)), (float) (center.y + d * sin(a + da)));
		setAngle(this.a + da);
	}
	
	public Vec2 getCenterOfRotation() {
		if(jointType == JointType.REVOLUTE) {
			Point2D c = new Point2D.Float(jointArgs[0], jointArgs[1]);
			c = trans.transform(c, c);
			return new Vec2((float)c.getX(), (float)c.getY());
		} else {
			return new Vec2(x, y);
		}
	}
	
	public int getLevel() {
		Blueprint bp = this;
		int level = 0;
		while(bp.parent != null) {
			level ++;
			bp = bp.parent;
		}
		return level * LEVEL_GAP;
	}
	
	public AABB getBounds()	 {
		Rectangle2D rect = shape.createTransformedShape(trans).getBounds2D();
		return new AABB(rect.getMinX(), rect.getMaxX(), rect.getMinY(), rect.getMaxY()); 
	}
}
