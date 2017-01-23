package net.slimevoid.probot.client.gui.mainmenu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import net.slimevoid.probot.client.gui.Gui;

public class GuiSubMessage extends Gui {
	
	private static final long serialVersionUID = 1L;

	private final String msg;
	
	public GuiSubMessage(String msg) {
		this.msg = msg;
	}
	
	@Override
	public void paint(Graphics g2d) {
		Graphics2D g = (Graphics2D) g2d;
		super.paint(g);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		Font f = new Font("Consolas", Font.PLAIN, 32);
		g.setFont(f);
		Rectangle2D bounds = f.getStringBounds(msg, g.getFontRenderContext());
		g.drawString(msg, GuiMainMenu.IMG_W/2-(int)bounds.getWidth()/2, GuiMainMenu.IMG_H/2-(int)bounds.getHeight()/2);
	}
}
