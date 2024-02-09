import javax.swing.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;

public class Restaurant {
	private Table[] table;
	private String[] colors = {"Red","Blue","Yellow","Violet","Green"};
	private CustomerGroup[] customerGroup;
	private ArrayList<Thread> customerThreads;
	private Chef chef;
	private Flo flo;
	private Counter counter;
	private PlateStorage plateStorage;
	int maxNumOfCustomers, enteredCustomers, exitedCustomers, goal, points, numOfTables;
	private boolean isOpen, isGoalComplete;
	private Timer entranceTimer;
	private GamePanel gamePanel;
	private Thread floThread;

	public Restaurant(int goal, int maxNumOfCustomers, GamePanel gamePanel) {
		this.goal = goal;
		this.maxNumOfCustomers = maxNumOfCustomers;
		this.gamePanel = gamePanel;
		//Sound.GAMEBACKGROUND.loop();
		this.flo = new Flo(this);
		this.chef = new Chef();
		this.counter = new Counter(this);
		this.plateStorage = new PlateStorage(this);
		this.prepareTables(4); //prepares 4 tables
		//Sound.SHOW_GOAL.play();
	}

	public void setFloThread(Thread floThread) {
		this.floThread = floThread;
	}

	public Thread getFloThread() {
		return this.floThread;
	}
	
	public void open() {
		this.isOpen = true;
		this.reserveCustomers(maxNumOfCustomers); //starts initializing customers
	}

	public Table[] getTable() {
		return this.table;
	}

	public CustomerGroup[] getCustomerGrp() {
		return this.customerGroup;
	}

	public Counter getCounter() {
		return this.counter;
	}

	public int getSeated() {
		int totalSeated = 0;
		for (int i=0;i<maxNumOfCustomers;i++) {
			if (customerGroup[i].isThisSeated()) {
				totalSeated++;
			}
		}
		return totalSeated;
	}

	public Flo getFlo() { //for reference to Flo
		return this.flo;
	}

	public Chef getChef() {
		return this.chef;
	}

	public GamePanel getPanel() { //for reference to GamePanel
		return this.gamePanel;
	}

	public boolean isFull() { //checks all the tables if all of them has been occupied
		int occupiedTables = 0;
		for (int i=0;i<numOfTables;i++) {
			if (table[i].isThisOccupied()) occupiedTables++;
		}
		if (occupiedTables == numOfTables) return true;
		else return false;
	}

	public void prepareTables(int numOfTables) {
		this.numOfTables = numOfTables;
		this.table = new Table[numOfTables];
		for (int i=0;i<numOfTables;i++) {
			this.table[i] = new Table("default");
			this.table[i].placeIn(this); //passes the restaurant reference
		}
		this.table[0].designButton(246,165,50,50); //(x,y,width,height)
		this.table[0].setListener(50,4,"west"); 
		this.table[1].designButton(390,165,50,50);
		this.table[1].setListener(-48,10,"east");
		this.table[2].designButton(245,245,50,50);
		this.table[2].setListener(50,13,"west");
		this.table[3].designButton(390,245,50,50);
		this.table[3].setListener(-45,13,"east");
		// the first and second parameters in setListener() are the change of x and y to be added to the coordinates of the table so that Flo will stop at the side of the table. The third argument is the direction to face upon stopping
	}

	public void reserveCustomers(int maxNumOfCustomers) { //initializes the customers randomly
		Random rand = new Random();
		this.customerThreads = new ArrayList<Thread>();
		this.customerGroup = new CustomerGroup[maxNumOfCustomers];
		for (int i=0;i<maxNumOfCustomers;i++) {
			int randomCustomerNum = rand.nextInt(4) + 1;
			String randomColor = colors[rand.nextInt(colors.length)];
			this.customerGroup[i] = new CustomerGroup(randomCustomerNum,randomColor, this);
			Thread thread = new Thread(customerGroup[i]); //creates a thread for each customerGroup
			this.customerThreads.add(thread); //then adds it to the array of threads
		}
		this.setEntranceTimer(); //initializes the timer
	}

	public void setEntranceTimer() {
		Random rand = new Random();
		ActionListener customerListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) { //will be invoked every time the timer reach the specified interval
				if (!getPanel().paused) {
					if (enteredCustomers < maxNumOfCustomers) {
						acceptCustomers(enteredCustomers); //a customer will arrive
					}
					else closeRestaurant(); //the maximum number of customers has been reached
				}
			}
		};
		int interval = rand.nextInt(5) + 8; //random interval of time per customer arrival
		this.entranceTimer = new Timer(interval*1000,customerListener); //(milliseconds,actionListener)
		this.entranceTimer.setRepeats(true); //allows repetitions
		this.entranceTimer.start();
	}

	public void acceptCustomers(int customerIndex) { 
		//Sound.CUSTOMER_ARRIVAL.play();
		customerThreads.get(customerIndex).start(); //customer arrival
		try {
			customerThreads.get(customerIndex).join(); //waits for the customer thread to finish before incrementing enteredCustomers
		}
		catch (Exception e) {}
		this.enteredCustomers++;
	}

	/*public void alertChef() {
		//int orderNum = flo.getOrderAtHands();
		//Sound.SET_ORDERS.play();
		Thread chefThread = new Thread(chef);
		//chef.prepare(orderNum,counter);
		chefThread.start();
	}*/

	public void increaseExitedCustomers() {
		this.exitedCustomers++;
		if (this.exitedCustomers == this.maxNumOfCustomers) {
			//show points and if the goal was achieved
			//finish game
		}
	}

	public void increasePoints(int points) {
		this.points += points;
		if (this.points > this.goal && this.isGoalComplete == false) {
			this.isGoalComplete = true;
			//Sound.GOAL_COMPLETE.play();
		}
	}

	public void closeRestaurant() {
		System.out.println("Closing time!");
		this.isOpen = false;
		entranceTimer.stop();
		//closing time
	}
	
	public int getPoints(){
		return this.points;
	}
	
}