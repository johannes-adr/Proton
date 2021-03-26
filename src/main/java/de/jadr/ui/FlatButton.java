package de.jadr.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class FlatButton extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2772931776967564396L;
	protected HashMap<String, Object> properties = new HashMap<String, Object>();
	protected int fbW, fbH;
	
	public FlatButton(String s,int w, int h) {	
		super(s);
		fbW = w;
		fbH = h;
		onStart();
		Dimension d = new Dimension(w, h);
		this.setMaximumSize(d);
		//this.setBorder(null);
		this.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseExited(MouseEvent e) {
				onHoverEnd(e);
				repaint();
			}
			
			public void mouseEntered(MouseEvent e) {
				onHoverStart(e);
				repaint();
			}
			
			public void mouseClicked(MouseEvent e) {
				onClick(e);
				repaint();
			}
		});
	}
	
	@Override
	public void paint(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		paint((Graphics2D)g);
	}
	
	public void onStart() {
		
	}
	
	public void onHoverStart(MouseEvent e) {
		
	}
	public void onHoverEnd(MouseEvent e) {
		
	}
	
	public void onClick(MouseEvent e) {
		
	}
	public abstract void paint(Graphics2D g);
}