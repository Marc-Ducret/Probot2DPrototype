package net.slimevoid.probot.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jbox2d.callbacks.ContactFilter;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;

import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.render.Drawable;
import net.slimevoid.probot.render.RenderObject;

public class Game implements RenderObject {

	public World world;
	public List<Entity> entities;
	public List<Entity> entitiesToAdd;
	public List<Entity> entitiesToRm;
	public Map<Body, Entity> bodyMap;
	private int lastRobotIndex;
	public Random rand;
	
	public Game() {
		entities = new ArrayList<>();
		entitiesToAdd = new ArrayList<>();
		entitiesToRm = new ArrayList<>();
		bodyMap = new HashMap<>();
		rand = new Random(42);
	}
	
	public void init() {
		world = new World(new Vec2());
		world.setContactFilter(new ContactFilter() {
			@Override
			public boolean shouldCollide(Fixture fA, Fixture fB) {
				Entity eA = findEntByBody(fA.m_body);
				Entity eB = findEntByBody(fB.m_body);
				return !(eA.isParent(eB) || eB.isParent(eA));
			}
		});
		world.setContactListener(new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				float power = 0;
				Fixture f1;
				Fixture f2;
				Entity e1;
				Entity e2;
				Manifold m = contact.getManifold();
				if(m.type == Manifold.ManifoldType.FACE_A) {
					f1 = contact.m_fixtureA;
					f2 = contact.m_fixtureB;
				} else {
					f1 = contact.m_fixtureB;
					f2 = contact.m_fixtureA;
				}
				e1 = findEntByBody(f1.getBody());
				e2 = findEntByBody(f2.getBody());
				Vec2 dmgPoint = new Vec2();
				WorldManifold wm = new WorldManifold();
				contact.getWorldManifold(wm);
				Vec2 p = wm.points[0];
				Vec2 wP = e2.body.getWorldPoint(p);
				float dpower = e1.body.getLinearVelocityFromWorldPoint(wP).sub(e2.body.getLinearVelocityFromWorldPoint(wP)).lengthSquared();
				power += dpower;;
				dmgPoint = p;
				power *= .00000001F;
				e1.damage(power*e2.hardness/e1.hardness, e1.body.getLocalPoint(dmgPoint));
				e2.damage(power*e1.hardness/e2.hardness, e2.body.getLocalPoint(dmgPoint));
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				
			}
			
			@Override
			public void endContact(Contact contact) {
			}
			
			@Override
			public void beginContact(Contact contact) {
			}
		});
		lastRobotIndex = 1;
		entities.clear();
		entitiesToAdd.clear();
		entitiesToRm.clear();
	}
	
	public void tick(float dt) {
		world.step(dt, 6, 3);
		for(Entity e : entities) {
			e.tick(dt);
		}
		addBufferedEnts();
	}
	
	public void addBufferedEnts() {
		entities.addAll(entitiesToAdd);
		entitiesToAdd.clear();
		entities.removeAll(entitiesToRm);
		for(Entity e : entitiesToRm) bodyMap.remove(e.body);
		entitiesToRm.clear();
	}
	
	public void addEntity(Entity e) {
		entitiesToAdd.add(e);
		e.enterWorld(world, this);
		bodyMap.put(e.body, e);
	}
	
	public void removeEntity(Entity e) {
		entitiesToRm.add(e);
		world.destroyBody(e.body);
	}

	@Override
	public void draw(List<Drawable> toDraw) {
		for(Entity e : entities) e.draw(toDraw);
	}
	
	public Entity findEntByBody(Body b) {
		return bodyMap.get(b);
	}

	public Joint addJoint(JointDef jd) {
		return world.createJoint(jd);
	}
	
	public int addRobot(List<Blueprint> bps) {
		int robotIndex = lastRobotIndex++;
		for(Blueprint bp : bps) {
			bp.enterGame(this, robotIndex);
		}
		for(Blueprint bp : bps) {
			bp.initEntity(this, robotIndex);
		}
		return robotIndex;
	}
}
