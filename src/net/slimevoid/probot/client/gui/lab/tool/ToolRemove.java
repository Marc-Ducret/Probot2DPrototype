package net.slimevoid.probot.client.gui.lab.tool;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import net.slimevoid.probot.client.gui.lab.GuiLabEditor;

public class ToolRemove extends ToolAbstract {

	public ToolRemove(String name, GuiLabEditor lab) {
		super(name, lab);
		cursorOnBP = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if(underMouse != null) lab.bps.remove(underMouse); // TODO adjust repercusions
	}
}
