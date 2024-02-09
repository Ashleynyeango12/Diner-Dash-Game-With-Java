import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Flo implements Runnable{
    private int xpos = 319; //current x position
    private int ypos = 212; //current y position
    private Image[] sprites;
    private Image im;
    private int counter, x, y;
    private char walkFirst;
    private String direction;
    private boolean walking;
    private int orderAtHands;
    private int platesAtHands;
    private int dishAtHands;
    private int totalAtHands;
    private Restaurant workPlace;
    
    public Flo(Restaurant workPlace) {
        sprites = new Image[12];
        this.workPlace = workPlace;
        this.dressUp(); //sprites set-ups                
    }

    public void dressUp() {
        for(int i=0;i<12;i++){
            sprites[i] = Toolkit.getDefaultToolkit().getImage("../Sprites/Flo/flo"+(i+1)+".png");
        }      
        im = sprites[1]; //sets the current image of Flo standing
    }

    public synchronized void run() { //does not allow interruptions while walking
        boolean hasPaused = false;
        while (this.walking) { //the boolean walking has already been set to true in class Table before starting the thread
            if (walkFirst == 'y') {
                while(ypos != y && !workPlace.getPanel().paused) { //the current y position is not yet the target y position
                    try {
                        moveVertically();
                        Thread.sleep(10);
                    }
                    catch (Exception ex) {}  
                }
                while(xpos != x && !workPlace.getPanel().paused) { //the current x position is not yet the target x position
                    try {
                        moveHorizontally();
                        Thread.sleep(10);
                    }
                    catch (Exception ex) {}  
                }
            }
            else {
                while(xpos != x && !workPlace.getPanel().paused) { //the current x position is not yet the target x position
                    try {
                        moveHorizontally();
                        Thread.sleep(10);
                    }
                    catch (Exception ex) {}  
                }
                while(ypos != y && !workPlace.getPanel().paused) { //the current y position is not yet the target y position
                    try {
                        moveVertically();
                        Thread.sleep(10);
                    }
                    catch (Exception ex) {}  
                }   
            } 
            while (workPlace.getPanel().paused) hasPaused = true; //waits for the user to resume the game 
            if (hasPaused) run(); //the game has been resumed, continue walking
            break; //finished walking
        }
        face(direction); //faces Flo to the specified direction
    }

    public void setCoordinates(int x, int y) { //the coordinates for Flo to go
        this.x = x;
        this.y = y;
    }

    public void setWalkFirst(char walkFirst) {
        this.walkFirst = walkFirst;
    }

    public void moveVertically() {
        if(y > ypos) {
            ypos += 1;
            im = sprites[(++counter%3)];                   
        } else {
            ypos -= 1;
            im = sprites[(++counter%3)+9];           
        }                                          
    }

    public void moveHorizontally() {
        if(x > xpos) {
            xpos += 1;
            im = sprites[(++counter%3)+6];
        } else {      
            xpos -= 1;
            im = sprites[(++counter%3)+3];       
        }           
    }
    
    private void face(String direction) { 
        if (direction.equals("north")) {
            im = sprites[10]; //image where Flo is facing north
        }
        else if (direction.equals("east")) {
            im = sprites[7]; //image where Flo is facing east
        }
        else if (direction.equals("west")) {
            im = sprites[4]; //image where Flo is facing west
        }   

        checkPosition();
        //Flo faced successfully (since this will be applied upon repainting)
        this.walking = false;
    }

    public void checkPosition() { //checks where Flo arrived
        //flo arrived at the counter
        if (xpos == workPlace.getCounter().getOrderWheel().getX() - 11 && ypos == workPlace.getCounter().getOrderWheel().getY() + 67 && this.orderAtHands != 0) {
            workPlace.getChef().prepare(getOrderAtHands(),workPlace.getCounter()); //place the order to the orderWheel and alerts the chef
            dressUp(); //back to normal appearance (w/o the menu)
        }
        //flo arrived at table[0]
        else if (xpos == workPlace.getTable()[0].button.getX() + 50 && ypos == workPlace.getTable()[0].button.getY() + 4 && workPlace.getTable()[0].isThisOccupied()) { //the table should have an occupant
            if (workPlace.getTable()[0].getCustomers().getHasOrdered() == true && !workPlace.getTable()[0].getCustomers().getHasOrderTaken()) { //the customers in the table has ordered and their orders are not yer taken
                collectOrders(workPlace.getTable()[0]); //collects their orders
            }
            else if (workPlace.getTable()[0].getCustomers().getHasOrderTaken() == true && this.dishAtHands > 0) { //their orders are taken and Flo has a dish in her hand
                deliverFood(workPlace.getTable()[0]);       
            }
        }
        else if (xpos == workPlace.getTable()[1].button.getX() - 48 && ypos == workPlace.getTable()[1].button.getY() + 10 && workPlace.getTable()[1].isThisOccupied()) {
            if (workPlace.getTable()[1].getCustomers().getHasOrdered() == true && !workPlace.getTable()[1].getCustomers().getHasOrderTaken()) {
                collectOrders(workPlace.getTable()[1]);
            }
            else if (workPlace.getTable()[1].getCustomers().getHasOrderTaken() == true && this.dishAtHands > 0) {
                deliverFood(workPlace.getTable()[1]);       
            }
        }
        else if (xpos == workPlace.getTable()[2].button.getX() + 50 && ypos == workPlace.getTable()[2].button.getY() + 13 && workPlace.getTable()[2].isThisOccupied()) {
            if (workPlace.getTable()[2].getCustomers().getHasOrdered() == true && !workPlace.getTable()[2].getCustomers().getHasOrderTaken()) {
                collectOrders(workPlace.getTable()[2]);
            }
            else if (workPlace.getTable()[2].getCustomers().getHasOrderTaken() == true && this.dishAtHands > 0) {
                deliverFood(workPlace.getTable()[2]);       
            }
        }
        else if (xpos == workPlace.getTable()[3].button.getX() - 45 && ypos == workPlace.getTable()[3].button.getY() + 13 && workPlace.getTable()[3].isThisOccupied()) {
            if (workPlace.getTable()[3].getCustomers().getHasOrdered() == true && !workPlace.getTable()[3].getCustomers().getHasOrderTaken()) {
                collectOrders(workPlace.getTable()[3]);
            }
            else if (workPlace.getTable()[3].getCustomers().getHasOrderTaken() == true && this.dishAtHands > 0) {
                deliverFood(workPlace.getTable()[3]);       
            }
        }
        else if (xpos == workPlace.getCounter().dish[0].getButton().getX() && ypos == workPlace.getCounter().dish[0].getButton().getY() + 32 && workPlace.getCounter().hasFood[0] == true) {
            getDishFromCounter(workPlace.getCounter());
        }
        else if (xpos == workPlace.getCounter().dish[1].getButton().getX() && ypos == workPlace.getCounter().dish[0].getButton().getY() + 32 && workPlace.getCounter().hasFood[1] == true) {
            getDishFromCounter(workPlace.getCounter());
        }
        else if (xpos == workPlace.getCounter().dish[2].getButton().getX() && ypos == workPlace.getCounter().dish[2].getButton().getY() + 32 && workPlace.getCounter().hasFood[2] == true) {
            getDishFromCounter(workPlace.getCounter());
        }
        else if (xpos == workPlace.getCounter().dish[3].getButton().getX() && ypos == workPlace.getCounter().dish[3].getButton().getY() + 32 && workPlace.getCounter().hasFood[3] == true) {
            getDishFromCounter(workPlace.getCounter());
        }
    }  


    public void setFace(String direction) { //setter for direction to face
        this.direction = direction;
    }

    public Image getImg() {
        return this.im;
    }

    public int getXPos() {
        return this.xpos;
    }

    public int getYPos() {
        return this.ypos;
    }

    public boolean isWalking() {
        return this.walking;
    }

    public void setWalking(boolean walking) {
        this.walking = walking;
    }

    public int getOrderAtHands() {
        this.totalAtHands -= this.orderAtHands;
        int prevOrder = this.orderAtHands;
        this.orderAtHands = 0;
        return prevOrder;
    }

    public void collectOrders(Table table) {
        if (this.totalAtHands < 2) {
            table.getCustomers().setOrderTaken(true); //the orders of the customers are now taken
            this.orderAtHands++;
            this.totalAtHands++;
            workPlace.increasePoints(20*this.orderAtHands);
            changeSpritesMenu(); //the one with menu

            System.out.println("getting order");
        }
    }

    public void changeSpritesMenu() {
        for(int i = 0; i<12; i++) {
            if(i<3) {
                sprites[i] = Toolkit.getDefaultToolkit().getImage("../Sprites/Flo/Order/floOrder"+(i+1)+".png");
            } else if (i >8) {
                sprites[i] = Toolkit.getDefaultToolkit().getImage("../Sprites/Flo/Order/floOrder"+(i-5)+".png");
            }
        }
    }

    public void changeSpritesDish() {
        for(int i = 0; i<12; i++) {
            if(i<3) {
                sprites[i] = Toolkit.getDefaultToolkit().getImage("../Sprites/Flo/Dish/floDish"+(i+1)+".png");
            } else if (i >8) {
                sprites[i] = Toolkit.getDefaultToolkit().getImage("../Sprites/Flo/Dish/floDish"+(i-5)+".png");
            }
        }   
    }

    public void getDishFromCounter(Counter counter) {
        if (this.totalAtHands < 2) {
            this.dishAtHands += counter.getFood(2 - this.totalAtHands);;
            this.totalAtHands += counter.getFood(2 - this.totalAtHands);
            changeSpritesDish();
        }
    }

    public void deliverFood(Table table) {
        int bonus = 1;
        //Sound.DELIVER_FOOD.play();
        table.getCustomers().setHasFood(true);
        if (this.dishAtHands == 2) bonus = 2;
        this.totalAtHands -= 1;
        this.dishAtHands--;
        workPlace.increasePoints(bonus*50);
        this.dressUp();
    }

    public void collectBills(Table table) {
        if (this.totalAtHands < 2 && table.getCustomers().hasFinishedEating() == true) {
            //Sound.COLLECT_CHECK.play();
            table.getCustomers().setHasBillCollected(true);
            workPlace.increasePoints(50);
        }
    }

    public void collectPlates(Table table) {
        if (this.totalAtHands < 2) {
            int bonus = 1;
            //Sound.CLEAN_PLATES.play();
            this.platesAtHands++;
            this.totalAtHands++;
            if (this.platesAtHands == 2) bonus = 2;
            workPlace.increasePoints(bonus*40);
            table.removeOccupancy();
        }
    }

    public void storePlates() {
        //Sound.KEEP_PLATES.play();
        this.totalAtHands -= this.platesAtHands;
        this.platesAtHands = 0;
    }

}

