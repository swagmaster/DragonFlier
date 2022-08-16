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
import java.util.ArrayList;

/**
 * Dragon
 * ------
 * Dragon class is for the main character that will move around the screen
 * The dragon is animated, so it has a number of images that it flips between.
 */
public class Dragon extends DragonGameShape{
    
    // These booleans track whether the user is pushing the buttons to make the dragon go in a certain direction:
    private boolean up, down, left, right; 
    
    // This holds the current image of the dragon:
    // (I say "current", because the image changes a lot, since it's animated.)
    private BufferedImage dragonImage = null;
    
    private int imageWidth; 
    private int imageHeight; 
    
    private boolean facingRight = true; // determines the direction the dragon is currently facing

    // These are lists of all the images used for the dragon.
    // some are images of the dragon facing left, others of the dragon facing right:
    private ArrayList<BufferedImage> leftDragonImageList = new ArrayList<>();
    private ArrayList<BufferedImage> rightDragonImageList = new ArrayList<>();

    // This variable determines which dragon image in the above lists we're currently using:
    private int whichDragon = 1;
    
    private int numberOfDraws = 0 ; // how many times the draw command has been called.
    // This is useful becayse every certain number of draws, we change the dragon image.
    
    // Since the dragon needs to affect the background, we'll keep a reference to it:
    private Background background = null;

    private double lifeLeft = 1.0; // How much life (health) the Dragon has left; a percentage.

    /**
     * Dragon Constructor
     * ------------------
     * initialX, initialY is the inital (x,y) location of the dragon.
     * The constructor will set up all the things that need to be set up.
     */
    public Dragon(Background b, int initialX, int initialY) {
        super();
        up = false;
        down = false;
        left = false;
        right = false;
        this.background = b;

        this.loc.x = initialX;
        this.loc.y = initialY;

        loadImages();

    }

