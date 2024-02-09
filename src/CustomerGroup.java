import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CustomerGroup implements Runnable {
	private final int MAX_NUMBER_OF_HEARTS = 6;
	private int numberOfPeople;
	private int numberOfHearts;
	private String color;
	private Restaurant diningPlace;
	private boolean isSeated, isOrdering;
	private boolean hasOrdered, orderTaken;
	private boolean hasFood;
	private boolean hasFinishedEating;
	private boolean hasBillCollected;
	private Timer patienceTimer, orderTimer;
	Image[] sprites, hearts;
	private Image currentImage;
	
	public CustomerGroup(int numberOfPeople, String color, Restaurant diningPlace) {
		sprites = new Image[5];
		this.numberOfPeople = numberOfPeople;
		this.color = color;
		this.dressUp();
		this.diningPlace = diningPlace;
		this.numberOfHearts = 3;
		this.heartBar();
	}

	public void dressUp() {
		sprites[0] = Toolkit.getDefaultToolkit().getImage("../Sprites/"+this.color+"/"+this.color+this.numberOfPeople+".png"); //front
		sprites[1] = Toolkit.getDefaultToolkit().getImage("../Sprites/Sit/"+this.color+"Sit"+this.numberOfPeople +".png"); //sitting
		//sprites[2] = Toolkit.getDefaultToolkit().getImage("../Sprites/Eat/"+this.color+"Sit"+this.numberOfPeople +".png"); //eating
		currentImage = sprites[0]; //standing front (for waiting at the entrance)	
	}

	public void heartBar() {
		Image[] hearts = new Image[MAX_NUMBER_OF_HEARTS];
		for(int i = 0; i < MAX_NUMBER_OF_HEARTS; i++) {
			hearts[i] = Toolkit.getDefaultToolkit().getImage("../Sprites/Heart/Heart"+i+".png");
		}
	}

	public void run() {
		this.enterRestaurant();
		diningPlace.getPanel().positionCustomers(currentImage,numberOfPeople); //passes to gamePanel for rendering
		System.out.println("Customer coming!");
	}

	public void enterRestaurant() {
		//waiting for tables
		this.setPatienceTimer(); //initializes the timer for the patience of the customers
		this.setOrderTimer();
	}

	public void setPatienceTimer() {
		ActionListener patienceListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) { //will be invoked every time the timer reach the specified interval
				reduceHearts();
			}
		};
		patienceTimer = new Timer(10000,patienceListener); //(milliseconds,actionListener)
		patienceTimer.setRepeats(true); //allows repetitions
		patienceTimer.start();
	}

	public void setOrderTimer() {
		ActionListener orderListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isSeated && !hasOrdered && !diningPlace.getPanel().paused){	
					System.out.println("CUSTOMER ORDERS!");
					hasOrdered = true;
					orderTimer.stop();
				}
			}
		};
		orderTimer = new Timer(8000,orderListener);
		orderTimer.setRepeats(false); //does not allow repetitions
		orderTimer.start();
	}

	public void reduceHearts() {
		if (this.numberOfHearts > 1) this.numberOfHearts--;
		else {
			this.diningPlace.increaseExitedCustomers();
			this.exitRestaurant();
		}
	}

	public void exitRestaurant() {
		Thread thisThread = Thread.currentThread();
		this.patienceTimer.stop();
		thisThread.interrupt();
	}

	public void sit(Table table) {
		System.out.println("sit");
		int bonus = 1;
		if (table.getColor().equals(this.color)) bonus = 100;
		this.diningPlace.increasePoints(bonus*this.numberOfPeople);
		//Sound.GAIN_HEART.play();
		this.increaseHearts();
		table.beOccupied(this);
		this.isSeated = true;
		this.patienceTimer.stop();
		//this.order();
		//Sound.READ_MENU.play();
		/*try {
			Thread.sleep(8000); //read menu for 8 seconds
			this.order();
		}
		catch (Exception e) {
			return;
		}*/
	}

	public void order() {
		int i = 10;
		this.setPatienceTimer();
		this.setOrderTimer();
		//Sound.GAIN_HEART.play();
		this.increaseHearts();
		this.patienceTimer.stop();
		}
		/*this.setPatienceTimer();
		while (!hasFood) {
			this.keepWaiting();	
		}
		this.patienceTimer.stop();
		this.eat();*/
	

	public void setOrderTaken(boolean orderTaken) {
		this.orderTaken = orderTaken;
	}

	public boolean getHasOrdered() {
		return this.hasOrdered;
	}

	public boolean getHasOrderTaken() {
		return this.orderTaken;
	}

	public void setHasFood(boolean hasFood) {
		this.hasFood = hasFood;
	}

	public void eat() {
		try {
			Thread.sleep(8000); //eat for 8 seconds
			this.hasFinishedEating = true;
			this.setPatienceTimer();
			while (!this.hasBillCollected) {
				this.keepWaiting();
			}
			this.exitRestaurant();
		}
		catch (Exception e) {
			return;
		}
	}

	public void setHasBillCollected(boolean hasBillCollected) {
		this.hasBillCollected = hasBillCollected;
	}

	public void keepWaiting() {
		try {
			Thread.sleep(100);
		}
		catch (Exception e) {
			return;
		}
	}

	public void increaseHearts() {
		if (this.numberOfPeople < 5) this.numberOfHearts++;
	}

	public Image getHearts() {
		return this.hearts[this.numberOfHearts];
	}

	public String getCustomerColor() {
		return this.color;
	}

	public boolean hasFinishedEating() {
		return this.hasFinishedEating;
	}

	public void setFinishedEating(boolean hasFinishedEating) {
		this.hasFinishedEating = hasFinishedEating;
	}

	public boolean isThisSeated() {
		return this.isSeated;
	}
	
}
