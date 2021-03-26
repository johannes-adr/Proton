package de.jadr.ui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.jadr.local.JarFileReader;
import de.jadr.local.exceptions.JarFileReadException;

public class JadrFrame {
	private JFrame frame;
	private static final int HEAD_SIZE = 40;
	private JPanel content;
	private JadrFrameHead head;
	

	
	public JadrFrame(int w, int h, boolean resizable) {
		frame = new JFrame();
		frame.setSize(w, h+HEAD_SIZE);
		frame.setResizable(resizable);
		frame.setUndecorated(true);
		frame.setLocation(500, 500);
		content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
		
		
		head = new JadrFrameHead(HEAD_SIZE, w, frame);
		frame.getContentPane().add(head, BorderLayout.NORTH);
        frame.getContentPane().add(content, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
        
        
		loadDarkMode();
		try {
			setAppImage("defaultIcon.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void exit(int code) {
		frame.dispose();
		Runnable r = head.getCloseHandler();
		if(r!=null)r.run();
		System.exit(code);
	}
	
	public JadrFrameHead getFrameHead() {
		return head;
	}
	
	public void update() {
		frame.revalidate();
		frame.repaint();
	}
	
	public JPanel getContentPane() {
		return content;
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	public void setAppImage(String fileName) throws IOException {
		//Image i = Toolkit.getDefaultToolkit().getImage(JadrFrame.class.getResource(fileName));
		Image i = null;
		try {
			i = JarFileReader.readRelative(fileName, JadrFrame.class).asImage();
		}catch (Exception e) {
			e.printStackTrace();
		}
		frame.setIconImage(i);
		head.setIcon(i);
	}
	
	public static Image resize(Image source, int w, int h) {
		return source.getScaledInstance(w, h, Image.SCALE_SMOOTH);
	}
	
	public void loadDarkMode()  {
		head.setBackground(JadrColors.D_LAYER2);
		content.setBackground(JadrColors.D_LAYER1);
	}
	
}
