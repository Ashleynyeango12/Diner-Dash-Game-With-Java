import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;

public class GamePanel extends JPanel implements Runnable {
	private static final int WIDTH = 500;
    private static final int HEIGHT = 350;
    private Restaurant resto;
    private Thread thread,th;
    private boolean running;
    private BufferedImage image;
    private BufferedImage backgroundImg;
    private Image scaledBackground, chefImage;
    private Image moneyBar, pauseImage, playImage, floIcon, orderSign;
    private Image[] customerImages;
    private int[] customerNumbers, preX, preY;
    private JButton pauseButton;
    private boolean hasDragged, inRect;
    boolean paused;
    private int onHand = -1;
    //onHand - index of the customerGroup the user is dragging

	public GamePanel () {
		resto = new Restaurant(1000,8,this); // (goal,numOfCustomers,GamePanel reference)
		try {
			backgroundImg = ImageIO.read(new File("../bin/back.jpg"));
			scaledBackground = backgroundImg.getScaledInstance(WIDTH,HEIGHT,Image.SCALE_SMOOTH); //fits the background image to window size
		}
		catch (Exception e) {}
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));	
		this.setLayout(null);
	}

	public void startThread() { //this will only be invoked after the user clicked the new game or load game button in the main menu
        if(thread == null) {
            thread = new Thread(this);
            thread.start();
            resto.open(); //opens restaurant then start accepting customers 
        }
    }

    public void run() {
        init();

        while(running) {
            this.repaint();
        }
    }

    private void init() { //mainly for initializations of widgets and images
        running = true;

        pauseButton = new JButton();
        pauseButton.setBounds(450,13,35,35);
        pauseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.add(pauseButton);
        pauseButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) { //if paused is true, the conditions in the methods for movements and timer will not be satisfied because of the boolean paused, thus making the threads look suspended
        		paused = !paused; //either pause or resume
        	}
        });

       	customerImages = new Image[resto.maxNumOfCustomers]; //images of customers (standing for now)
       	customerNumbers = new int[resto.maxNumOfCustomers]; //numbers of customers per customer group
       	moneyBar = Toolkit.getDefaultToolkit().getImage("../bin/moneyBar.png");
       	pauseImage = Toolkit.getDefaultToolkit().getImage("../bin/pauseButton.png");
       	playImage = Toolkit.getDefaultToolkit().getImage("../bin/playButton.png");
       	floIcon = Toolkit.getDefaultToolkit().getImage("../bin/floIcon.png");
       	orderSign = Toolkit.getDefaultToolkit().getImage("../bin/arrow.png");
       	chefImage = Toolkit.getDefaultToolkit().getImage("../bin/chef.gif");

       	preX = new int[resto.maxNumOfCustomers];
       	preY = new int[resto.maxNumOfCustomers];


       	//initializes the positions of the customers upon entering assuming they will not be dragged (if the user dragged the customers, the modification is on drawCustomers())
       	int dy = 0; //change in y
       	for (int i=0;i<resto.maxNumOfCustomers;i++) { //coordinates for each customerGroup
       		preX[i] = 10;
       		preY[i] = 285 - dy;
       		dy += 45; //adjusts the next customerGroup's position above the last customerGroup
       	}

    	this.addMouseMotionListener(new MouseMotionListener() {
    		public void mouseDragged(MouseEvent e) {
				for (int i=0;i<resto.enteredCustomers;i++) {
					if (!resto.getCustomerGrp()[i].isThisSeated() && !resto.isFull()) { //only those waiting in line can be dragged and only if the restaurant is not full
						onHand = i; //index of the customerGroup currently being dragged
						preY[i] = e.getY();
					    preX[i] = e.getX();

					    hasDragged = true; //the user drags the customerGroup
					    break;
					}
				}
			 }
			public void mouseMoved(MouseEvent e) {}
    	});

    	this.addMouseListener(new MouseAdapter(){
	        public void mouseReleased(MouseEvent e) { //assuming that the user released the customerGroup in the tables
		 		if (hasDragged && onHand != -1) { //since this method is also invoked after clicking the mouse so we need to make sure that the user really dragged the customer or else they will be marked as occupied already
			 		inRect = checkInside(preX[onHand],preY[onHand]);
			 		if (inRect) { //should be inside the rectangle
			 			resto.getCustomerGrp()[onHand].order();
			 		}
				 	hasDragged = false; //finished dragging

			 	}
	        }
    	});

    }
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		drawBackground(g2d); //paints the background first before the customers and Flo
		drawRectangle(g2d);
		drawSittingCustomer(g2d);
		drawHearts(g2d);
		drawMenu(g2d);
		drawCustomers(g2d);		//should be painted after the background and before Flo
		drawOrderSign(g2d);
		drawFlo(g2d); //so that Flo will appear in front of the background (should always be the last one to paint because of her movements)
		if (paused) drawPause(g2d);
		g2d.dispose(); //paints over the buttons (the buttons will not appear but they are still at the back of the tables)
	}

	private void drawBackground(Graphics2D g2d) { //this is where the icons should be so that they will appear on the screen
		g2d.drawImage(scaledBackground,0,0,this);
		drawChef(g2d);
		drawProgressBar(g2d);
		drawIcons(g2d);
		drawPoints(g2d);
		drawDishesAtCounter(g2d);
	}

	private void drawFlo(Graphics2D g2d) {
    	g2d.drawImage(resto.getFlo().getImg(),resto.getFlo().getXPos(), resto.getFlo().getYPos(), this); //paints the current image of Flo and her coordinates
        Toolkit.getDefaultToolkit().sync(); //allows smooth movements
    }

	private void drawPoints(Graphics2D g2d) { //sets and positions the points bar
		g2d.setFont(new Font("Arial",Font.BOLD,15));
		g2d.setColor(Color.BLACK);
		int restoPoints = resto.getPoints();
		g2d.drawString(Integer.toString(restoPoints),375,33);
	}

	private void drawProgressBar(Graphics2D g2d) {
		g2d.setColor(Color.black);
		g2d.drawRect(15,15,resto.goal/4,20);
		g2d.setColor(Color.green);
		if(resto.getPoints() != resto.goal){	
			g2d.fillRect(15,15,resto.getPoints()/4,20);
		}
	}

	private void drawPause(Graphics2D g2d) { //will only appear after pausing the game
		g2d.setFont(new Font("Arial",Font.BOLD,25));
		g2d.setColor(Color.BLACK);
		g2d.drawString("GAME PAUSED",160,190);
	}

	private void drawIcons(Graphics2D g2d) { //paints the money and pause icons
		if (!paused) g2d.drawImage(pauseImage,450,13,this);
		else g2d.drawImage(playImage,450,13,this);
		g2d.drawImage(moneyBar,340,14,this);
		g2d.drawImage(floIcon,245,2,this);
	}

	private void drawCustomers(Graphics2D g2d) {
		for (int i=0;i<resto.enteredCustomers;i++) { //loops to the number of enteredCustomers
			if (!resto.getCustomerGrp()[i].isThisSeated() && onHand != i) {
				g2d.drawImage(customerImages[i],preX[i],preY[i] + (resto.getSeated() * 45),this);
				//if the previous customerGroup become occupied, the next customerGroup that will arrive/already arrived will replace the position of the previous one in line 
			} 
			else if (!resto.getCustomerGrp()[i].isThisSeated()) {
				g2d.drawImage(customerImages[i],preX[i],preY[i],this); //the user is dragging the customer
			}
		}
	}

	private void drawSittingCustomer(Graphics2D g2d) {
		//paints the tables and the sitting customers
		if(resto.getTable()[0].isThisOccupied()) g2d.drawImage(resto.getTable()[0].getCustomers().sprites[1], 212,143, this);
		if(resto.getTable()[1].isThisOccupied()) g2d.drawImage(resto.getTable()[1].getCustomers().sprites[1], 360,143, this);
		if(resto.getTable()[2].isThisOccupied()) g2d.drawImage(resto.getTable()[2].getCustomers().sprites[1], 211,224, this);
		if(resto.getTable()[3].isThisOccupied()) g2d.drawImage(resto.getTable()[3].getCustomers().sprites[1], 357,225, this);
	}

	private void drawHearts(Graphics2D g2d) {
		if(resto.getTable()[0].isThisOccupied()) g2d.drawImage(resto.getTable()[0].getCustomers().hearts[3], 212,153, this);
		if(resto.getTable()[1].isThisOccupied()) g2d.drawImage(resto.getTable()[1].getCustomers().hearts[3], 360,153, this);
		if(resto.getTable()[2].isThisOccupied()) g2d.drawImage(resto.getTable()[2].getCustomers().hearts[3], 211,234, this);
		if(resto.getTable()[3].isThisOccupied()) g2d.drawImage(resto.getTable()[3].getCustomers().hearts[3], 357,235, this);
	}

	private void drawOrderSign(Graphics2D g2d){
			for(int i=0; i<resto.numOfTables; i++){
				//the table should not be empty, the orders are not yet taken after the customers ordered
				if (resto.getTable()[i].isThisOccupied() && resto.getTable()[i].getCustomers().getHasOrdered() && !resto.getTable()[i].getCustomers().getHasOrderTaken()){
					g2d.drawImage(orderSign,resto.getTable()[i].button.getX() + 13,resto.getTable()[i].button.getY() - 20,20,20,this);

			}
		}
	}

	private void drawChef(Graphics2D g2d) {
		if(resto.getChef().isCooking) {
			g2d.drawImage(chefImage, 290, 20, 70, 55, this);
		}
	}

	private void drawDishesAtCounter(Graphics2D g2d) {
		int x = 245;
		for (int i=0;i<resto.getCounter().getFoodNum();i++) {
			//if i has food
			g2d.drawImage(resto.getCounter().dish[i].getImg(),x,75,this);
			x += 25;
		}
		
	}

	private void drawMenu(Graphics2D g2d) {

			//the menu should only be painted if Flo has not collected them yet
			if(resto.getTable()[0].isThisOccupied() && !resto.getTable()[0].getCustomers().getHasOrderTaken()) g2d.drawImage(resto.getTable()[0].getMenu(), 258,172, this);
			if(resto.getTable()[1].isThisOccupied() && !resto.getTable()[1].getCustomers().getHasOrderTaken()) g2d.drawImage(resto.getTable()[1].getMenu(), 403,172, this);
			if(resto.getTable()[2].isThisOccupied() && !resto.getTable()[2].getCustomers().getHasOrderTaken()) g2d.drawImage(resto.getTable()[2].getMenu(), 257,257, this);
			if(resto.getTable()[3].isThisOccupied() && !resto.getTable()[3].getCustomers().getHasOrderTaken()) g2d.drawImage(resto.getTable()[3].getMenu(), 402,257, this);
	}
	
	private void drawRectangle(Graphics2D g2d) {
		g2d.setColor(new Color(0,0,0,0));
		g2d.drawRect(236,155,70,70);
		g2d.drawRect(380,155,70,70);
		g2d.drawRect(235,235,70,70);
		g2d.drawRect(380,245,70,70);
	}


	public void positionCustomers(Image im, int numOfCustomers) { //will be invoked by the customerGroup
		customerImages[resto.enteredCustomers] = im;
		customerNumbers[resto.enteredCustomers] = numOfCustomers;
	}

	public boolean checkInside(int x, int y) {
		if((x<306 && x>236)&&(y<225 && y> 155) && !resto.getTable()[0].isThisOccupied()){
			//the table should be empty before customers sit
			resto.getCustomerGrp()[onHand].sit(resto.getTable()[0]);
			//sits the customers to the table and in doing so, the table will be occupied by the customerGroup (in their classes)
			return true;
		} else if((x<450 && x>380)&&(y<225 && y> 155) && !resto.getTable()[1].isThisOccupied()){
			resto.getCustomerGrp()[onHand].sit(resto.getTable()[1]);
			return true;
		} else if((x<305 && x>235)&&(y<305 && y> 235) && !resto.getTable()[2].isThisOccupied()){
			resto.getCustomerGrp()[onHand].sit(resto.getTable()[2]);
			return true;
		}else if((x<450 && x>380)&&(y<315 && y> 245) && !resto.getTable()[3].isThisOccupied()){
			resto.getCustomerGrp()[onHand].sit(resto.getTable()[3]);
			return true;
		}
	
		return false;
	}
}
