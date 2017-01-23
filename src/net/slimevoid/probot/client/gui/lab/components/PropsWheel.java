package net.slimevoid.probot.client.gui.lab.components;

import static java.lang.Math.PI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.slimevoid.probot.game.Material;
import net.slimevoid.probot.game.components.Behaviour;
import net.slimevoid.probot.game.components.CompDB.CompEntry;
import net.slimevoid.probot.game.components.WheelBehaviour;
import net.slimevoid.probot.render.DSVG;
import net.slimevoid.probot.render.Drawable;

public class PropsWheel extends PropsComp {

	private static final long serialVersionUID = 1L;
	
	public PropsWheel(CompEntry comp) {
		super(Material.PVC, comp);
		hasScriptInterface = true;
	}
	
	@Override
	public JPanel populatePropsFrame(JPanel pan) {
		pan = super.populatePropsFrame(pan);
		JButton button = new JButton("Reverse");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bp.setAngle((float) (bp.a + PI));
			}
		});
		pan.add(button);
		return pan;
	}
	
	@Override
	public void draw(List<Drawable> toDraw) {
		super.draw(toDraw);
		AffineTransform trans = new AffineTransform(bp.trans);
		trans.translate(0, -.1F);
		toDraw.add(new DSVG("ArrowWheel", .15F, bp.getLevel()+1).trans(trans));
	}

	@Override
	public Behaviour createBehaviour() {
		return new WheelBehaviour(comp, compName);
	}
}
