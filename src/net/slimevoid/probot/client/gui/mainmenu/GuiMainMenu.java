package net.slimevoid.probot.client.gui.mainmenu;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import net.slimevoid.probot.client.PlayNetClient;
import net.slimevoid.probot.client.ProbotClient;
import net.slimevoid.probot.client.gui.Gui;
import net.slimevoid.probot.client.gui.lab.GuiLabEditor;
import net.slimevoid.probot.client.gui.lab.RobotSelector;
import net.slimevoid.probot.client.gui.lab.RobotSelector.Robot;
import net.slimevoid.probot.client.gui.lab.RobotSelector.SelectListener;
import net.slimevoid.probot.network.packet.Packet04PlayRequest;

public class GuiMainMenu extends Gui {

	private static final long serialVersionUID = 1L;
	public static final int IMG_W = 1280;
	public static final int IMG_H = 720;
	
	private enum MenuState {STILL, FIGHT, BUILD, OPTIONS, SUB}
	
	private BufferedImage[] menuImg;
	private Rectangle[] zones;
	private MenuState state;
	private Gui subG;
	
	public GuiMainMenu() {
		menuImg = new BufferedImage[MenuState.values().length];
		for(int i = 0; i < MenuState.values().length; i++) {
			MenuState s = MenuState.values()[i];
			try {
				menuImg[i] = ImageIO.read(ClassLoader.getSystemResourceAsStream("menu/menu_"+s.name().toLowerCase()+".png"));
			} catch (Exception e) {
				System.out.println("No menu image for state "+s);
				menuImg[i] = new BufferedImage(IMG_W, IMG_H, BufferedImage.TYPE_INT_ARGB);
			}
		}
		state = MenuState.STILL;
		zones = new Rectangle[MenuState.values().length];
		zones[MenuState.STILL.ordinal()]   = new Rectangle(0, 0, IMG_W, IMG_H);
		zones[MenuState.FIGHT.ordinal()]   = new Rectangle(635, 330, 215, 90);
		zones[MenuState.BUILD.ordinal()]   = new Rectangle(635, 435, 215, 90);
		zones[MenuState.OPTIONS.ordinal()] = new Rectangle(1100, 560, 110, 110);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE && state == MenuState.SUB) {
			stopDisplayingSub();
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		if(state == MenuState.SUB) return;
		MenuState newState = null;
		for(MenuState s : MenuState.values()) {
			if(zones[s.ordinal()] != null && zones[s.ordinal()].contains(e.getPoint())) {
				newState = s;
			}
		}
		if(newState != null && newState != state) {
			state = newState;
			repaint();
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if(state == MenuState.SUB) return;
		switch(state) {
		case BUILD:
			displayRobotSelector(new SelectListener() {
				@Override
				public void onSelect(Robot r) {
					GuiLabEditor lab = new GuiLabEditor();
					lab.fileName = r.name+".probot";
					lab.bps = r.bps;
					ProbotClient.instance.changeGui(lab);
				}
			}, new File("."));
			break;
		case FIGHT:
			displayMessage("Connecting to server");
			final PlayNetClient client = PlayNetClient.startNet(this);
			new Thread(new Runnable() {
				public void run() {
					if(client.connect("192.168.1.43", 8004)) {
						displayRobotSelector(new SelectListener() {
							@Override
							public void onSelect(Robot r) {
								displayMessage("PlayRequest with "+r.name);
								client.net.server.sendPacket(new Packet04PlayRequest(r.bps));
								client.robot = r.bps;
							}
						}, new File("."));
						client.run();
					} else {
						displayMessage("Connection unsucessful");
					}
				}
			}).start();
			break;
		case OPTIONS:
			displayMessage("Options");
			break;
		default:
			break;
		}
	}
	
	public void displayRobotSelector(SelectListener listener, File dir) {
		RobotSelector rSel = new RobotSelector(listener);
		try {
			rSel.loadRobotsFrom(dir);
			displaySubGui(rSel);
		} catch (Exception ex) {
			displayMessage("Error while loading robots "+ex);
		}
	}
	
	public void displayMessage(String msg) {
		displaySubGui(new GuiSubMessage(msg));
	}
	
	public void displaySubGui(Gui g) {
		if(subG != null) removeMouseListener(subG);
		addMouseListener(g);
		subG = g;
		subG.setSize(getWidth(), getHeight());
		changeState(MenuState.SUB);
	}
	
	public void stopDisplayingSub() {
		changeState(MenuState.STILL);
	}
	
	private void changeState(MenuState state) {
		this.state = state;
		repaint();
	}
	
	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.drawImage(menuImg[state.ordinal()], 0, 0, null);
		super.paint(graphics);
		if(state == MenuState.SUB && subG != null) {
			subG.paint(g);
		}
	}
	
	@Override
	public void tick(float dt) {
		super.tick(dt);
	}
}