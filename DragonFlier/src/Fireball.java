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
import java.awt.Font;

/**
 * FireBall
 * --------
 * The sort of thing that gets shot from a dragon's mouth
 * Usually it's fire, but once the dragon has shot the fire,
 * it takes a little bit for the fire to recharge, so then it comes out as a 
 * puff of smoke. So there are two different images for the fireball: fire or smoke.
 */
public class Fireball extends DragonGameShape {
    // we need to have access to the dragon and background to properly draw the fireball:
    private Dragon dragon;
    private Background background;
    
    private int FRAME_WIDTH, FRAME_HEIGHT;
    
    private boolean fired = false; // fired means the fireball has been fired.
    boolean readyToFire = true; // after firing, this briefly turns false.
    
    double numberOfSecondsItTakesToBeReadyToFireAgain = 4.0;
    double secondsUntilReadyToFire = numberOfSecondsItTakesToBeReadyToFireAgain;
    
    BufferedImage fireballImage = null;
    BufferedImage puffOfSmokeImage = null;
    BufferedImage currentFireballImage = null; // the current image, be it fire or smoke
    
    int imageWidth; 
    int imageHeight; 
    
    // A Swing timer is set every time you shoot the fireball:
    Timer timerUntilReadyToFireAgain = null;

    public Fireball(Dragon d, Background b, int frame_width,int frame_height){
        this.dragon = d;
        this.background = b;
        this.FRAME_WIDTH = frame_width;
        this.FRAME_HEIGHT = frame_height;
        this.loc.gravity = 0.98; // maybe I want to change gravity later?
        
        loadImages();

        
    }
    
