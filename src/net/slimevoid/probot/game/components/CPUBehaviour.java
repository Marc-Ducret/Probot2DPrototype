package net.slimevoid.probot.game.components;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

import net.slimevoid.probot.game.Entity;
import net.slimevoid.probot.game.components.CompDB.CompEntry;
import net.slimevoid.probot.game.components.script.CPUInterface;
import net.slimevoid.probot.game.components.script.PlayerInterface;
import net.slimevoid.probot.game.components.script.ScriptInterface;

public class CPUBehaviour extends ComponentBehaviour {

	private String code;
	private ScriptEngine engine;
	public double time;
	public final PlayerInterface debugi;
	
	static {
		ContextFactory cf = new ContextFactory() {
			@Override
			protected void observeInstructionCount(Context cx, int instructionCount) {
				super.observeInstructionCount(cx, instructionCount);
				if(instructionCount > cx.getInstructionObserverThreshold()) throw new RuntimeException();
			}
			
			@Override
			protected Context makeContext() {
				Context ctx = super.makeContext();
				ctx.setOptimizationLevel(6   );
				ctx.setInstructionObserverThreshold(10000);
				return ctx;
			}
		};
		ContextFactory.initGlobal(cf);
	}
	
	public CPUBehaviour(CompEntry comp, String compName, String code) {
		super(comp, compName);
		this.code = code;
		scriptI = new CPUInterface(this);
		debugi = new PlayerInterface();
	}

	public void init() {
		ScriptContext ctx = new SimpleScriptContext();
		ScriptEngineManager m = new ScriptEngineManager();
		engine = m.getEngineByName("rhino");
		Bindings binds = new SimpleBindings();
		binds.put(scriptI.getName(), scriptI);
		binds.put(debugi.getName(), debugi);
		for(Wire w : owner.wires) {
			if(w.type == WireType.SCRIPT) {
				ScriptInterface scriptI = ((ComponentBehaviour)((Entity)w.src).behaviour).scriptI;
				binds.put(scriptI.getName(), scriptI);
			}
		}
		ctx.setBindings(binds, ScriptContext.ENGINE_SCOPE);
		engine.setContext(ctx);
	}
	
	@Override
	public void tick(double dt) {
		try {
			engine.eval(code);
		} catch (Exception e) {}
		time += dt;
	}
	
	@Override
	public void onWireRemoved(Wire w) {
		Bindings binds = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		super.onWireRemoved(w);
		if(w.type == WireType.SCRIPT) {
			ScriptInterface scriptI = ((ComponentBehaviour)((Entity)w.src).behaviour).scriptI;
			binds.remove(scriptI.getName());
		}
	}
}
