package net.slimevoid.probot.client.gui;

import static java.lang.Math.max;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JDesktopPane;
import javax.swing.SpringLayout;

import net.slimevoid.probot.client.ProbotClient;

public abstract class Gui extends JDesktopPane implements KeyListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;
	
	protected final SpringLayout layout;
	
	public Gui() {
		this(true);
	}
	
	public Gui(boolean changeLay) {
		setOpaque(false);
		if(changeLay) {
			layout = new SpringLayout();
			setLayout(layout);
		} else {
			layout = null;
		}
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void onDisplay() {}
	
	public void tick(float dt) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
	}

	public long waitTime(long timeTick) {
		return max(0, (long)(1000*ProbotClient.DT) - timeTick);
	}
}
