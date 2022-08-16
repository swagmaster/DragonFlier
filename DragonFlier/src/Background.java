import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.io.File;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

/**
 * Background
 * ----------
 * The background class is where we represent the background image that the dragon flies around in.
 * Note that while the dragon actually stays in the center of the screen, the background moves around.
 * 
 */
public class Background extends DragonGameShape{
    
    BufferedImage backgroundImage = null;
    BufferedImage backgroundBackgroundImage = null; // even further in the background is this image.
                                                    // this needs to be the same size as backgroundImage.
    
    boolean left, right, up, down; // these keep track of the direction the user is trying to go
    
    int imageWidth; 
    int imageHeight; 
    
    int FRAME_WIDTH, FRAME_HEIGHT;
    //double GRAVITY;

    /**
     * Background constructor:
     * -----------------------
     * Takes in the width and height of the frame that the background is in.
     */
    public Background(int frame_width,int frame_height) {
        
        this.FRAME_WIDTH = frame_width;
        this.FRAME_HEIGHT = frame_height;
        
        this.left = false;
        this.right = false;
        this.up = false;
        this.down = false;
        loadImages();

        this.loc.x = 0;
        this.loc.y = FRAME_HEIGHT - imageHeight; // puts the image at the bottom of the pane
    }
    
    /**
     * loadImages
     * ----------
     * This is just a helper method for the constructors, to load all the images we need
     * PRE: all the images are in the images folder!
     */
    private void loadImages(){
        try {
            this.backgroundImage = ImageIO.read(new File("images/bigCavern01.png"));
            this.backgroundBackgroundImage = ImageIO.read(new File("images/backgroundbackground02.jpg"));
            this.imageWidth = this.backgroundImage.getWidth();
            this.imageHeight = this.backgroundImage.getHeight();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    

    /**
     * draw
     * ----
     * This draws the background (and also the background of the background image.)
     * PRE: g is not null
     * POST: draws the background.
     * 
     */
    @Override
    public void draw(Graphics g) {
        double x = loc.x;
        double y = loc.y;
        g.drawImage(backgroundBackgroundImage, (int)x, (int)y, null);
        g.drawImage(backgroundImage, (int)x, (int)y, null);
        
        // if we move to the edge of the image, we'll end up seeing some black. 
        // To prevent that, we could draw secondary images (sort of like looping the image):
        /*
        if (x  > 0 ){ // draw secondary landscape
            g.drawImage(backgroundBackgroundImage, (int)x-imageWidth, (int)y, null);
            g.drawImage(backgroundImage, (int)x-imageWidth, (int)y, null);

        } 
        */
        
    }

    /**
     * nextLoc
     * -------
     * This determines the background's next location based on its current velocity and acceleration, etc.
     * PRE: this.loc has been set
     * POST: none; this doesn't change anything, it only returns a LocationAndVelocity object representing
     *       the next location of the background.
     */
    public LocationAndVelocity nextLoc(){

        LocationAndVelocity tempLoc = new LocationAndVelocity(this.loc);
        double acceleration = tempLoc.acceleration;
        double maxVelocity = tempLoc.maxVelocity;            
        // take care of left/right motion:
        if (left) {
            tempLoc.xv -= acceleration;
        } else if (right) {
            tempLoc.xv += acceleration;
        } else if (!left && !right) {
            tempLoc.xv *= loc.gravity;
        }
        // xv max is maxVelocity:
        if (tempLoc.xv >= maxVelocity) {
            tempLoc.xv = maxVelocity;
        } else if (tempLoc.xv <= -maxVelocity) {
            tempLoc.xv = -maxVelocity;
        }
        tempLoc.x += tempLoc.xv; // (velocity doubles as you keep moving)

        // if it gets more than one imageWidth off the screen, reset it.
        if (tempLoc.x <= -imageWidth) {
            tempLoc.x = tempLoc.x + imageWidth;
        } else if (tempLoc.x >= imageWidth) {
            tempLoc.x = tempLoc.x - imageWidth;
        }
        
        // take care of up/down motion:
        if (down) {
            tempLoc.yv -= acceleration;
        } else if (up) {
            tempLoc.yv += acceleration;
        } else if (!up && !down) {
            tempLoc.yv *= loc.gravity;
        }
        // yv max is maxVelocity:
        if (tempLoc.yv >= maxVelocity) {
            tempLoc.yv = maxVelocity;
        } else if (tempLoc.yv <= -maxVelocity) {
            tempLoc.yv = -maxVelocity;
        }
        tempLoc.y += tempLoc.yv; // (velocity doubles as you keep moving)

        // if it gets more than one imageHeight off the screen, reset it.
        if (tempLoc.y <= -imageHeight) {
            tempLoc.y = tempLoc.x + imageHeight;
        } else if (tempLoc.y >= imageHeight) {
            tempLoc.x = tempLoc.y - imageHeight;
        }
        

        return tempLoc;

    }

    /**
     * move
     * ----
     * When the move method is called, it updates the shape's location based on its current velocity.
     */
    @Override
    public void move() {
        // I put the builk of the code into the the nextLoc() method,
        // which will determine the lext location based on the current velocity and such.
        this.loc = nextLoc();
    }

    
    /**
     * notTransparentAt
     * ----------------
     * This returns true if there's a non-transparent pixel in the background image at x or y.
     * It also returns true if x or y is off the image enirely.
     * 
     */
    public boolean notTransparentAt(int x, int y){
        if (x < 0 || y < 0) return true;
        if (x >= this.imageWidth || y >= this.imageHeight) return true;
        
        Color pixel = new Color(this.backgroundImage.getRGB(x,y), true);
        if (pixel.getAlpha() != 0) return true;
        return false;
    }
    
    public boolean notTransparentAt(double x, double y){
        return notTransparentAt((int)x, (int)y);
    }

    public void setLeft(boolean l) {
        this.left = l;
    }

    public void setRight(boolean r) {
        this.right = r;
    }
    
    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

} // end Background class