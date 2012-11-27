package net.minecraft;

import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JPanel;

public class TransparentPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Insets insets;

	public TransparentPanel() {
	}

	public TransparentPanel(LayoutManager layout) {
		setLayout(layout);
	}

	@Override
	public boolean isOpaque() {
		return false;
	}

	public void setInsets(int a, int b, int c, int d) {
		insets = new Insets(a, b, c, d);
	}

	@Override
	public Insets getInsets() {
		if (insets == null)
			return super.getInsets();
		return insets;
	}
}