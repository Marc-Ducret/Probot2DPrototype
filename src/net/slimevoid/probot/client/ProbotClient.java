package net.slimevoid.probot.client;

import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import net.slimevoid.probot.client.gui.Gui;
import net.slimevoid.probot.client.gui.mainmenu.GuiMainMenu;

public class ProbotClient implements KeyEventDispatcher {

	public static ProbotClient instance;
	
	public static final float DT = .016F;
	
	public static void main(String[] args) throws InterruptedException {
		System.setProperty("sun.java2d.transaccel", "True");
	    System.setProperty("sun.java2d.opengl", "True");
	    System.setProperty("sun.java2d.d3d", "True");
	    System.setProperty("sun.java2d.ddforcevram", "True");
		instance = new ProbotClient();
		JFrame frame = initFrame();
		instance.init(frame);
		frame.setVisible(true);
		instance.run();
	}
	
	private static JFrame initFrame() {
		JFrame frame = new JFrame();
		frame.setTitle("Probot!");
		try {
			frame.setIconImage(ImageIO.read(ClassLoader.getSystemResourceAsStream("probotIcon.png")));
		} catch (Exception e) {e.printStackTrace();}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension d = new Dimension(1280, 720);
		frame.getContentPane().setPreferredSize(d);
		frame.pack();
		frame.setLocationRelativeTo(null);
		return frame;
	}
	
	private Gui currentGui;
	private Gui newGui;
	private JPanel container;
	private int frameCT;
	public int fps;
	private long lastFpsCountTime;
	
	private void init(JFrame frame) {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		frame.getContentPane().add(container);
		changeGui(new GuiMainMenu());
		frameCT = 0;
		lastFpsCountTime = System.currentTimeMillis();
	}
	
	public void changeGui(Gui gui) {
		newGui = gui;
	}
	
	private void run() throws InterruptedException {
		while(true) {
			frameCT++;
			if(newGui != null) {
				container.add(newGui);
				if(currentGui != null) container.remove(currentGui);
				currentGui = newGui;
				container.validate();
				container.repaint();
				newGui.onDisplay();
				newGui = null;
			}
			if(currentGui != null) {
				long startTime = System.currentTimeMillis();
				currentGui.tick(DT);
				long timeTick = System.currentTimeMillis() - startTime;
				long waitTime = currentGui.waitTime(timeTick);
				startTime = System.currentTimeMillis();
				if(waitTime > 0) Thread.sleep(waitTime);
			} else {
				Thread.sleep((int) (DT*1000));
			}
			if(System.currentTimeMillis() - lastFpsCountTime >= 1000) {
				fps = frameCT;
				frameCT = 0;
				lastFpsCountTime = 1000 * (System.currentTimeMillis() / 1000);
			}
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		switch(e.getID()) {
		case KeyEvent.KEY_PRESSED:
			if(currentGui != null) currentGui.keyPressed(e);
			break;
		case KeyEvent.KEY_RELEASED:
			if(currentGui != null) currentGui.keyReleased(e);
			break;
		case KeyEvent.KEY_TYPED:
			if(currentGui != null) currentGui.keyTyped(e);
			break;
		}
		return false;
	}
}
