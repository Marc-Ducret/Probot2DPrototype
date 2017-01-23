package net.slimevoid.probot.client.gui.lab;

import static java.lang.Math.max;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.slimevoid.probot.client.gui.Gui;
import net.slimevoid.probot.client.gui.mainmenu.GuiMainMenu;
import net.slimevoid.probot.render.Drawable;
import net.slimevoid.probot.utils.AABB;

public class RobotSelector extends Gui {

	private static final long serialVersionUID = 1L;

	private static final int TILE_W = 128;
	private static final int TXT_H  = 32;
	private static final int TILE_H = TILE_W + TXT_H;
	private static final int MARGIN = 16;
	
	public static class Robot {
		private final BufferedImage img;
		public final String name;
		public final List<Blueprint> bps;
		
		private Robot(BufferedImage img, String name, List<Blueprint> bps) {
			this.img = img;
			this.name = name;
			this.bps = bps;
		}
	}
	
	public static interface SelectListener {
		public void onSelect(Robot r);
	}
	
	private List<Robot> robots;
	private SelectListener listener;
	
	public RobotSelector(SelectListener listener) {
		robots = new ArrayList<>();
		this.listener = listener;
	}
	
	public void addRobot(List<Blueprint> bps, String name) {
		BufferedImage img = createImg(bps, TILE_W - MARGIN * 2);
		robots.add(new Robot(img, name, bps));
	}
	
	public void loadRobotsFrom(File dir) throws ClassNotFoundException, FileNotFoundException, IOException {
		assert(dir.isDirectory());
		for(File f : dir.listFiles()) {
			if(f.getName().endsWith(".probot")) {
				addRobot(GuiLabEditor.load(new FileInputStream(f)), f.getName().substring(0, f.getName().length()-".probot".length()));
			}
		}
	}
	
	private BufferedImage createImg(List<Blueprint> bps, int size) {
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.translate(size/2, size/2);
		AABB aabb = GuiLabEditor.getBounds(bps);
		float scale = size/max(aabb.maxX-aabb.minX, aabb.maxY-aabb.minY);
		g2d.scale(scale, scale);
		g2d.translate(-(aabb.maxX+aabb.minX)/2, -(aabb.maxY+aabb.minY)/2);
		List<Drawable> toDraw = new ArrayList<>();
		for(Blueprint bp : bps) {
			bp.draw(toDraw);
		}
		for(Drawable d : toDraw) {
			d.draw(g2d);
		}
		return img;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if(e.getButton() == MouseEvent.BUTTON1) {
			int cols = getWidth() / TILE_W;
			int rows = (robots.size() + cols - 1) / cols;
			if(rows == 1) cols = robots.size();
			int w = cols * TILE_W;
			int h = rows * TILE_H;
			int x0 = GuiMainMenu.IMG_W/2 - w/2;
			int y0 = GuiMainMenu.IMG_H/2 - h/2;
			int x = e.getX() - x0;
			int y = e.getY() - y0;
			if(x >= 0 && x <= w && y >= 0 && y <= h) {
				int i = x / TILE_W + cols * (y / TILE_H);
				if(i < robots.size()) listener.onSelect(robots.get(i));
			}
		}
	}
	
	@Override
	public void paint(Graphics g2d) {
		Graphics2D g = (Graphics2D) g2d;
		super.paint(g);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		Font f = new Font("Consolas", Font.PLAIN, 16);
		g.setFont(f);
		int cols = getWidth() / TILE_W;
		int rows = (robots.size() + cols - 1) / cols;
		if(rows == 1) cols = robots.size();
		int w = cols * TILE_W;
		int h = rows * TILE_H;
		int x0 = GuiMainMenu.IMG_W/2 - w/2;
		int y0 = GuiMainMenu.IMG_H/2 - h/2;
		for(int i = 0; i < robots.size(); i ++) {
			Robot r  = robots.get(i);
			int x = x0 + (i % cols)*TILE_W;
			int y = y0 + (i / cols)*TILE_H;
			g.drawImage(r.img, x+MARGIN, y+MARGIN, null);
			Rectangle2D bounds = f.getStringBounds(r.name, g.getFontRenderContext());
			g.drawString(r.name, x+TILE_W/2-(int)bounds.getWidth()/2, y+TILE_W+TXT_H/2-(int)bounds.getHeight()/2);
		}
	}
}
