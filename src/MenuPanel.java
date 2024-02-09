import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel{
	JButton ngame = new JButton("New Game");
	JButton lgame = new JButton("Load Game");
	
	Image backg;
	public MenuPanel () {
		
		backg = Toolkit.getDefaultToolkit().getImage("../bin/back1.jpg");
		ngame.setBounds(40,100,200,50);
		lgame.setBounds(40,160,200,50);
		
		this.setLayout(null);
		this.add(ngame);
		this.add(lgame);
		this.setPreferredSize(new Dimension(500, 350));		
	}
	

	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g; 

		g2d.drawImage(backg, 0, 0, getWidth(), getHeight(), this);
	}
}
