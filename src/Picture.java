import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;

public class Picture extends JPanel {
	int input = 0;
	JButton next;
	ImageIcon image;
	Image[] story = new Image[10];
	
	public Picture(){

		for(int i=0; i<10; i++) {
			story[i] = Toolkit.getDefaultToolkit().getImage("../bin/"+(i+1) + ".jpg");
			this.setSize(story[i].getWidth(null),story[i].getHeight(null));
		}
		this.setLayout(null);
		this.setDoubleBuffered(true);
		
	}


	public void paintComponent(Graphics g){

		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g; 

		g2d.drawImage(story[input], 0, 0, getWidth(), getHeight(), this);
	
	}

}
