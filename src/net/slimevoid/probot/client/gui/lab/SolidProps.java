package net.slimevoid.probot.client.gui.lab;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;

import net.slimevoid.probot.game.Material;
import net.slimevoid.probot.game.SolidBehaviour;
import net.slimevoid.probot.game.components.Behaviour;

public class SolidProps extends BPProperties {

	private static final long serialVersionUID = 1L;

	public SolidProps(Material mat) {
		super(mat);
	}

	@Override
	public JPanel populatePropsFrame(JPanel pan) {
		pan.add(new JLabel("Material"));
		final JComboBox<Material> cbox = new JComboBox<>(Material.values());
		cbox.setSelectedIndex(mat.ordinal());
		cbox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				mat = (Material) cbox.getSelectedItem();
			}
		});
		pan.add(cbox);
		return pan;
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
	}

	@Override
	public Behaviour createBehaviour() {
		return new SolidBehaviour();
	}
}
