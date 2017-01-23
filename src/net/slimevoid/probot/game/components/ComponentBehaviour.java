package net.slimevoid.probot.game.components;

import java.awt.geom.AffineTransform;
import java.util.List;

import net.slimevoid.probot.game.Entity;
import net.slimevoid.probot.game.components.CompDB.CompEntry;
import net.slimevoid.probot.game.components.script.ScriptInterface;
import net.slimevoid.probot.render.DSVG;
import net.slimevoid.probot.render.Drawable;

public abstract class ComponentBehaviour implements Behaviour {

	public final CompEntry comp;
	public final String compName;
	public ScriptInterface scriptI;
	public Entity owner;
	
	public ComponentBehaviour(CompEntry comp, String compName) {
		this.comp = comp;
		this.compName = compName;
	}
	
	public void init() {
	}
	
	@Override
	public Behaviour setOwner(Entity e) {
		this.owner = e;
		return this;
	}
	
	@Override
	public boolean renderBody() {
		return false;
	}
	
	@Override
	public void draw(List<Drawable> toDraw, AffineTransform trans) {
		toDraw.add(new DSVG(comp.getString("icon"), comp.getFloat("size"), owner.level).trans(trans));
	}
	
	@Override
	public void onWireRemoved(Wire w) {
	}
}
