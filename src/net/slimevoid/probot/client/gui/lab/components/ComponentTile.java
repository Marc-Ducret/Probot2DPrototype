package net.slimevoid.probot.client.gui.lab.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.slimevoid.probot.client.gui.lab.GuiLabEditor;
import net.slimevoid.probot.client.gui.lab.tool.ToolComponent;
import net.slimevoid.probot.game.components.CompDB.CompEntry;
import net.slimevoid.probot.render.DSVG;

public class ComponentTile extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L;
	
	public final CompEntry comp;
	public final GuiLabEditor lab;
	
	public ComponentTile(CompEntry comp, GuiLabEditor lab) {
		this.comp = comp;
		this.lab = lab;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		addLabel(new JLabel(createIcon(comp, 64)));
		addLabel(new JLabel(comp.getString("name")));
		addLabel(new JLabel("$"+comp.getInt("price")));
		setBorder(new EmptyBorder(1, 1, 1, 1));
		addMouseListener(this);
	}
	
	public void addLabel(JLabel label) {
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(label);
	}

	private Icon createIcon(CompEntry comp, int size) {
		DSVG dsvg = new DSVG(comp.getString("icon"), size, 0, false);
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.translate(size/2, size/2);
		dsvg.draw(g2d);
		return new ImageIcon(img);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		lab.changeTool(new ToolComponent(comp, lab));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		setBorder(new LineBorder(Color.GRAY));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		setBorder(new EmptyBorder(1, 1, 1, 1));
	}
}
