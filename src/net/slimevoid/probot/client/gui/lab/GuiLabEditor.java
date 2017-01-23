package net.slimevoid.probot.client.gui.lab;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import net.slimevoid.probot.client.ProbotClient;
import net.slimevoid.probot.client.gui.Gui;
import net.slimevoid.probot.client.gui.GuiScreen;
import net.slimevoid.probot.client.gui.lab.components.ComponentSelector;
import net.slimevoid.probot.client.gui.lab.tool.ToolAbstract;
import net.slimevoid.probot.client.gui.lab.tool.ToolAxis;
import net.slimevoid.probot.client.gui.lab.tool.ToolBar;
import net.slimevoid.probot.client.gui.lab.tool.ToolMove;
import net.slimevoid.probot.client.gui.lab.tool.ToolPolygon;
import net.slimevoid.probot.client.gui.lab.tool.ToolProperties;
import net.slimevoid.probot.client.gui.lab.tool.ToolRectangle;
import net.slimevoid.probot.client.gui.lab.tool.ToolRemove;
import net.slimevoid.probot.client.gui.lab.tool.ToolWeld;
import net.slimevoid.probot.client.gui.lab.tool.ToolWire;
import net.slimevoid.probot.client.gui.mainmenu.GuiMainMenu;
import net.slimevoid.probot.render.Drawable;
import net.slimevoid.probot.render.RenderObject;
import net.slimevoid.probot.utils.AABB;

public class GuiLabEditor extends Gui implements RenderObject {

	private static final long serialVersionUID = 1L;
	private static final int FILE_PROTOCOL_CODE = 0x512065DA;

	public GuiScreen screen;
	public ToolBar toolBar;
	public ToolAbstract tool;
	public List<Blueprint> bps;
	public int currentLevel;
	public ComponentSelector compSel;
	public String fileName;
	
	public GuiLabEditor() {
		screen = new GuiScreen();
		bps = new ArrayList<>();
		toolBar = new ToolBar(this, 
				new ToolRectangle("Rect", this),
				new ToolPolygon("Polygon", this),
				new ToolMove("Move", this), 
				new ToolWeld("Weld", this), 
				new ToolAxis("Axis", this), 
				new ToolProperties("Props", this),
				new ToolWire("Wire", this),
				new ToolRemove("Remove", this));
		compSel = new ComponentSelector(this);
		add(screen);
		add(toolBar);
		add(compSel);
		layout.putConstraint(NORTH, screen, 0, SOUTH, toolBar);
		layout.putConstraint(SOUTH, screen, 0, SOUTH, this);
		layout.putConstraint(EAST, screen, 0, WEST, compSel);
		layout.putConstraint(WEST, screen, 0, WEST, this);
		
		layout.putConstraint(NORTH, toolBar, 0, NORTH, this);
		layout.putConstraint(EAST, toolBar, 0, EAST, this);
		layout.putConstraint(WEST, toolBar, 0, WEST, this);
		
		layout.putConstraint(NORTH, compSel, 0, SOUTH, toolBar);
		layout.putConstraint(SOUTH, compSel, 0, SOUTH, this);
		layout.putConstraint(EAST, compSel, 0, EAST, this);
		
		this.tool = toolBar.tools[0];
		resetLevel();
	}
	
	@Override
	public void onDisplay() {
		super.onDisplay();
		screen.init();
		changeTool(tool);
	}
	
	@Override
	public void tick(float dt) {
		super.tick(dt);
		screen.tick(dt);
		tool.tick(dt);
		screen.rEngine.draw(tool, this, screen);
	}

	public void changeTool(ToolAbstract tool) {
		screen.rEngine.getScreen().removeMouseListener(this.tool);
		screen.rEngine.getScreen().removeMouseMotionListener(this.tool);
		screen.rEngine.getScreen().removeKeyListener(this.tool);
		screen.rEngine.getScreen().addMouseListener(tool);
		screen.rEngine.getScreen().addMouseMotionListener(tool);
		screen.rEngine.getScreen().addKeyListener(tool);
		this.tool = tool;
	}

	@Override
	public void draw(List<Drawable> toDraw) {
		for(Blueprint bp : bps) {
			bp.opacity = bp.getLevel() < currentLevel ? 1 : .2F;
			bp.draw(toDraw);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		tool.keyPressed(e);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		super.keyTyped(e);
		tool.keyTyped(e);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		tool.keyReleased(e);
		switch(e.getKeyCode()) {
		case KeyEvent.VK_SPACE:
			ProbotClient.instance.changeGui(new GuiLabTest(this));
			break;
			
		case KeyEvent.VK_S:
			if(e.isControlDown()) {
				try {
					if(fileName == null || e.isShiftDown()) fileName = JOptionPane.showInputDialog("Save File");
					if(fileName != null) {
						if(!fileName.endsWith(".probot")) fileName += ".probot";
						save(new FileOutputStream(new File(fileName)), bps);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			break;
			
		case KeyEvent.VK_L:
			if(e.isControlDown()) {
				try {
					fileName = JOptionPane.showInputDialog("Open File");
					if(fileName != null) {
						if(!fileName.endsWith(".probot")) fileName += ".probot";
						bps = load(new FileInputStream(new File(fileName)));
					}
				} catch (Exception e1) {
				}
			}
			break;
			
		case KeyEvent.VK_ADD:
			levelUp();
			break;
			
		case KeyEvent.VK_SUBTRACT:
			levelDown();
			break;
			
		case KeyEvent.VK_NUMPAD0:
			resetLevel();
			break;
			
		case KeyEvent.VK_ESCAPE:
			ProbotClient.instance.changeGui(new GuiMainMenu());
			break;
		}
	}
	
	public void levelUp() {
		if(currentLevel == Integer.MAX_VALUE) setToMaxLevel();
		currentLevel += Blueprint.LEVEL_GAP;
	}
	
	public void levelDown() {
		if(currentLevel == Integer.MAX_VALUE) setToMaxLevel();
		currentLevel -= Blueprint.LEVEL_GAP;
	}
	
	private void setToMaxLevel() {
		int max = 0;
		for(Blueprint bp : bps) {
			int lvl = bp.getLevel();
			if(lvl > max) max = lvl;
		}
		currentLevel = max + Blueprint.LEVEL_GAP;
	}
	
	public void resetLevel() {
		currentLevel = Integer.MAX_VALUE;
	}
	
	public static void save(OutputStream out, List<Blueprint> bps) throws IOException {
		ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(out));
		oout.writeInt(FILE_PROTOCOL_CODE);
		oout.writeInt(bps.size());
		for(Blueprint bp : bps) oout.writeObject(bp);
		oout.flush();
		if(out instanceof FileOutputStream) {
			oout.close();
		}
	}
	
	public static List<Blueprint> load(InputStream in) throws IOException, ClassNotFoundException {
		List<Blueprint> bps = new ArrayList<>();
		ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
		if(oin.readInt() != FILE_PROTOCOL_CODE) throw new IllegalArgumentException("File protocol unrecognized");
		int size = oin.readInt();
		for(int i = 0; i < size; i ++) {
			Blueprint bp = (Blueprint) oin.readObject();
			bp.init();
			bps.add(bp);
		}
		if(in instanceof FileInputStream) {
			oin.close();
		}
		return bps;
	}
	
	public static AABB getBounds(List<Blueprint> bps) {
		AABB bounds = null;
		for(Blueprint bp : bps) {
			if(bounds == null) bounds = bp.getBounds();
			else bounds = bounds.union(bp.getBounds());
		}
		return bounds;
	}
}
