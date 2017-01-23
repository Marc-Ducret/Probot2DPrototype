package net.slimevoid.probot.client.gui.lab;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;

import net.slimevoid.probot.client.ProbotClient;
import net.slimevoid.probot.client.gui.Gui;
import net.slimevoid.probot.client.gui.GuiScreen;
import net.slimevoid.probot.game.Entity;
import net.slimevoid.probot.game.Game;
import net.slimevoid.probot.game.components.CPUBehaviour;
import net.slimevoid.probot.game.player.PlayerControllerLocal;

public class GuiLabTest extends Gui {

	private static final long serialVersionUID = 1L;
	public GuiScreen screen;
	public Game game;
	public GuiLabEditor lab;
	private PlayerControllerLocal playerController;
	
	public GuiLabTest(GuiLabEditor lab) {
		this.lab = lab;
		playerController = new PlayerControllerLocal(0);
		screen = new GuiScreen();
		
		game = new Game();
		game.init();
		try {
			game.addRobot(GuiLabEditor.load(new FileInputStream(new File("arena.probot"))));
		} catch (Exception e1) {
		}
		game.addRobot(lab.bps);
		game.addBufferedEnts();
		for(Entity e : game.entities) {
			if(e.behaviour instanceof CPUBehaviour) {
				CPUBehaviour cpub = (CPUBehaviour) e.behaviour;
				cpub.debugi.setPlayerController(playerController);
			}
		}
		
		add(screen);
		layout.putConstraint(NORTH, screen, 0, NORTH, this);
		layout.putConstraint(SOUTH, screen, 0, SOUTH, this);
		layout.putConstraint(EAST, screen, 0, EAST, this);
		layout.putConstraint(WEST, screen, 0, WEST, this);
	}
	
	@Override
	public void onDisplay() {
		super.onDisplay();
		screen.init();
	}
	
	@Override
	public void tick(float dt) {
		super.tick(dt);
		screen.tick(dt);
		game.tick(dt);
		screen.rEngine.draw(screen, game);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		playerController.updateKey(e.getKeyCode(), true);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		playerController.updateKey(e.getKeyCode(), false);
		switch(e.getKeyCode()) {
		case KeyEvent.VK_SPACE:
			ProbotClient.instance.changeGui(lab);
			break;
		}
	}
}
