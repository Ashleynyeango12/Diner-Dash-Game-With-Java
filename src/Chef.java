import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Chef {
	private int ordersToCook;
	private Counter counter;
	Timer chefTimer;
	boolean isCooking ;
	
	public Chef() {
	}

	public void prepare(int ordersToCook, Counter counter) {
		this.ordersToCook = ordersToCook;
		this.counter = counter;
		this.isCooking = false;
		this.cook();
	}

	public void cook() {
		//Sound.COOK.loop();
		this.setChefTimer();
		//Sound.COOK.stop();
	}	

	public void setChefTimer() {
		ActionListener chefListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("chef cooking");
				counter.addFood();
				ordersToCook--;
				if (ordersToCook == 0) {
					chefTimer.stop();
					isCooking = false;
				}
			}
		};
		chefTimer = new Timer(3000,chefListener);
		chefTimer.setRepeats(true); //allows repetitions
		chefTimer.start();
		isCooking = true;
	}
}
