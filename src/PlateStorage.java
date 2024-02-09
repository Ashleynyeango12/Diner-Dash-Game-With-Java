import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PlateStorage {
	private Restaurant resto;
	private JButton plateButton;

	public PlateStorage(Restaurant resto) {
		this.resto = resto;
		this.plateButton = new JButton();
		this.setArrangement();
		this.resto.getPanel().add(this.plateButton);
	}

	public void setArrangement() {
		plateButton.setBounds(465,125,30,40);
		plateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		plateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!resto.getPanel().paused) {
		            Thread waitThread = resto.getFloThread();
	            	try {
		            	waitThread.join();
		            }catch (Exception ex) {}
		            Flo flo = resto.getFlo(); //allows for the continuation of the movement of Flo
					Thread floThread = new Thread(flo); //will start from Flo's last position
					flo.setCoordinates(plateButton.getX() - 5, plateButton.getY() + 5); //fixed position so that Flo will stop below the plate storage
	            	flo.setWalking(true);
	            	flo.setFace("north"); //and will be facing north
	            	resto.setFloThread(floThread);
	            	floThread.start();
				}
			}
		});
	}
}