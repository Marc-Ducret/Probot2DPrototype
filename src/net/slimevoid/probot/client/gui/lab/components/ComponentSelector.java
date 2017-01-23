package net.slimevoid.probot.client.gui.lab.components;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.slimevoid.probot.client.gui.lab.GuiLabEditor;
import net.slimevoid.probot.game.components.CompDB;
import net.slimevoid.probot.game.components.CompDB.CompEntry;

public class ComponentSelector extends JPanel {

	private static final long serialVersionUID = 1L;

	private GuiLabEditor lab;
	
	public ComponentSelector(GuiLabEditor lab) {
		this.lab = lab;
		JPanel vertPane = new JPanel();
		vertPane.setLayout(new BoxLayout(vertPane, BoxLayout.Y_AXIS));
		JScrollPane pane = new JScrollPane(vertPane);
		add(pane);
		for(String type : CompDB.getTypes()) {
			JLabel typeLabel = new JLabel("--- "+type+" ---");
			typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			vertPane.add(typeLabel);
			JPanel gridPanel = new JPanel(new GridLayout(0, 3));
			for(CompEntry comp : CompDB.getComps(type)) {
				ComponentTile ct = new ComponentTile(comp, this.lab);
				gridPanel.add(ct);
			}
			vertPane.add(gridPanel);
		}
	}
}
