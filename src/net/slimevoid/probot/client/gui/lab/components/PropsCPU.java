package net.slimevoid.probot.client.gui.lab.components;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;

import net.slimevoid.probot.game.Material;
import net.slimevoid.probot.game.components.Behaviour;
import net.slimevoid.probot.game.components.CPUBehaviour;
import net.slimevoid.probot.game.components.CompDB.CompEntry;

import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.js.JavaScriptCompletionProvider;
import org.fife.rsta.ac.js.JavaScriptLanguageSupport;
import org.fife.rsta.ac.js.JavaScriptParser;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.engine.JavaScriptEngineFactory;
import org.fife.rsta.ac.js.engine.RhinoJavaScriptEngine;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

public class PropsCPU extends PropsComp {
	
	private static final long serialVersionUID = 1L;
	public String code;
	private transient RSyntaxTextArea editor;
	
	public PropsCPU(CompEntry comp) {
		super(Material.PVC, comp);
	}

	@Override
	public JPanel populatePropsFrame(JPanel pan) {
		pan = super.populatePropsFrame(pan);
		pan.setLayout(new BorderLayout());
		RhinoJavaScriptEngine engine = new RhinoJavaScriptEngine();
		engine.getTypesFactory().addType("name", new TypeDeclaration("test", "comps", "name"));
		JavaScriptEngineFactory.Instance().addEngine("engine", engine);;
		editor = new RSyntaxTextArea(20, 60);
		editor.setText(code);
		editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
		editor.setCodeFoldingEnabled(true);
		editor.setAntiAliasingEnabled(true);
		JavaScriptLanguageSupport ls = new JavaScriptLanguageSupport();
		ls.setAutoActivationEnabled(true);
		ls.setAutoActivationDelay(0);
		editor.addParser(new JavaScriptParser(ls, editor));
		AutoCompletion ac = new AutoCompletion(createJSProvider(ls));
		ac.install(editor);
		ls.install(editor);
		try {
			Theme.load(ClassLoader.getSystemResourceAsStream("editor/eclipse.xml")).apply(editor);
		} catch (IOException e) {
		}
		RTextScrollPane sp = new RTextScrollPane(editor);
		sp.setFoldIndicatorEnabled(true);
		pan.add(sp);
		return pan;
	}

	private CompletionProvider createJSProvider(JavaScriptLanguageSupport ls) {
		SourceCompletionProvider scp = new SourceCompletionProvider("engine", false);
		JarManager jm = new JarManager();
		try {
			jm.addClassFileSource(new File("release/scriptAPI.jar"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new JavaScriptCompletionProvider(scp, jm, ls);
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		super.internalFrameClosed(e);
		code = editor.getText();
	}

	@Override
	public Behaviour createBehaviour() {
		return new CPUBehaviour(comp, compName, code);
	}
}
