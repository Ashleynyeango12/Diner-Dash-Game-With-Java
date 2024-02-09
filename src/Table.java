import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Table {
	private String color;
	private boolean isOccupied;
	private CustomerGroup occupants;
	private Restaurant resto;
	private Image menu;
	JButton button;
	int clicks = 0;
	
	public Table(String color) {
		this.color = color;
		this.isOccupied = false;
		this.button = new JButton(); //a table represented as a button so that the user can click it
		menu = Toolkit.getDefaultToolkit().getImage("../bin/Menu.jpg");
	}

	public void setListener(int dx, int dy, String direction) {
		// the first and second parameters in setListener() are the change of x and y to be added to the coordinates of the table so that Flo will stop at the side of the table. The third argument is the direction to face upon stopping
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (!resto.getPanel().paused) {
		            Thread waitThread = resto.getFloThread();
	            	try {
		            	waitThread.join(); //waits for the running thread (flo's) to stop
		            }catch (Exception ex) {}
		            Flo flo = resto.getFlo(); //allows for the continuation of the movement of Flo
					Thread floThread = new Thread(flo); //will start from Flo's last position
					flo.setCoordinates(button.getX() + dx, button.getY() + dy); //sets Flo to stop at the side of the table
					flo.setWalkFirst('x'); //Flo will walk horizontally first
	            	flo.setWalking(true);
	            	flo.setFace(direction); //sets the direction for Flo to face
	            	resto.setFloThread(floThread);
	            	floThread.start();

	            	if (isOccupied) addClicks();
				}
			}
				
		});
	}

	public void designButton(int x, int y, int width, int height) { //sets button's appearance
		button.setBounds(x,y,width,height);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		resto.getPanel().add(button);
	}

	public void addClicks(){
		clicks++;

		if(clicks == 1) {
			//resto.getFlo().collectOrders(this);
		} else if (clicks == 2) {
			//occupants.setHasFood(true);
		} else if (clicks == 3) {
			//occupants.setHasBillCollected(true);
		} else if (clicks == 4) {
			//occupants.setFinishedEating(true);
		}

	}
	
	public String getColor() {
		return this.color;
	}

	public Image getMenu() {
		return this.menu;
	}

	public void changeColor(String color) {
		this.color = color;
	}

	public void beOccupied(CustomerGroup customerGroup) {
		this.changeColor(customerGroup.getCustomerColor());
		this.occupants = customerGroup;
		this.isOccupied = true;
	}

	public void removeOccupancy() {
		this.isOccupied = false;
	}

	public boolean isThisOccupied() {
		return this.isOccupied;
	}

	public CustomerGroup getCustomers() {
		return this.occupants;
	}
	
	public void placeIn(Restaurant resto) {
		this.resto = resto;
	}

}
