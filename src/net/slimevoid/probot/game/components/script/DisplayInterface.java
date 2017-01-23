package net.slimevoid.probot.game.components.script;

import net.slimevoid.probot.game.components.DisplayBehaviour;

public class DisplayInterface implements ScriptInterface {

	private DisplayBehaviour db;
	
	public DisplayInterface(DisplayBehaviour db) {
		this.db = db;
	}
	
	@Override
	public String getName() {
		return db.compName;
	}
	
	public void print(char c) {
		db.print(c);
	}
	
	public void print(String txt) {
		db.print(txt);
	}
	
	public void println(String txt) {
		db.println(txt);
	}
}
