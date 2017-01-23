package net.slimevoid.probot.game;

import static java.lang.Math.ceil;
import static java.lang.Math.random;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;

import net.slimevoid.probot.game.components.Behaviour;
import net.slimevoid.probot.game.components.Wire;
import net.slimevoid.probot.game.components.WireInterface;
import net.slimevoid.probot.render.DShape;
import net.slimevoid.probot.render.Drawable;

public class Entity implements WireInterface {
	
	public Body body;
	public World world;
	public Game game;
	public Color color;
	public JointDef jointDef;
	public Joint parentJoint;
	public Vec2 anchorPoint;
	public Entity parent;
	public List<Entity> childs;
	public List<Wire> wires;
	private BodyDef bdef;
	private FixtureDef fdef;
	public Behaviour behaviour;
	public final int owner;
	public float health;
	public float maxHealth;
	public int level;
	public float hardness;
	public Vec2 damageCenter;
	
	public Entity(int owner, BodyDef bdef, FixtureDef fdef) {
		this.bdef = bdef;
		this.fdef = fdef;
		this.color = new Color((float)random(), (float)random(), (float)random());
		wires = new ArrayList<>();
		this.owner = owner;
		this.maxHealth = 5F; // TODO change start health
		this.level = 0;
		hardness = 1;
		damageCenter = new Vec2();
		childs = new ArrayList<>();
	}
	
	public Entity color(Color color) {
		this.color = color;
		return this;
	}
	
	public Entity behaviour(Behaviour behaviour) {
		this.behaviour = behaviour;
		return this;
	}
	
	public Entity hardness(float hardness) {
		this.hardness = hardness;
		return this;
	}
	
	public void enterWorld(World world, Game game) {
		body = world.createBody(bdef);
		this.game = game;
		this.world = world;
		body.createFixture(fdef);
		health = maxHealth;
	}
	
	public void tick(double dt) { 
		if(health <= 0) breakApart();
		else {
			for(int i = 0; i < wires.size(); i ++) {
				Wire w = wires.get(i);
				if(w.src instanceof Entity && w.dst instanceof Entity) {
					Entity src = (Entity) w.src;
					Entity dst = (Entity) w.dst;
					if(src.getSuperParent() != dst.getSuperParent()) {
						src.behaviour.onWireRemoved(w);
						dst.behaviour.onWireRemoved(w);
						wires.remove(i);
						i--;
					}
				}
			}
//			if(level == 0) body.applyForceToCenter(body.getLinearVelocity().mul(-.1F));
			behaviour.tick(dt);
		}
	}
	
	public void breakApart() {
		Vec2 dmgPos = damageCenter.mul(1/(maxHealth - health));
		PolygonShape pol = (PolygonShape) fdef.shape;
		Vec2 a = pol.m_centroid;
		split(a, dmgPos);
	}
	
	/**
	 * Split according to (AB), assuming a and b are in this entity's local space
	 * @param a
	 * @param b
	 */
	public void split(Vec2 a, Vec2 b) {
		if(maxHealth < .2F || !(behaviour instanceof SolidBehaviour)) {
			delete();
			List<Entity> copy = new ArrayList<>(childs);
			for(Entity c : copy) c.breakApart();
			return;
		}
		Vec2 tangent = b.sub(a);
		tangent.normalize();
		Vec2 normal = new Vec2(-tangent.y, tangent.x);
		if(normal.x < 0) normal.negateLocal();
		// normal should point right
		PolygonShape poly = (PolygonShape) fdef.shape;
		Vec2[] verts = poly.m_vertices;
		int n = poly.m_count;
		List<Vec2> vertsR = new ArrayList<>();
		List<Vec2> vertsL = new ArrayList<>();
		Vec2 iUp = null, iDown = null;
		for(int j = 0; j < n; j++) {
			if(Vec2.dot(normal, verts[j].sub(a)) > 0) vertsR.add(verts[j]);
			else vertsL.add(verts[j]);
			Vec2 inter = inter(a, b, verts[(j+1)%n], verts[j]);
			if(iUp == null) iUp = inter;
			else if(iDown == null) iDown = inter;
		}
		if(iDown.y > iUp.y) {
			Vec2 inter = iUp;
			iUp = iDown;
			iDown = inter;
		}
		vertsL.add(iUp);
		vertsL.add(iDown);
		vertsR.add(iDown);
		vertsR.add(iUp);
		int nL = vertsL.size();
		int nR = vertsR.size();
		Entity eL = tryAddingBody(vertsL.toArray(new Vec2[nL]), nL);
		Entity eR = tryAddingBody(vertsR.toArray(new Vec2[nR]), nR);
		for(Entity c : childs) {
			Entity e = Vec2.dot(normal, body.getLocalPoint(c.body.getWorldPoint(c.anchorPoint)).sub(a)) > 0 ? eR : eL;
			if(e != null) {
				c.jointDef.bodyB = e.body;
				c.parentJoint = game.addJoint(c.jointDef);
				c.parent = e;
				e.childs.add(c);
			} else {
				c.delete();
			}
		}
		if(parent != null) {
			Entity e = Vec2.dot(normal, anchorPoint.sub(a)) > 0 ? eR : eL;
			if(e != null) {
				e.jointDef = jointDef;
				e.jointDef.bodyA = e.body;
				e.parentJoint = game.addJoint(e.jointDef);
				e.parent = parent;
				e.anchorPoint = anchorPoint;
				parent.childs.add(e);
			}
		}
		delete();
	}
	
