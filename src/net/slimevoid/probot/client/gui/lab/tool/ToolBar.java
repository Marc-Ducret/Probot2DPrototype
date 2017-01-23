package net.slimevoid.probot.client.gui.lab.tool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.slimevoid.probot.client.gui.lab.GuiLabEditor;
import net.slimevoid.probot.client.gui.lab.tool.ToolAbstract;

public class ToolBar extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public final GuiLabEditor lab;
	public final ToolAbstract[] tools;
	
	public ToolBar(final GuiLabEditor lab, ToolAbstract...tools) {
		this.lab = lab;
		this.tools = tools;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		for(final ToolAbstract tool : tools) {
			JButton button = new JButton(tool.name); 
			add(button);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					lab.changeTool(tool);
				}
			});
		}
	}
}
