import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameFrame extends JFrame{
	public static final int WIDTH = 500;
    public static final int HEIGHT = 350;
	
	JPanel p = new JPanel();

    public GameFrame () {
        super("Diner Dash");
        this.setLayout(new CardLayout());
        this.setResizable(false);
        this.setPreferredSize(new Dimension (WIDTH,HEIGHT));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);   

        p.setLayout(new CardLayout());
        
        MenuPanel menuPanel = new MenuPanel();
        Picture picture = new Picture();
        GamePanel gamePanel = new GamePanel();
        
        menuPanel.ngame.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout)(p.getLayout());
                cl.show(p, "PIC");
            }
            
        });
        
        menuPanel.lgame.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout)(p.getLayout());
                cl.show(p, "GAME");
                gamePanel.startThread();
            }
            
        }); 
        
        picture.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                picture.input++;
                picture.repaint();
                if(picture.input == 10) {
                    CardLayout cl = (CardLayout)(p.getLayout());
                    cl.show(p, "GAME");
                    gamePanel.startThread();
                }
            }
        });
        
        p.add(menuPanel, "MENU");
        p.add(picture, "PIC");
        p.add(gamePanel, "GAME");
        
        this.setContentPane(p);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}