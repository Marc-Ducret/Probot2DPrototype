package net.slimevoid.probot.game.components.script;

import java.awt.event.KeyEvent;
import java.util.Arrays;

import net.slimevoid.probot.game.player.PlayerController;

public class PlayerInterface implements ScriptInterface {
	
	public static final int[] KEYS = new int[]{
			KeyEvent.VK_A,
			KeyEvent.VK_B,
			KeyEvent.VK_C,
			KeyEvent.VK_D,
			KeyEvent.VK_E,
			KeyEvent.VK_F,
			KeyEvent.VK_G,
			KeyEvent.VK_H,
			KeyEvent.VK_I,
			KeyEvent.VK_J,
			KeyEvent.VK_K,
			KeyEvent.VK_L,
			KeyEvent.VK_M,
			KeyEvent.VK_N,
			KeyEvent.VK_O,
			KeyEvent.VK_P,
			KeyEvent.VK_Q,
			KeyEvent.VK_R,
			KeyEvent.VK_S,
			KeyEvent.VK_T,
			KeyEvent.VK_U,
			KeyEvent.VK_V,
			KeyEvent.VK_W,
			KeyEvent.VK_X,
			KeyEvent.VK_Y,
			KeyEvent.VK_Z};
	
	static {Arrays.sort(KEYS);}
	
	public final int KEY_A = KeyEvent.VK_A;
	public final int KEY_B = KeyEvent.VK_B;
	public final int KEY_C = KeyEvent.VK_C;
	public final int KEY_D = KeyEvent.VK_D;
	public final int KEY_E = KeyEvent.VK_E;
	public final int KEY_F = KeyEvent.VK_F;
	public final int KEY_G = KeyEvent.VK_G;
	public final int KEY_H = KeyEvent.VK_H;
	public final int KEY_I = KeyEvent.VK_I;
	public final int KEY_J = KeyEvent.VK_J;
	public final int KEY_K = KeyEvent.VK_K;
	public final int KEY_L = KeyEvent.VK_L;
	public final int KEY_M = KeyEvent.VK_M;
	public final int KEY_N = KeyEvent.VK_N;
	public final int KEY_O = KeyEvent.VK_O;
	public final int KEY_P = KeyEvent.VK_P;
	public final int KEY_Q = KeyEvent.VK_Q;
	public final int KEY_R = KeyEvent.VK_R;
	public final int KEY_S = KeyEvent.VK_S;
	public final int KEY_T = KeyEvent.VK_T;
	public final int KEY_U = KeyEvent.VK_U;
	public final int KEY_V = KeyEvent.VK_V;
	public final int KEY_W = KeyEvent.VK_W;
	public final int KEY_X = KeyEvent.VK_X;
	public final int KEY_Y = KeyEvent.VK_Y;
	public final int KEY_Z = KeyEvent.VK_Z;
	
	private PlayerController controller;
	
	public PlayerInterface() {}

	public void setPlayerController(PlayerController controller) {
		this.controller = controller;
	}
	
	public boolean getKeyState(int key) {
		return controller.getKeyState(key);
	}
	
	@Override
	public String getName() {
		return "debug";
	}
}
