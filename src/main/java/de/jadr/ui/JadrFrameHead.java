package de.jadr.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JadrFrameHead extends JPanel {
	private static final long serialVersionUID = 3295167723280810299L;

	private JLabel icon = new JLabel();
	private FlatButton close;
	private FlatButton minimize;
	private int height;
	
	private Runnable onClose;
	public void setCloseHandler(Runnable r) {
		onClose = r;
	}
	
	public Runnable getCloseHandler() {
		return onClose;
	}

	public JadrFrameHead(final int height, final int width, final JFrame frame) {
		this.height = height;
		Dimension size = new Dimension(width, height);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
		this.setPreferredSize(size);
		
		this.setSize(size);
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		final Point dragStart = new Point();

		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {

			}

			public void mouseDragged(MouseEvent e) {
				frame.setLocation(e.getXOnScreen() - dragStart.x, e.getYOnScreen() - dragStart.y);
			}
		});

		this.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
				dragStart.setLocation(e.getPoint());
			}

			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseClicked(MouseEvent e) {

			}
		});
		int s = height - 10;
		icon.setSize(s, s);
		//
		

		
		close = new FlatButton("XXXX",s, s) {
			@Override
			public void onStart() {
				properties.put("bg", JadrColors.D_LAYER2);
			}
			@Override
			public void paint(Graphics2D g) {
				Font f = g.getFont().deriveFont(20f);
				g.setFont(f);
				g.setColor((Color) properties.get("bg"));
				g.fillRoundRect(0, 0, fbW, fbH, 10, 10);
				g.setColor(JadrColors.RED);
				String s = "x";
				g.drawString(s, fbW / 2 - g.getFontMetrics().stringWidth(s)/2 , fbH / 2+6);
			}
			@Override
			public void onClick(MouseEvent e) {
				if(onClose != null) {
					onClose.run();
				}else {
					frame.dispose();
					System.exit(0);	
				}
			}
			@Override
			public void onHoverStart(MouseEvent e) {
				properties.put("bg", JadrColors.D_LAYER3);
			}
			@Override
			public void onHoverEnd(MouseEvent e) {
				properties.put("bg", JadrColors.D_LAYER2);
			}
		};
		
		minimize = new FlatButton("XXXX", s,s) {
			@Override
			public void onStart() {
				properties.put("bg", JadrColors.D_LAYER2);
			}
			@Override
			public void paint(Graphics2D g) {
				Font f = g.getFont().deriveFont(20f);
				g.setFont(f);
				g.setColor((Color) properties.get("bg"));
				g.fillRoundRect(0, 0, fbW, fbH, 10, 10);
				g.setColor(Color.GRAY);
				String s = "_";
				g.drawString(s, fbW / 2 - g.getFontMetrics().stringWidth(s)/2 , fbH / 2);
			}
			@Override
			public void onClick(MouseEvent e) {
				frame.setExtendedState(JFrame.ICONIFIED);
				frame.setExtendedState(frame.getExtendedState() | JFrame.ICONIFIED);
			}
			@Override
			public void onHoverStart(MouseEvent e) {
				properties.put("bg", JadrColors.D_LAYER3);
			}
			@Override
			public void onHoverEnd(MouseEvent e) {
				properties.put("bg", JadrColors.D_LAYER2);
			}
		};
		
		Box b = Box.createHorizontalBox();
		b.add(Box.createHorizontalStrut(10));
		b.add(icon);
		b.add(Box.createHorizontalGlue());
		b.add(minimize);
		b.add(Box.createHorizontalStrut(5));
		b.add(close);
		b.add(Box.createHorizontalStrut(5));
		close.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		this.add(b);
	}


	public void setIcon(Image i) {
		icon.setIcon(new ImageIcon(JadrFrame.resize(i, height - 20, height - 20)));
	}

}
