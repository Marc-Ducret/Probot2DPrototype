package net.slimevoid.probot.client.gui.lab.tool;

import java.awt.Cursor;
import java.awt.event.KeyEvent;

import net.slimevoid.probot.client.gui.lab.GuiLabEditor;

public class ToolScreen extends ToolAbstract {

	private ToolAbstract prevTool;
	
	public ToolScreen(String name, GuiLabEditor lab, ToolAbstract prevTool) {
		super(name, lab);
		this.prevTool = prevTool;
		cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		if(e.getKeyCode() == KeyEvent.VK_CONTROL) lab.changeTool(prevTool);
	}
}
