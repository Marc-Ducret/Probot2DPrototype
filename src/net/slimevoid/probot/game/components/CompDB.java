package net.slimevoid.probot.game.components;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slimevoid.probot.client.gui.lab.tool.PlaceType;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.JointType;

public class CompDB {
	
	public static class CompEntry implements Serializable {
		
		private static final long serialVersionUID = 1L;
		private Map<String, Object> props;
		
		private CompEntry(Object...objs) {
			props = new HashMap<>();
			for(int i = 0; i < objs.length / 2; i ++) {
				setProp((String)objs[i*2], objs[i*2+1]);
			}
		}
		
		private void setProp(String key, Object value) {
			props.put(key, value);
		}
		
		public Object getProp(String key) {
			if(props.containsKey(key)) 		return props.get(key);
			else if(this != defaultComp) 	return defaultComp.getProp(key);
			else 							throw new IllegalArgumentException("Prop {"+key+"} undefined in "+this);
		}
		
		public <T> T getProp(String key, Class<T> type) {
			Object prop = getProp(key);
			if(type.isInstance(prop)) return type.cast(prop);
			throw new IllegalArgumentException("Prop {"+key+"} ("+prop+") can't be casted to "+type.getSimpleName()+" in "+this);
		}
		
		public String getString(String key) {
			return getProp(key, String.class);
		}
		
		public float getFloat(String key) {
			return getProp(key, Float.class);
		}
		
		public int getInt(String key) {
			return getProp(key, Integer.class);
		}
		
		public Vec2 getVec2(String key) {
			return getProp(key, Vec2.class);
		}
		
		@Override
		public String toString() {
			return getString("name")+"["+getString("type")+"]";
		}
	}
	
	private static CompEntry defaultComp;
	private static List<CompEntry> comps = new ArrayList<>();
	
	static {
		defaultComp = new CompEntry(
				"name", "NAMELESS_COMP",
				"type", "TYPELESS_COMP",
				"jointType", JointType.WELD,
				"jointAxis", new Vec2(),
				"placeType", PlaceType.FREE);
		
		comps.add(new CompEntry(
				"name", "CPU",
				"icon", "CPU",
				"size", .2F,
				"price", 100,
				"type", "CPU"));
		comps.add(new CompEntry(
				"name", "CPU2",
				"icon", "CPU",
				"size", .2F,
				"price", 100,
				"type", "CPU"));
		comps.add(new CompEntry(
				"name", "CPU3",
				"icon", "CPU",
				"size", .2F,
				"price", 100,
				"type", "CPU"));
		comps.add(new CompEntry(
				"name", "LCD 10x4",
				"icon", "LCD",
				"size", .4F,
				"price", 35,
				"type", "Display",
				"displayRows", 4,
				"displayCols", 10,
				"charW", 4,
				"charH", 10,
				"displayFont", new Font("Consolas", 1, 1),
				"displayColor", Color.black));
		comps.add(new CompEntry(
				"name", "DC Motor",
				"icon", "Motor",
				"size", .1F,
				"price", 20,
				"type", "Motor",
				"jointType", JointType.REVOLUTE));
		comps.add(new CompEntry(
				"name", "Wheel",
				"icon", "Wheel",
				"size", .2F,
				"price", 20,
				"type", "Wheel",
				"wheelR", .1F,
				"wheelW", .1F,
				"placeType", PlaceType.EDGE,
				"placeEdgeNormal", new Vec2(1, 0),
				"placeEdgeNDist", .05F));
		comps.add(new CompEntry(
				"name", "Laser",
				"icon", "Laser",
				"size", .3F,
				"price", 1000,
				"type", "Laser"));
	}
	
	public static List<CompEntry> getComps() {
		return comps;
	}
	
	public static List<CompEntry> getComps(String type) {
		List<CompEntry> list = new ArrayList<>();
		for(CompEntry ce : comps) {
			if(ce.getString("type").equalsIgnoreCase(type)) {
				list.add(ce);
			}
		}
		return list;
	}
	
	public static List<String> getTypes() {
		List<String> list = new ArrayList<>();
		for(CompEntry ce : comps) {
			String type = ce.getString("type");
			if(!list.contains(type)) {
				list.add(type);
			}
		}
		return list;
	}
}