    /**
     * loadImages
     * ----------
     * This is just a helper method for the constructor, to load all the images we need
     * PRE: all the images are in the images folder!
     * POST: images are ready to go.
     */
    private void loadImages(){
        try {
            //this.dragonImage = ImageIO.read(new File("testdragon.png"));
            //this.dragonImage = ImageIO.read(new File("images/dragon1Left.png"));
            leftDragonImageList.add(ImageIO.read(new File("images/dragon1Left.png")));
            leftDragonImageList.add(ImageIO.read(new File("images/dragon2Left.png")));
            leftDragonImageList.add(ImageIO.read(new File("images/dragon3Left.png")));
            leftDragonImageList.add(ImageIO.read(new File("images/dragon4Left.png")));
            leftDragonImageList.add(ImageIO.read(new File("images/dragon5Left.png")));
            leftDragonImageList.add(ImageIO.read(new File("images/dragon6Left.png")));
            rightDragonImageList.add(ImageIO.read(new File("images/dragon1Right.png")));
            rightDragonImageList.add(ImageIO.read(new File("images/dragon2Right.png")));
            rightDragonImageList.add(ImageIO.read(new File("images/dragon3Right.png")));
            rightDragonImageList.add(ImageIO.read(new File("images/dragon4Right.png")));
            rightDragonImageList.add(ImageIO.read(new File("images/dragon5Right.png")));
            rightDragonImageList.add(ImageIO.read(new File("images/dragon6Right.png")));

            this.dragonImage = leftDragonImageList.get(0);
            this.imageWidth = this.dragonImage.getWidth();
            this.imageHeight = this.dragonImage.getHeight();

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * draw
     * ----
     * This draws the dragon
     */
    @Override
    public void draw(Graphics g) {

        // We'll switch dragons every 15 times it redraws it.
        // (if we change it each time it's too fast)
        this.numberOfDraws++;
        if (this.numberOfDraws > 15) {
            this.numberOfDraws = 0;
            if (this.facingRight) this.dragonImage = nextRightDragonImage();
            else this.dragonImage = nextLeftDragonImage();
        }
        double x = loc.x;
        double y = loc.y;

        g.drawImage(dragonImage, (int)x-(imageWidth/2), (int)y-(imageHeight/2), null);
    }

    /**
     * nextLeftDragonImage
     * -------------------
     * This just gets the next image of the dragon facing left from the LeftDragonImageList
     * PRE: images have been loaded into the list
     * POST: whichDragon is incremented.
     */
    private BufferedImage nextLeftDragonImage(){
        this.whichDragon++; // whichCreature is essentially the index of which creature in the list to use.
        if (this.whichDragon >= this.leftDragonImageList.size()) this.whichDragon = 0;
        return this.leftDragonImageList.get(this.whichDragon);
    }

    /**
     * nextRightDragonImage
     * -------------------
     * This just gets the next image of the dragon facing right from the RightDragonImageList
     * PRE: images have been loaded into the list
     * POST: whichDragon is incremented.
     */
    private BufferedImage nextRightDragonImage(){
        this.whichDragon++; // whichCreature is essentially the index of which creature in the list to use.
        if (this.whichDragon >= this.rightDragonImageList.size()) this.whichDragon = 0;
        return this.rightDragonImageList.get(this.whichDragon);
    }



    /**
     * move
     * ----
     * Each time this is called, we update the dragon's position.
     * Now techically, the dragon doesn't move -- the background moves around it.
     * But here we can check if the dragon is about to hit a wall,
     * and if so, we'll bounce off of it (which means the background's velocity should change)
     * So really, this move method is 
     */
    @Override
    public void move() {
        // First we check if we're going to hit (and bounce off) of a wall:
        boolean gotBounced = false;

        int verticalMargin = imageHeight/4; // how much above or below we want to check.
        int horizontalMargin = imageWidth/2; // how much left or right to check.

        int currentx = (int)(loc.x - background.loc.x); // make it relative to the background
        int currenty = (int)(loc.y - background.loc.y);
        
        // These leftOfX, rightOfX, aboveY, belowY are to get some points around the dragon
        // where we can check if they might be impinging on the background.
        int leftOfX = currentx - horizontalMargin;
        int rightOfX = currentx + horizontalMargin;

        int abovey = (int)(currenty - verticalMargin);
        int belowy = (int)(currenty + verticalMargin);

        // check for something to the left or right of you, and if you've got horizontal velocity, about to hit it.
        // if so, reverse horizontal velocity.
        if ((background.notTransparentAt(leftOfX,currenty) && background.loc.xv > 0) ||
        background.notTransparentAt(rightOfX,currenty) && background.loc.xv < 0) {
            background.loc.xv = -background.loc.xv;
            //System.out.println("we're bouncing HORIZONTALLY");
            gotBounced = true;

        }

        // check if there's something right above or below you and you've got up or down velocity, about to hit it.
        // If so, reverse vertical velocity.
        if ((background.notTransparentAt(currentx,abovey) && background.loc.yv > 0) ||
        background.notTransparentAt(currentx,belowy) && background.loc.yv < 0) {
            background.loc.yv = -background.loc.yv; 
            //System.out.println("we're bouncing the dragon! VERTICAL");
            gotBounced = true;
        }

        // check for slow (velocity = 0)pushing that could let them creep into a wall:
        // in case vertical velocity is 0, but they're right near the top, pushing up, don't let them:

        if (background.notTransparentAt(currentx,abovey) && up){
            background.loc.y -= 1;
        }
        if (background.notTransparentAt(currentx,belowy) && down){
            background.loc.y += 1;
        }
        if (background.notTransparentAt(leftOfX,currenty) && left){
            background.loc.x -= 1;
        }
        if (background.notTransparentAt(rightOfX,currenty) && right){
            background.loc.x += 1;
        }

    } // end move()

    /*
     * the setUp, setDown, setLeft, setRight methods are setters for 
     * the internal up,down,left,right variables.
     */
    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setLeft(boolean l) {
        this.left = l;
        if (left) facingRight = false;
    }

    public void setRight(boolean r) {
        this.right = r;
        if(right) facingRight = true;

    }
    
    /*
     * Some basic getters:
     */
    public double getLifeLeft(){
        return this.lifeLeft;
    }
    
    public int getImageWidth(){
        return this.imageWidth;
    }
    
    public int getImageHeight(){
        return this.imageHeight;
    }
    
    public void subtractLife(double subtractThisAmount){
        this.lifeLeft -= subtractThisAmount;
        if (this.lifeLeft < 0 ) this.lifeLeft  = 0;
    }
    
    public void setFacingRight(boolean setToThis){
        this.facingRight = setToThis;
    }
    
    public boolean isFacingRight(){
        return this.facingRight;
    }
    
    public boolean isFacingLeft(){
        return !(this.facingRight);
    }
    

} // end Dragon class
