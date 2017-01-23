package net.slimevoid.probot.client.gui.play;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import net.slimevoid.probot.client.ProbotClient;
import net.slimevoid.probot.client.gui.Gui;
import net.slimevoid.probot.client.gui.GuiScreen;
import net.slimevoid.probot.client.gui.lab.Blueprint;
import net.slimevoid.probot.game.Entity;
import net.slimevoid.probot.game.Game;
import net.slimevoid.probot.game.components.CPUBehaviour;
import net.slimevoid.probot.game.components.script.PlayerInterface;
import net.slimevoid.probot.game.player.PlayerControllerBuffred;
import net.slimevoid.probot.game.player.PlayerControllerLocal;
import net.slimevoid.probot.network.p2p.InputData;
import net.slimevoid.probot.network.p2p.P2PSocket;

public class GuiPlay extends Gui {
	
	private static final long serialVersionUID = 1L;
	public final P2PSocket socket;
	public GuiScreen screen;
	public Game game;
	public PlayerControllerBuffred pcme;
	public PlayerControllerBuffred pchim;
	private PlayerControllerLocal pclocal;
	private int pIDme;
	private int pIDhim;
	private int tickCTInput;
	private int tickCTGame;
	public long timeStart; // SYNC relative
	
	public GuiPlay(P2PSocket socket, boolean first, List<Blueprint> rMe, List<Blueprint> rHim) throws ClassNotFoundException, FileNotFoundException, IOException {
		this.socket = socket;
		pclocal = new PlayerControllerLocal(-1);
		screen = new GuiScreen();
		tickCTInput = 0;
		tickCTGame = 0;
		timeStart = -1;
		game = new Game();
		game.init();
		List<Blueprint> bps1 = first ? rMe  : rHim;
		List<Blueprint> bps2 = first ? rHim : rMe ;
		int id1  = game.addRobot(bps1);
		int id2  = game.addRobot(bps2);
		pIDme  = first ? id1 : id2;
		pIDhim = first ? id2 : id1;
		pcme  = new PlayerControllerBuffred(pIDme );
		pchim = new PlayerControllerBuffred(pIDhim);
		game.addBufferedEnts();
		for(Entity e : game.entities) {
			if(e.behaviour instanceof CPUBehaviour) {
				CPUBehaviour cpub = (CPUBehaviour) e.behaviour;
				if(e.owner == pIDme ) cpub.debugi.setPlayerController(pcme );
				if(e.owner == pIDhim) cpub.debugi.setPlayerController(pchim);
			}
		}
		
		add(screen);
		layout.putConstraint(NORTH, screen, 0, NORTH, this);
		layout.putConstraint(SOUTH, screen, 0, SOUTH, this);
		layout.putConstraint(EAST , screen, 0, EAST , this);
		layout.putConstraint(WEST , screen, 0, WEST , this);
		screen.init();
	}
	
	@Override
	public void tick(float dt) {
		super.tick(dt);
		screen.tick(dt);
		socket.update();
		if(hasGameStarted()) {
			if(tickCTInput == 0) System.out.println("Game started!!");
			if(tickCTInput % 60 == 0) {
//				System.out.println("I:"+tickCTInput / 60+" G:"+(tickCTGame/60)+" T:"+(tickIDBasedOnTime()/60)+" ("+socket.timeOffsetEst+")");
			}
			while(pcme.ready(tickCTInput) && pchim.ready(tickCTInput)) {
				pcme.pop();
				pchim.pop();
				game.tick(dt);
				tickCTGame++;
			}
			if(tickCTInput - tickCTGame < 15) {
				tickCTInput++;
				bufferLocalKeyStates();
			}
		}
		if(socket.getGameTime() - (timeStart+tickCTInput*(int)(ProbotClient.DT*1000)) < 50) { // if not need to catch up
			screen.rEngine.draw(game, screen);
			screen.repaint();
		}
	}
	
	public void bufferLocalKeyStates() {
		boolean[] state = new boolean[PlayerInterface.KEYS.length];
		for(int i = 0; i < state.length; i ++) {
			state[i] = pclocal.getKeyState(PlayerInterface.KEYS[i]);
		}
		InputData input = new InputData(state, tickCTInput);
		pcme.add(input);
		socket.sendInput(input);
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		pclocal.updateKey(e.getKeyCode(), true);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		pclocal.updateKey(e.getKeyCode(), false);
		switch(e.getKeyCode()) {
		default:
			break;
		}
	}
	
	@Override
	public long waitTime(long tickTime) {
		if(!hasGameStarted()) return super.waitTime(tickTime);
		return timeStart+(tickCTInput)*(long)(ProbotClient.DT*1000)-socket.getGameTime();
	}
	
	public int tickIDBasedOnTime() {
		if(!hasGameStarted()) return -1;
		return  (int) ((socket.getGameTime() - timeStart)/((int)(ProbotClient.DT*1000)));
	}
	
	public boolean hasGameStarted() {
		return timeStart >= 0 && socket.getGameTime() >= timeStart;
	}
}
