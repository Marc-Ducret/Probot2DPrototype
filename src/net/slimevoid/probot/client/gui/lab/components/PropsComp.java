package net.slimevoid.probot.client.gui.lab.components;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.InternalFrameEvent;

import net.slimevoid.probot.client.gui.lab.BPProperties;
import net.slimevoid.probot.game.Material;
import net.slimevoid.probot.game.components.CompDB.CompEntry;
import net.slimevoid.probot.game.components.Wire;
import net.slimevoid.probot.game.components.WireInterface;
import net.slimevoid.probot.render.Drawable;

public abstract class PropsComp extends BPProperties implements WireInterface {

	private static final long serialVersionUID = 1L;
	
	public CompEntry comp;
	public boolean hasScriptInterface;
	public final List<Wire> wires;
	public String compName;
	private transient JTextField nameField;
	
	public PropsComp(Material mat, CompEntry comp) {
		super(mat);
		this.comp = comp;
		hasScriptInterface = false;
		wires = new ArrayList<>();
		compName = comp.getString("name").replace(" ", "_")+"_"+(System.currentTimeMillis() % 10000);
	}
	
	public void addWire(Wire w) {
		wires.add(w);
	}
	
	@Override
	public JPanel populatePropsFrame(JPanel pan) {
		pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
		JPanel namePanel = new JPanel();
		nameField = new JTextField(20);
		nameField.setText(compName);
		InputVerifier iv = new InputVerifier() {
			
			@Override
			public boolean verify(JComponent c) {
				boolean flag = ((JTextField)c).getText().matches("[a-zA-Z][a-zA-Z0-9_]*");
				c.setBackground(flag ? Color.white : Color.red);
				return flag;
			}
		};
		nameField.setInputVerifier(iv);
		nameField.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent arg0) {
				nameField.getInputVerifier().verify(nameField);
			}
		});
		namePanel.add(new JLabel("Name"));
		namePanel.add(nameField);
		pan.add(namePanel);
		JPanel subPan = new JPanel();
		pan.add(subPan);
		return subPan;
	}
	
	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		if(nameField.getInputVerifier().verify(nameField)) compName = nameField.getText();
	}
	
	@Override
	public AffineTransform getWireTransform() {
		return bp.trans;
	}
	
	@Override
	public void draw(List<Drawable> toDraw) {
		super.draw(toDraw);
		for(Wire w : wires) w.draw(toDraw);
	}
}
