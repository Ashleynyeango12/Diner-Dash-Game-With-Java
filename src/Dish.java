import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dish {
	private Image dishImg;
	private JButton dishButton;
	private Restaurant resto;

	public Dish(Restaurant resto) {
		this.dishImg = Toolkit.getDefaultToolkit().getImage("../bin/dish.png");
		this.resto = resto;
		this.setButtons();
	}

	public void setButtons() {	
		this.dishButton = new JButton();
		this.resto.getPanel().add(this.dishButton);
		this.dishButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!resto.getPanel().paused) {
					Thread waitThread = resto.getFloThread();
	            	try {
		            	waitThread.join(); //waits for the running thread (flo's) to stop
		            }catch (Exception ex) {}
		            Flo flo = resto.getFlo(); //allows for the continuation of the movement of Flo
					Thread floThread = new Thread(flo); //will start from Flo's last position
					flo.setCoordinates(dishButton.getX(), dishButton.getY() + 32); //sets Flo to stop at the side of the table
	            	flo.setWalkFirst('y'); //Flo will walk vertically first
	            	flo.setWalking(true);
	            	flo.setFace("north"); //sets the direction for Flo to face
	            	resto.setFloThread(floThread);
	            	floThread.start();
					dishButton.setCursor(null);
				}
			}
		});
	}

	public JButton getButton() {
		return this.dishButton;
	}

	public Image getImg() {
		return this.dishImg;
	}
}