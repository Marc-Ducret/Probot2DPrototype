package net.slimevoid.probot.client.gui.lab;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.slimevoid.probot.game.Material;
import net.slimevoid.probot.game.components.Behaviour;
import net.slimevoid.probot.render.Drawable;
import net.slimevoid.probot.render.RenderObject;

public abstract class BPProperties implements InternalFrameListener, RenderObject, Serializable {
	
	private static final long serialVersionUID = 1L;
	public Material mat;
	public Blueprint bp;
	private transient JInternalFrame frame;
	
	public BPProperties(Material mat) {
		this.mat = mat;
	}
	
	public void setBp(Blueprint bp) {
		this.bp = bp;
	}
	
	public void editProps(GuiLabEditor lab) {
		if(frame != null) {
			try {
				frame.setClosed(true);
			} catch (PropertyVetoException e) {
			}
		}
		frame = new JInternalFrame("Props", false, true, false, false);
		JPanel pan = new JPanel();
		frame.getContentPane().add(pan);
		populatePropsFrame(pan);
		lab.screen.add(frame);
		frame.setVisible(true);
		frame.pack();
		frame.setLocation(lab.screen.getWidth() / 2 - frame.getWidth() / 2, lab.screen.getHeight() / 2 - frame.getHeight() / 2);
		frame.addInternalFrameListener(this);
	}
	
	public abstract JPanel populatePropsFrame(JPanel pan);
	public abstract Behaviour createBehaviour();
	
	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
	}
	
	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
	}
	
	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
	}
	
	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
	}
	
	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
	}
	
	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
	}
	
	@Override
	public void draw(List<Drawable> toDraw) {
	}
}