	private Entity tryAddingBody(Vec2[] verts, int n) {
		if(n > 2 && n < Settings.maxPolygonVertices) {
			PolygonShape shape = new PolygonShape();
			shape.set(verts, n);
			FixtureDef fdefNew = new FixtureDef();
			fdefNew.density = fdef.density;
			fdefNew.filter = fdef.filter;
			fdefNew.restitution = fdef.restitution;
			fdefNew.friction = fdef.friction;
			fdefNew.shape = shape;
			Vec2 pos = body.getPosition();
			float angle = body.getAngle();
			Entity e = new Entity(owner, bdef, fdefNew).color(color).behaviour(behaviour).hardness(hardness);
			e.maxHealth = maxHealth / 2;
			game.addEntity(e);
			e.body.setTransform(pos, angle);
			e.body.setAngularVelocity(body.getAngularVelocity());
			e.body.setLinearVelocity(body.getLinearVelocity());
			return e;
		}
		return null;
	}
	
	/**
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return (ab)inter[cd]
	 */
	private Vec2 inter(Vec2 a, Vec2 b, Vec2 c, Vec2 d) {
		Vec2 u = d.sub(c);
		Vec2 v = b.sub(a);
		Vec2 vorth = new Vec2(-v.y, v.x);
		if(Vec2.dot(u, vorth) == 0) return null;
		float t = Vec2.dot(a.sub(c), vorth)/Vec2.dot(u, vorth);
		if(t >= 0 && t <= 1) return c.add(u.mul(t));
		return null;
	}
	
	public void delete() {
		game.removeEntity(this);
		if(parent != null) parent.childs.remove(this);
	}

	public void draw(List<Drawable> toDraw) {
		if(body == null) return;
		Fixture fix = body.m_fixtureList;
		Vec2 p = body.getTransform().p;
		Rot q = body.getTransform().q;
		AffineTransform trans = new AffineTransform(q.c, q.s, -q.s, q.c, p.x, p.y);
		if(behaviour.renderBody()) {
			while(fix != null) {
				Shape s;
				switch(fix.m_shape.getType()) {
				case CIRCLE:
					s = null;
					// TODO
					break;
					
				case POLYGON:
					PolygonShape polyPhys = (PolygonShape) fix.m_shape;
					Path2D path = new Path2D.Float();
					for(int i = 0; i < polyPhys.m_count; i ++) {
						int i2 = (i+1) % polyPhys.m_count;
						path.append(new Line2D.Float(polyPhys.m_vertices[i].x, polyPhys.m_vertices[i].y, polyPhys.m_vertices[i2].x, polyPhys.m_vertices[i2].y), true);
					}
					path.closePath();
					s = path;
					break;
				default:
					s = null;
					break;
				}
				if(s != null) {
					Color c = color;
					for(int i = (int) ceil(health*4/maxHealth); i < 4; i++) {
						c = c.darker();
					}
					toDraw.add(new DShape(s, c, trans).level(level));
					toDraw.add(new DShape(s, Color.BLACK, trans, .01F).level(level));
				}
				fix = fix.m_next;
			}
		} else {
			behaviour.draw(toDraw, trans);
		}
		for(Wire w : wires) w.draw(toDraw);
	}
	
	public void damage(float dmg, Vec2 loc) {
		damageCenter = damageCenter.add(loc.mul(dmg));
		health -= dmg;
	}
	
	public void addWire(Wire w) {
		wires.add(w);
	}

	@Override
	public AffineTransform getWireTransform() {
		Vec2 p = body.getTransform().p;
		Rot q = body.getTransform().q;
		return new AffineTransform(q.c, q.s, -q.s, q.c, p.x, p.y);
	}
	
	public Entity level(int level) {
		this.level = level;
		return this;
	}
	
	/**
	 * @param e The parent to test
	 * @return if e is a parent of this entity
	 */
	public boolean isParent(Entity e) {
		return parent != null && (parent == e || parent.isParent(e));
	}
	
	public Entity getSuperParent() {
		if(parent != null) return parent.getSuperParent();
		return this;
	}
	
	public float getDescendingMass() {
		float mass = body.getMass();
		for(Entity c : childs) {
			mass += c.getDescendingMass();
		}
		return mass;
	}
	
	public float getTotalMass() {
		return getSuperParent().getDescendingMass();
	}
}