    /**
     * loadImages
     * ----------
     * This is just a helper method for the constructors, to load all the images we need
     * PRE: all the images are in the images folder!
     * POST: images are ready to go.
     */
    private void loadImages(){
        try {
            this.fireballImage = ImageIO.read(new File("images/fireball1.png"));
            this.currentFireballImage = this.fireballImage;
            this.imageWidth = this.fireballImage.getWidth();
            this.imageHeight = this.fireballImage.getHeight();
            this.puffOfSmokeImage = ImageIO.read(new File("images/puffOfSmoke.png"));

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * draw
     * ----
     * this draws the fireball or puff of smoke.
     * It also draws the "ready to fire meter" if the fireball isn't ready to fire yet.
     * PRE: fired==true (otherwise it won't draw anything), 
     *      g != null
     * POST: none; nothing changes in the class, it just draws something on the screen.
     */
    //@Override
    public void draw(Graphics g) {
        if (!this.readyToFire) drawReadyToFireMeter(g);
        
        if (!fired) return;

        // this needs to draw relative to the background's location.
        double bx = this.background.loc.x;
        double by = this.background.loc.y;

        int x = (int)(loc.x + bx); // so that it's relative to the background location.
        int y = (int)(loc.y + by);

        g.drawImage(currentFireballImage, (int)x-(imageWidth/2), (int)y-(imageHeight/2), null);
    }
    
    /**
     * drawReadyToFireMeter
     * --------------------
     * This draws the little meter that appears when you fire your fireball,
     * that shows how long until you'll be ready to fire it again.
     * PRE: g != null, 
     *      variables have been set: 
     *      numberOfSecondsItTakesToBeReadyToFireAgain, secondsUntilReadyToFire
     * POST: none; object doesn't change, this just draws some stuff on the screen.
     */
    public void drawReadyToFireMeter(Graphics g){
        int bigRecWidth = 250;
        int bigRecHeight = 40;
        int x = FRAME_WIDTH/3 + 100;
        int y = 12;
        double secondsElapsed = (numberOfSecondsItTakesToBeReadyToFireAgain - secondsUntilReadyToFire);
        double percentageUntilReady =  secondsElapsed / numberOfSecondsItTakesToBeReadyToFireAgain ;
        /*
        g.setColor(Color.WHITE);
        int fontSize = 36;
        g.setFont(new Font("Arial", Font.PLAIN, fontSize)); 
        g.drawString("seconds until you can fire again: " + secondsUntilReadyToFire, x,y+200);
        */
        
        g.setColor(Color.BLACK);
        g.fillRect(x, y, bigRecWidth, bigRecHeight);
        
        int offset = 10; // how much the smaller internal rectangle is offset by

        int smallRecWidth = bigRecWidth - offset;
        int smallRecHeight = bigRecHeight - offset;
        int smallX = x + offset/2;
        int smallY = y + offset/2;
        
        g.setColor(Color.ORANGE);
        g.fillRect(smallX, smallY, smallRecWidth, smallRecHeight);
        
        g.setColor(Color.RED);
        g.fillRect(smallX, smallY, (int)((double)smallRecWidth * percentageUntilReady), smallRecHeight);
    }

    /**
     * nextLoc
     * -------
     * This determines the fireball's next location based on its current velocity and acceleration, etc.
     * assuming it doesn't bounce off of anything
     * PRE: this.loc has been set
     * POST: none; this doesn't change anything, it only returns a LocationAndVelocity object representing
     *       the next location of the fireball.
     */
    public LocationAndVelocity nextLoc(){
        LocationAndVelocity tempLoc = new LocationAndVelocity(loc);

        double acceleration = tempLoc.acceleration;
        double maxVelocity = tempLoc.maxVelocity;
        // take care of up/down motion:

        tempLoc.yv *= loc.gravity; // gravity slows down your velocity

        // yv max is maxVelocity:
        if (tempLoc.yv >= maxVelocity) {
            tempLoc.yv = maxVelocity;
        } else if (tempLoc.yv <= -maxVelocity) {
            tempLoc.yv = -maxVelocity;
        }
        tempLoc.y += tempLoc.yv; 

        // stop it from going off the edge of the background image:
        if (tempLoc.y <= 0) {
            tempLoc.y = 0;
            tempLoc.yv = -tempLoc.yv; // bounce
        } else if (tempLoc.y >= background.imageHeight) {
            tempLoc.y = background.imageHeight - 1;
            tempLoc.yv = -tempLoc.yv; // bounce
        }

        // take care of left/right motion:

        tempLoc.xv *= loc.gravity; // gravity slows down your velocity

        // xv max is maxVelocity:
        if (tempLoc.xv >= maxVelocity) {
            tempLoc.xv = maxVelocity;
        } else if (tempLoc.xv <= -maxVelocity) {
            tempLoc.xv = -maxVelocity;
        }
        tempLoc.x += tempLoc.xv; 

        // stop it from going off off the edge of the background image:

        if (tempLoc.x <= 0) {
            tempLoc.x = 1;
        } else if (tempLoc.x >= background.imageWidth) {
            tempLoc.x = background.imageWidth - 1;
            tempLoc.xv = -tempLoc.xv; // bounce
        }

        return tempLoc;
    }

    /**
     * move
     * ----
     * When this is called, it updates the fireball's position based on it's current velocity, etc.
     * It also will bounce it off a wall (change velocity) if it's about to hit a wall.
     * PRE: loc has been set, background has been set.
     * POST: fireball's location is updated.
     *       Also, if fireball slows down that its velocity is below 0.05,
     *       then it sets this.fired==false. (So it won't be drawn)
     */
    @Override
    public void move() {
        if (!fired) return; // no point in moving if it hasn't been fired.

        boolean gotBounced = false;

        int verticalMargin = imageHeight/2; // how much above or below we want to check.
        int horizontalMargin = imageWidth/2; // how much left or right to check.

        int currentx = (int)loc.x; // should be already relative to the background
        int currenty = (int)loc.y;
        int leftOfX = currentx - horizontalMargin;
        int rightOfX = currentx + horizontalMargin;

        int abovey = (int)(loc.y - verticalMargin);
        int belowy = (int)(loc.y + verticalMargin);

        // check for something to the left or right of you, and if you've got horizontal velocity, about to hit it.
        // if so, reverse horizontal velocity.
        if ((background.notTransparentAt(leftOfX, currenty) && this.loc.xv < 0) ||
        background.notTransparentAt(rightOfX, currenty) && this.loc.xv > 0) {
            this.loc.xv = -this.loc.xv;
            //System.out.println("we're bouncing HORIZONTALLY");
            gotBounced = true;

        }

        // check if there's something right above or below you and you've got up or down velocity, about to hit it.
        // If so, reverse vertical velocity.
        if ((background.notTransparentAt(currentx, abovey) && this.loc.yv < 0) ||
        background.notTransparentAt(currentx, belowy) && this.loc.yv > 0) {
            this.loc.yv = -this.loc.yv; 
            //System.out.println("we're bouncing the fireball! VERTICAL");
            gotBounced = true;
        }

        // if no bounces were made, but the next location would still put us inside the landscape, 
        // bounce both vertically and horizontally.
        if (!gotBounced){
            // check next location:
            LocationAndVelocity nextloc = nextLoc();
            int nextx = (int)(nextloc.x); // should already be relative to the background
            int nexty = (int)(nextloc.y);

            if (background.notTransparentAt(nextx, nexty)) {
                this.loc.xv = -this.loc.xv;
                this.loc.yv = -this.loc.yv; 
                gotBounced = true;
                //System.out.println("bounce fireball VERTICAL AND HORIZONTAL (DIAGONAL)");

            }
        }

        // now, determine new next location and move there.
        this.loc = nextLoc();
        // if you stop moving, you've stopped firing, (so you won't be drawn, so you'll disappear):
        if (Math.abs(this.loc.xv) < 0.05 && Math.abs(this.loc.yv) < 0.05) this.fired = false; 
    }

    /**
     * fire
     * ----
     * this is called when you want to fire the fireball.
     * If you're not ready to fire it, then you'll just get a puff of smoke,
     *  whose inital velocity will be set to rather slow.
     * If you ARE ready to fire it then we'll change readyToFire to false,
     * and we'll set a timer until you're ready to fire again.
     * The inital location of the fireball or puff of smoke will be set to the dragon's location
     * (or a little to the left or right depending on what direction the dragon is facing.)
     * PRE: none
     * POST: if you were previously ready to fire, a timer gets started: timerUntilReadyToFireAgain
     *       fired == true
     *       readyToFire == false
     *       inital location and velocity for the fireball (or puff of smoke) is set
     *       
     */
    public void fire(){
        this.fired = true;
        if (this.readyToFire == false){ // you tried to fire when you weren't ready, so we switch the fireball to smoke
            this.currentFireballImage = this.puffOfSmokeImage;

            loc = new LocationAndVelocity(dragon.getLoc());
            loc.x -= background.loc.x;
            loc.xv = -background.loc.xv; // it's the background's velocity that determines horizontal velocity.

            loc.y -= background.loc.y;
            loc.yv = -background.loc.yv;

            if (dragon.isFacingRight()) {
                loc.xv += 10; // always give it at least a little velocity
                loc.x += 50; // so the fireball doesn't start in the dragon's body
            }
            else {
                loc.xv -= 10; // always give it at least a little velocity
                loc.x -= 50; // so the fireball doesn't start in the dragon's body
            }
            loc.xv *= 0.25; // the puff of smoke 
            loc.yv *= 0.25; // moves slowly.
            loc.maxVelocity = 10;
        }
        else { // we ARE ready to fire.
            this.readyToFire = false;

            int timeUntilReadyToFire = 100; // it ticks down every tenth of a second
            timerUntilReadyToFireAgain = new Timer(timeUntilReadyToFire, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        
                        secondsUntilReadyToFire -= 0.1;
                        if (secondsUntilReadyToFire <= 0.0) getReadyToFire();

                    }
                });
            timerUntilReadyToFireAgain.setRepeats(true);    
            timerUntilReadyToFireAgain.start();

            loc = new LocationAndVelocity(dragon.getLoc());
            loc.x -= background.loc.x;
            loc.xv = -background.loc.xv; // it's the background's velocity that determines horizontal velocity.

            loc.y -= background.loc.y;
            loc.yv = -background.loc.yv;

            if (dragon.isFacingRight()) {
                loc.xv += 10; // always give it at least a little velocity
                loc.x += 50; // so the fireball doesn't start in the dragon's body
            }
            else {
                loc.xv -= 10; // always give it at least a little velocity
                loc.x -= 50; // so the fireball doesn't start in the dragon's body
            }
            loc.xv *= 2;
            loc.yv *= 2;
            loc.maxVelocity = 15;
        }

        //GRAVITY = GRAVITY / 5;

        //System.out.println("FIRE: loc.xv = " + String.format("%.2f", loc.xv) + ", loc.yv = " + String.format("%.2f", loc.yv));
    }

    /**
     * getReadyToFire
     * --------------
     * This resets the fireball so that it's ready to fire, 
     * and stops timerUntilReadyToFireAgain if it was going.
     * PRE: none
     * POST: readyToFire == true
     *       fired == false
     *       currentFireBallImage == fireballImage (not smoke)
     *       timerUntilReadyToFireAgain is stopped
     */
    public void getReadyToFire(){
        this.readyToFire = true;
        this.fired = false;
        this.secondsUntilReadyToFire = this.numberOfSecondsItTakesToBeReadyToFireAgain;
        timerUntilReadyToFireAgain.stop();
        this.currentFireballImage = this.fireballImage;
        //System.out.println("ready to fire!");

    }

    public boolean hasFired(){
        return this.fired;
    }
    
    public void setFired(boolean setToThis){
        this.fired = setToThis;
    }

} // end Fireball
 