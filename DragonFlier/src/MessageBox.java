import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.Timer;

/*
 * MessageBox class draws the image of a message box
 * with clear instructions on how to play the dragon
 * flier game. 
 */
public class MessageBox extends DragonGameShape{

	double width;
	double height;
	private Background background;
	private Dragon dragon;
	private int x;
	private boolean run = false;
	
	/*
	 * MessageBox constructor initializes the variables
	 * accordingly.
	 */
	public MessageBox(double w, double h, Background b) {
		super();
		this.background = b;
		setWidth(w);
		setHeight(h);
	}
	

	/*
	 * setWidth() sets the width of the message box to 
	 * a third of the frame window.
	 */
	public void setWidth(double width) {
		this.width = (0.33) * this.background.FRAME_WIDTH;
	}
	
	/*
	 * setHeight() sets the height of the message box to 
	 * 80% of the frame window.
	 */
	public void setHeight(double height) {
		this.height = (0.8) * this.background.FRAME_HEIGHT;
	}
	
	/*
	 * getWidth() returns the width of the box.
	 */
	public int getWidth() {
		return (int) width;
	}
	
	/*
	 * getHeight() wreturns the height of the box.
	 */
	public int getHeight() {
		return (int) height;
	}
	/*
	 * setX() sets where the x coordinate of the box will be.
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/*
	 * getX() returns the x coordinate value.
	 */
	public int getX() {
		return x;
	}
	
	/*
	 * move() method checks to see if the dragon
	 * object is facing left or not. Changes the 
	 * x value accordingly.
	 */
	public void move() {
		// TODO Auto-generated method stub
		if(dragon.isFacingLeft()) {
			setX(background.FRAME_WIDTH + 100);
		} else { 
			setX(background.FRAME_WIDTH + 700);
		}
	}
	
	public void setRun(boolean run){
		this.run = run;
	}
	public boolean getRun() {
		return run;
	}

	/*
	 * draw() method overrides the parents draw method
	 * to draw the box and the text.
	 */
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		if(run)
			g.setColor(new Color(0, 0, 0));
			g.fillRect(50, 60, getWidth(), getHeight());
			
			g.setColor(new Color(100, 100, 0));
			g.drawRect(50,  60, getWidth(), getHeight());
			
			g.setColor(new Color(255, 255, 255));
			g.setFont(new Font("TimesRoman", Font.BOLD, 25)); 
			
			int y = 100;
			g.drawString(displayMessage(1), 150, y);
			g.drawString(displayMessage(2), 70, y + 100);
			g.drawString(displayMessage(3), 70, y + 140);
			g.drawString(displayMessage(4), 70, y + 180);
			g.drawString(displayMessage(5), 70, y + 220);
			g.drawString(displayMessage(6), 70, y + 260);
			g.drawString(displayMessage(7), 70, y + 300);
			g.drawString(displayMessage(8), 70, y + 340);
			g.drawString(displayMessage(9), 70, y + 380);
		

		
	}

	/*
	 * displayMessage() function puts the text in a nice display.
	 */
	public String displayMessage(int line) {
		String s = "";
		if(line == 1) {
			s += "Instructions: ";
		} else if(line == 2) {
			s += "You are trying to collect eggs.";
		} else if(line == 3) {
			s += "Make sure they are ripe.";
		} else if(line == 4) {
			s += "- 1 point per egg.";
		} else if(line == 5) {
			s += "Eggs can hatch and kill you:(";
		} else if(line == 6) {
			s += "Shoot creatures with fireball!";
		} else if(line == 7) {
			s += "Fireball must recharge.";
		}else if(line == 8) {
			s += "Reach 12 points and you win!";
		} else if(line == 9) {
			s += "Press R to restart.";
		}
		return s;
			
	}
	
	/*
	 * SwingTimer() is a timer that starts at runtime and ends around 
	 * 5 seconds later.
	 */
	public static void SwingTimer() throws Exception {
		
		ActionListener  taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	System.out.println("Timer is running");                             
            }
        };
        Timer timer = new Timer(400 ,taskPerformer);
        timer.setRepeats(true);
        timer.start();
        Thread.sleep(2500);
        timer.stop();
        System.out.println("Timer done.");
        
        
	}
	/*
	 * main() method to test functions
	 */
	public static void main(String[] args) throws Exception {
		SwingTimer();
	}
	
}

