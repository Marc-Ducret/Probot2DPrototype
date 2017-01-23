package net.slimevoid.probot.game.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.util.List;

import net.slimevoid.probot.game.components.CompDB.CompEntry;
import net.slimevoid.probot.game.components.script.DisplayInterface;
import net.slimevoid.probot.render.DShape;
import net.slimevoid.probot.render.Drawable;

public class DisplayBehaviour extends ComponentBehaviour {
	
	private char[] content;
	private int curRow;
	private int curCol;

	public DisplayBehaviour(CompEntry comp, String compName) {
		super(comp, compName);
		scriptI = new DisplayInterface(this);
	}
	
	@Override
	public void init() {
		super.init();
		content = new char[comp.getInt("displayRows") * comp.getInt("displayCols")];
		for(int i = 0; i < content.length; i ++) content[i] = ' ';
	}
	
	public void print(char c) {
		content[curRow * comp.getInt("displayCols") + curCol] = c;
		curCol ++;
		if(curCol >= comp.getInt("displayCols")) {
			curCol = 0;
			curRow ++;
		}
		if(curRow >= comp.getInt("displayRows")) {
			curRow = 0;
		}
	}
	
	public void print(String txt) {
		for(int i = 0; i < txt.length(); i ++) print(txt.charAt(i));
	}
	
	public void println(String txt) {
		print(txt);
		while(curCol != 0) print(' ');
	}

	@Override
	public void tick(double dt) {
	}
	
	@Override
	public void draw(List<Drawable> toDraw, AffineTransform trans) {
		super.draw(toDraw, trans);
		for(int i = 0; i < content.length; i ++) {
			if(content[i] == ' ') continue;
			float x = (i % comp.getInt("displayCols") + .5F - comp.getInt("displayCols") / 2F) * 4.75F - 2;//comp.getInt("charW");
			float y = (i / comp.getInt("displayCols") + .5F - comp.getInt("displayRows") / 2F) * 8 + 1;//comp.getInt("charH");
			GlyphVector gv = comp.getProp("displayFont", Font.class).deriveFont(7F).createGlyphVector(new FontRenderContext(null, false, false), new char[]{content[i]});
			AffineTransform tr = new AffineTransform(trans);
			float size = comp.getFloat("size") * .02F;
			tr.scale(size, size);
			tr.translate(x, y);
			toDraw.add(new DShape(gv.getOutline(), comp.getProp("displayColor", Color.class), tr).level(owner.level+1));
		}
	}
}
