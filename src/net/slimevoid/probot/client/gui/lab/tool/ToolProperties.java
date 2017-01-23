package net.slimevoid.probot.client.gui.lab.tool;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import net.slimevoid.probot.client.gui.lab.GuiLabEditor;

public class ToolProperties extends ToolAbstract {

	public ToolProperties(String name, GuiLabEditor lab) {
		super(name, lab);
		cursorOnBP = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mouseClicked(e);
		if(underMouse != null) {
			underMouse.props.editProps(lab);
		}
	}
}
