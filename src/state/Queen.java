package state;


import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
//this is where we get the pictures from to display on the main page
public class Queen {

	private ImageIcon queen;
	
	public Queen(int q){
		this.queen = setImage(q);
	}

	public Icon getImage(){return this.queen;}
	
	private ImageIcon setImage(int q){
		loadImage(q);
		return this.queen;
	}
	
	private void loadImage(int q){
		try{
			//makes it so the queen can change size in relation to the size of the panel
			//and the size of the checker board (q*q)
			int x = 620/q;
			int y = 590/q;
			
			ImageIcon imageIcon = new ImageIcon(getClass().getResource("/queen.png"));
			Image img = imageIcon.getImage();
			Image newImage = img.getScaledInstance(x, y, java.awt.Image.SCALE_SMOOTH);
			ImageIcon newIcon = new ImageIcon(newImage);
			this.queen = newIcon;
		} catch (Exception e) {
			System.out.println("UNABLE TO LOAD IMAGE");
			e.printStackTrace();
		}
	}
}
