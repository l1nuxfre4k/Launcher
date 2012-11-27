package net.minecraft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Point;
import javax.swing.JTextField;
import javax.swing.ImageIcon;

import net.petercashel.modUpdater;

public class OptionsPanel extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	public static String folder = null;
	private Util util = new Util(); 

	public OptionsPanel(Frame parent) {
		super(parent);
		setLocation(new Point(0, 24));

		setModal(true);

		JPanel panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Launcher options", 0);
		label.setBorder(new EmptyBorder(0, 0, 16, 0));
		label.setFont(new Font("Default", 1, 16));
		panel.add(label, "North");

		JPanel optionsPanel = new JPanel(new BorderLayout());
		JPanel labelPanel = new JPanel(new GridLayout(0, 1));
		JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
		optionsPanel.add(labelPanel, "West");
		optionsPanel.add(fieldPanel, "Center");

		final JButton forceButton = new JButton("Force update!");
		forceButton.setDefaultCapable(false);
		forceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				GameUpdater.forceUpdate = true;
				forceButton.setText("Will force!");
				forceButton.setEnabled(false);
			}
		});
		labelPanel.add(new JLabel("Force game update: ", 4));
		fieldPanel.add(forceButton);
		labelPanel.add(new JLabel("Do mod update: ", 4));
		
		final JButton modButton = new JButton("Update Mods?");
		modButton.setDefaultCapable(false);
		modButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				modUpdater.modUpdate = true;
				modButton.setText("Update Mods!");
				modButton.setEnabled(false);
			}
		});
		fieldPanel.add(modButton);
		
		labelPanel.add(new JLabel("Game location on disk: ", 4));
		

		if (LoginForm.mcdir == "Original") {
			TransparentLabel dirLink = new TransparentLabel(
					"No Version Selected") {

				private static final long serialVersionUID = 0L;

				@Override
				public void paint(Graphics g) {
					super.paint(g);

					int x = 0;
					int y = 0;

					FontMetrics fm = g.getFontMetrics();
					int width = fm.stringWidth(getText());
					int height = fm.getHeight();

					if (getAlignmentX() == 2.0F)
						x = 0;
					else if (getAlignmentX() == 0.0F)
						x = getBounds().width / 2 - width / 2;
					else if (getAlignmentX() == 4.0F)
						x = getBounds().width - width;
					y = getBounds().height / 2 + height / 2 - 1;

					g.drawLine(x + 2, y, x + width - 2, y);
				}

				@Override
				public void update(Graphics g) {
					paint(g);
				}
			};
			dirLink.setForeground(new Color(2105599));

			fieldPanel.add(dirLink);
		} else {
			TransparentLabel dirLink = new TransparentLabel(util
					.getWorkingDirectory().toString()) {

				private static final long serialVersionUID = 0L;

				@Override
				public void paint(Graphics g) {
					super.paint(g);

					int x = 0;
					int y = 0;

					FontMetrics fm = g.getFontMetrics();
					int width = fm.stringWidth(getText());
					int height = fm.getHeight();

					if (getAlignmentX() == 2.0F)
						x = 0;
					else if (getAlignmentX() == 0.0F)
						x = getBounds().width / 2 - width / 2;
					else if (getAlignmentX() == 4.0F)
						x = getBounds().width - width;
					y = getBounds().height / 2 + height / 2 - 1;

					g.drawLine(x + 2, y, x + width - 2, y);
				}

				@Override
				public void update(Graphics g) {
					paint(g);
				}
			};
			dirLink.setCursor(Cursor.getPredefinedCursor(12));
			dirLink.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent arg0) {
					try {
						Util.openURL(new URL("file://"
								+ util.getWorkingDirectory().getAbsolutePath())
								.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			dirLink.setForeground(new Color(2105599));

			fieldPanel.add(dirLink);
		}
		;
		JButton createNew = new JButton("");
		createNew.setIcon(new ImageIcon(OptionsPanel.class
				.getResource("/net/minecraft/new.png")));
		fieldPanel.add(createNew);
		createNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				File newFolder = new File(LoginForm.mcFolder + File.separator  + textField.getText());
				if (!newFolder.exists()) {
					newFolder.mkdir();
					JOptionPane.showMessageDialog(null, "Folder " + textField.getText() + " created!");
					folder = textField.getText();
				}
				textField.setText("");
			}
		});
		textField = new JTextField(20);

		JLabel lblCreateFolder = new JLabel("Create Folder");
		labelPanel.add(lblCreateFolder);
		labelPanel.add(textField);

		panel.add(optionsPanel, "Center");

		JPanel buttonsPanel = new JPanel(new BorderLayout());
		buttonsPanel.add(new JPanel(), "Center");
		JButton doneButton = new JButton("Done");
		doneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				setVisible(false);
			}
		});
		buttonsPanel.add(doneButton, "East");
		buttonsPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

		panel.add(buttonsPanel, "South");

		getContentPane().add(panel);
		panel.setBorder(new EmptyBorder(16, 24, 24, 24));
		pack();
		setLocationRelativeTo(parent);
	}
}