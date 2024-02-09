import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Counter {
	private int numOfFood, maxNumOfFood;
	private JButton orderWheel;
	private Restaurant resto;
	Dish[] dish;
	boolean[] hasFood;

	public Counter(Restaurant resto) {
		this.resto = resto;
		this.maxNumOfFood = 4;
		this.orderWheel = new JButton();
		this.setOrderWheel();
		this.resto.getPanel().add(this.orderWheel);
		this.dish = new Dish[maxNumOfFood];
		this.hasFood = new boolean[maxNumOfFood];
		this.setDishes();
	}

	private void setDishes() {
		for (int i=0;i<maxNumOfFood;i++) {
			dish[i] = new Dish(resto);
		}
		int x = 245;
		for (int i=0;i<maxNumOfFood;i++) {
			dish[i].getButton().setBounds(x,75,20,20);
			x += 25;
		}
	}

	public int getFood(int floCapacity) {
		if (this.numOfFood > 1 && floCapacity == 2) {
			this.numOfFood -= 2;
			return 2;
		}
		else {
			this.numOfFood -= 1;
			return 1;
		}
	}

	public void addFood() {
		//Sound.FOOD_READY.play();
		this.numOfFood++;
		//display food
		System.out.println("Num of food: " + numOfFood);
		for (int i=0;i<numOfFood;i++) {
			hasFood[i] = true;
			dish[i].getButton().setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
	}

	public void setOrderWheel() {
		orderWheel.setBounds(340,50,30,40);
		orderWheel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		orderWheel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //will be invoked every time the timer reach the specified interval
				if (!resto.getPanel().paused) {
		            Thread waitThread = resto.getFloThread();
	            	try {
		            	waitThread.join(); //waits for the running thread (Flo's) to stop
		            }catch (Exception ex) {}
		            //so that the user can click in advance the next destination of Flo
		            Flo flo = resto.getFlo(); //allows for the continuation of the movement of Flo
					Thread floThread = new Thread(flo); //will start from Flo's last position
					flo.setCoordinates(orderWheel.getX() - 11, orderWheel.getY() + 67); //fixed position so that Flo will stop right at the counter
	            	flo.setWalking(true);
	            	flo.setFace("north"); //and will be facing north
	            	resto.setFloThread(floThread);
	            	floThread.start();
				}
			}
		});
	}

	public JButton getOrderWheel() {
		return this.orderWheel;
	}

	public int getFoodNum() {
		return this.numOfFood;
	}
}