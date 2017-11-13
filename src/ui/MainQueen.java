package ui;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class MainQueen extends JFrame{
	
	//this sets the main frame of the ui and adds the jpanel which contains the checker board
	// and the options, also adds additional options to the way the frame interacts with the user
	public static void main(String[] args) {
		JFrame window = new JFrame();
		
		ImageIcon imageIcon = new ImageIcon(MainQueen.class.getResource("/checker.png"));
		window.setIconImage(imageIcon.getImage());
		
		window.setPreferredSize(new Dimension(700, 900));
		
		//draw the queen layout
		window.add(new QueenUI());
		
		window.setFocusable(true);
		window.requestFocus();

		window.pack();
		window.setResizable(false);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
