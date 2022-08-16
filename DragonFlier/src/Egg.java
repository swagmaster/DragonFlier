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
 * Egg
 * ---
 * This class represents the egg and the creature that hatches out of the egg.
 * When it hatches, the creature is animated, so there are several images it flips through.
 * Also when it hatches it will try to chase the dragon if it sees the dragon on the screen.
 */
public class Egg extends DragonGameShape{
    public eggStage stage = eggStage.NONEXISTENT;
    
    Color color = Color.WHITE; // the color of the egg
    
    int amountOfBlueness = 0; // the bluer it is, the more ready to eat.
    int amountOfBluenessAddedEachTime = 5;
    
    // We need access to the background and the dragon in order to draw the creature
    // relative to the background and bounce off walls and go towards the dragon:
    Background background; 
    Dragon dragon; 
    
    boolean isHungry; // after it has a bite of Dragon, it's not hungry for a bit.

    // the size of the image of the hatched creature:
    int imageWidth = 20;  
    int imageHeight = 10; 
    
    // the size to draw the egg:
    int eggWidth = 20;
    int eggHeight = 30;
    
    // an image for the creature (hatched) stage is stored here:
    // (This is the CURRENT image -- this will change a lot since it's animated.)
    BufferedImage creatureImage;
    
    // list of all the creature images for animating the creature:
    ArrayList<BufferedImage> creatureImageList = new ArrayList<>(); 
    // list of all the images for the creatures when they're on fire:
    // (when the dragon shoots fire and hits the creature, the animation changes)
    ArrayList<BufferedImage> creatureFireImageList = new ArrayList<>(); 

    int whichCreature = 0; // index of which image in creatureImageList to use.
    int whichCreatureFire = 0; // index of which image in creatureFireImageList to use
    
    int numberOfDraws = 0 ; // how many times the draw command has been called.
                            // useful if we want to do something every x number of draws.
    
    
    // The following variables keep track of when it will move through the different stages.
    // The Swing Timers are used to have trigger events after a certain time:
    Timer timerUntilEggAppears = null;
    double secondsItTakesUntilTheEggAppears = 0.5; // default value
    Timer timerToKeepGettingBluer = null;
    Timer timerUntilEggReadyToEat = null;
    double secondsItTakesUntilTheEggIsReadyToEat = 9.0; // default value
    Timer timerUntilEggHatches = null;
    double secondsItTakesUntilTheEggHatches = 9.0; // default value
    Timer timerUntilKilledCreatureDisappears = null;
    Timer timerUntilHungryAgain = null;

    /**
     * constructors:
     * -------------
     */
    public Egg(Background b, Dragon d, int x, int y) {
        this.background = b;
        this.dragon = d;
        this.stage = eggStage.NONEXISTENT; // later we'll change this.
        this.loc.x = x;
        this.loc.y = y;
        this.loc.maxVelocity = d.getMaxVelocity() * 0.5;
        this.isHungry = true;

        loadImages();

        startEggTimerForEggToAppear();

    } // end constructor
    
    public Egg(Background b, Dragon d, int x, int y, double secondsToAppear, double secondsToBeReadyToEat, double secondsToHatch){
        this.background = b;
        this.dragon = d;
        this.stage = eggStage.NONEXISTENT; // later we'll change this.
        this.loc.x = x;
        this.loc.y = y;
        this.loc.maxVelocity = d.getMaxVelocity() * 0.5;
        this.isHungry = true;
        this.secondsItTakesUntilTheEggAppears = secondsToAppear; // default value
        this.secondsItTakesUntilTheEggIsReadyToEat = secondsToBeReadyToEat; // default value
        this.secondsItTakesUntilTheEggHatches = secondsToHatch; // default value
        
        loadImages();

        startEggTimerForEggToAppear();
        
    }
    
    /**
     * startEggTimerForEggToAppear
     * ---------------------------
     * This just starts the Timer that will control when the egg will first appear
     * PRE: secondsItTakesUntilTheEggAppears must have been previously set (and be > 0)
     * POST: timer will start. At the end of the timer, the egg will move into the NOTREADYTOEAT stage.
     */
    private void startEggTimerForEggToAppear(){
        int timeUntilEggAppears = (int)(this.secondsItTakesUntilTheEggAppears * 1000.0);
        timerUntilEggAppears = new Timer(timeUntilEggAppears, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // We start in the non-existent phase
                    // If we want to have a different timing for each event
                    // (nonexistent, notReadyToEat, ReadyToEAt),
                    // we'll need different timers for that
                    eggAppears();

                }
            });
        timerUntilEggAppears.setRepeats(false);    
        timerUntilEggAppears.start();
        
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
            
            this.creatureImageList.add(ImageIO.read(new File("images/creatureSmall1.png")));
            this.creatureImageList.add(ImageIO.read(new File("images/creatureSmall2.png")));
            this.creatureImageList.add(ImageIO.read(new File("images/creatureSmall3.png")));
            this.creatureImageList.add(ImageIO.read(new File("images/creatureSmall4.png")));
            this.creatureImageList.add(ImageIO.read(new File("images/creatureSmall5.png")));
            this.creatureImageList.add(ImageIO.read(new File("images/creatureSmall6.png")));
            this.creatureImageList.add(ImageIO.read(new File("images/creatureSmall7.png")));
            this.creatureImageList.add(ImageIO.read(new File("images/creatureSmall8.png")));
            this.creatureImage = this.creatureImageList.get(0); // current image.
            
            this.creatureFireImageList.add(ImageIO.read(new File("images/creatureFire1.png")));
            this.creatureFireImageList.add(ImageIO.read(new File("images/creatureFire2.png")));
            this.creatureFireImageList.add(ImageIO.read(new File("images/creatureFire3.png")));
            this.creatureFireImageList.add(ImageIO.read(new File("images/creatureFire4.png")));
            

            this.imageWidth = this.creatureImage.getWidth();
            this.imageHeight = this.creatureImage.getHeight();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * eggAppears
     * ----------
     * This method is called to change from non-existent status to existing but not ready to eat status.
     * It also sets a timer to go to the next stage in egg-creature development.
     */
    public void eggAppears(){
        stage = eggStage.NOTREADYTOEAT;
        int timeUntilEggReadyToEat = (int)(this.secondsItTakesUntilTheEggIsReadyToEat * 1000.0);
        timerUntilEggReadyToEat = new Timer(timeUntilEggReadyToEat, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    eggReadyToEat();

                }
            });
        timerUntilEggReadyToEat.setRepeats(false);    
        timerUntilEggReadyToEat.start();

        int eachBlueIncreaseHappensThisOften = timeUntilEggReadyToEat / (255/amountOfBluenessAddedEachTime);
        timerToKeepGettingBluer = new Timer(eachBlueIncreaseHappensThisOften, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    increaseBlueness();
                }
            });

        timerToKeepGettingBluer.start();

    }

    /**
     * increaseBlueness
     * ----------------
     * As the egg is getting ready to eat, it gets more blue,
     * so this method is called to do that.
     * PRE: egg hasn't hatched, and isn't in the READYTOEAT stage yet.
     *      timerToKeepGettingBluer is still going.
     * POST: amountOfBlueness will increase. If we reach max blueness,
     *       we'll also shut off the timerToKeepGettingBluer
     */
    public void increaseBlueness(){
        this.amountOfBlueness += amountOfBluenessAddedEachTime;
        if (this.amountOfBlueness > 255) {
            this.amountOfBlueness = 255; // 255 is max.
            this.timerToKeepGettingBluer.stop();
        }
        this.color = new Color(255-this.amountOfBlueness,255-this.amountOfBlueness,255);

    }

    /**
     * eggReadyToEat
     * -------------
     * If the egg is ready to eat, we'll call this method which will
     * put it into the READYTOEAT stage and start a timer until the egg hatches.
     * PRE: it's currently in the NOTREADYTOEAT stage. 
     *     (if not (for example if it's already been eaten), all timers will be turned off and nothing will happen)
     * POST: stage == READYTOEAT,  
     *       timerUntilEggHatches is started.
     */
    public void eggReadyToEat(){
        if (stage != eggStage.NOTREADYTOEAT) {
            turnOffAllEggTimers();
            return; // maybe it's aleady been eaten.

        }
        stage = eggStage.READYTOEAT;
        int timeUntilEggHatches = (int)(this.secondsItTakesUntilTheEggHatches * 1000.0);
        timerUntilEggHatches = new Timer(timeUntilEggHatches, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    eggHatches();

                }
            });
        timerUntilEggHatches.setRepeats(false);    
        timerUntilEggHatches.start();

    }

    /**
     * eggHatches
     * ----------
     * This changes the egg stage to HATCHED and makes the creature hungry.
     * PRE: current stage was READYTOEAT. (If not, it won't go into hatched stage.)
     * POST: isHungry==true
     *       stage == HATCHED
     */
    public void eggHatches(){
        // we need to check the stage before we hatch because maybe it's already been eaten or something.
        if (stage == eggStage.READYTOEAT) stage = eggStage.HATCHED;

        //this.color = new Color(255,0,0);
        System.out.println("AN EGG HAS HATCHED!!!! ");
        isHungry = true;

    }

    /** 
     * creatureShot
     * ------------
     * This changes the creature's stage to KILLED, and also changes the image to the "on fire" image set.
     * It also starts a timer for when the creature disappears. 
     * (And when that timer goes off, it will go into the NONEXISTENT stage.)
     * PRE: stage== HATCHED
     * POST: stage == KILLED, 
     *       image changed to fire
     *       timer set: timerUntilKilledCreatureDisappears
     */
    public void creatureShot(){
        if (this.stage != eggStage.HATCHED) return; // it shouldn't get here.
        this.stage = eggStage.KILLED;
        this.creatureImage = this.creatureFireImageList.get(0);
        this.imageWidth = creatureImage.getWidth();
        this.imageHeight = creatureImage.getHeight();
        this.numberOfDraws = 0;
        this.whichCreature = 1;

        int timeUntilKilledCreatureDisappears = 3000;
        this.timerUntilKilledCreatureDisappears = new Timer(timeUntilKilledCreatureDisappears, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    stage = eggStage.NONEXISTENT; 

                }
            });
        this.timerUntilKilledCreatureDisappears.setRepeats(false);    
        this.timerUntilKilledCreatureDisappears.start();

    }

    /**
     * tookABiteOfTheDragon
     * --------------------
     * We call this when the creature is hatched and hungry and close enough to the dragon.
     * This will make the creature no longer hungry, and set a timer until it's hungry again.
     * (When it's not hungry, it won't chase the dragon)
     * PRE: stage == HATCHED
     *      isHungry == true
     *      it's close enough to the dragon to bite it.
     * POST: isHungry == false
     *       timerUntilHungryAgain is started (when it goes off, isHungry will be set to true)
     */
    public void tookABiteOfTheDragon(){
        System.out.println("took a bite of dragon!");
        this.isHungry = false;
        int timeUntilHungryAgain = 1000;
        this.timerUntilHungryAgain = new Timer(timeUntilHungryAgain, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    isHungry = true; 

                }
            });
        this.timerUntilHungryAgain.setRepeats(false);    
        this.timerUntilHungryAgain.start();

    }
    
    /**
     * turnOffAllEggTimers
     * -------------------
     * It does what it says.
     * PRE: timers aren't null, and have started. (if not, nothing happens)
     * POST: all timers are stopped
     */
    public void turnOffAllEggTimers(){
        if (timerUntilEggAppears != null) timerUntilEggAppears.stop();
        if (timerToKeepGettingBluer != null)timerToKeepGettingBluer.stop();
        if (timerUntilEggReadyToEat != null)timerUntilEggReadyToEat.stop();
        if (timerUntilEggHatches != null)timerUntilEggHatches.stop();
        if (timerUntilHungryAgain != null)timerUntilHungryAgain.stop();
    }

    /**
     * getEaten
     * --------
     * This is called if the dragon tries to eat an egg.
     * What happens then depends on whether the egg was ready to eat.
     * PRE: stage == NOTREADYTOEAT or stage == READYTOEAT
     *      (if it's in some other stage, nothing will happen)
     * POST: we'll turn off the egg timers, and then set stage == EATEN
     */
    public void getEaten(){

        switch(stage) {
            case NONEXISTENT:
                return; // don't do anything

            case NOTREADYTOEAT:
                turnOffAllEggTimers();
                break;
            case READYTOEAT:
                // score set by the GamePane, so I don't need to do it here.
                turnOffAllEggTimers();

                break;
            case HATCHED:
                // this shouldn't get called.  You can't get eaten when you're already hatched!
                System.out.println("error: someEgg.getEaten() was called when someEgg was already hatched.");
                break;
            case EATEN:
                // nothing needs to happen here.
                // But this also shouldn't get called.
                System.out.println("error: someEgg.getEaten() was called when someEgg was already EATEN.");
                return; // don't draw anything
            case KILLED:
                // nothing happens
                return;

        }
        this.stage = eggStage.EATEN;

    }

    /**
     * move
     * ----
     * Each time this is called, we update the Egg/creature's position.
     * (If it's still an egg, it doesn't move.)
     */
    @Override
    public void move(){
        if (stage != eggStage.HATCHED) return; // it only moves if it's hatched.

        // First we check if we're going to hit (and bounce off) of a wall:
        boolean gotBounced = false;

        int verticalMargin = imageHeight/4; // how much above or below we want to check.
        int horizontalMargin = imageWidth/4; // how much left or right to check.

        int currentx = (int)loc.x; 
        int currenty = (int)loc.y;
        int leftOfX = currentx - horizontalMargin;
        int rightOfX = currentx + horizontalMargin;

        int aboveY = (int)(loc.y - verticalMargin);
        int belowY = (int)(loc.y + verticalMargin);

        // check the pixels that are above, below, left and right of the creature.
        // If they're in the background where they shoudn't be, initiate a "bounce"
        // (which means reverse velocity): 
        if ((background.notTransparentAt(loc.x, aboveY) && loc.yv < 0) ||
        (background.notTransparentAt(loc.x, belowY) && loc.yv > 0) ) {
            gotBounced = true;
            loc.yv = -loc.yv; // reverse velocity for a bounce!
        }

        if ( (background.notTransparentAt(leftOfX, loc.y) && loc.xv < 0) ||
        (background.notTransparentAt(rightOfX, loc.y) && loc.xv > 0) ) {
            gotBounced = true;
            loc.xv = -loc.xv; // reverse velocity for a bounce!
        }

        // if it didn't get bounced, but it's next location mgiht put it into the background,
        // reverse velocity and move it over a little:
        if (!gotBounced){ 
            // check next location:
            LocationAndVelocity nextloc = nextLoc();
            int nextx = (int)(nextloc.x); 
            int nexty = (int)(nextloc.y);
            if (background.notTransparentAt(nextx, nexty)){
                this.loc.yv = -this.loc.yv; 
                this.loc.xv = -this.loc.xv; 
                if (0 < loc.xv && loc.xv < 0.2) loc.xv = 2;
                if (-0.2 < loc.xv && loc.xv < 0) loc.xv = -2;

                if (nextx < loc.x) loc.x += ((loc.x - nextx) + 5); // if a bad nextx is to the left, move right a little.
                if (loc.x < nextx) loc.x -= ((nextx - loc.x) + 5); // if a bad nextx is to the right, move left a little.
                if (nexty < loc.y) loc.y += ((loc.y - nexty) + 5); // if a bad nextx is to the left, move right a little.
                if (loc.y < nexty) loc.y -= ((nexty - loc.y) + 5); // if a bad nexty is below me, move up a little. 

                gotBounced = true;
            }
        }

        // Presumably, we haven't moved into any background location yet, so 
        // current loc.x and loc.y should be "safe".
        // We'll store them in case we still end up moving into the backround.
        double safeX = loc.x;  
        double safeY = loc.y; 
        
        // now, determine new next location and move there.
        this.loc = nextLoc();
        
        // Finally I'm going to do a last check of the new location 
        // to see it would overlap the non-transparent parts of the background.
        // If so, I'll revert to the most recent safeX, safeY coordinates.
        if ( background.notTransparentAt((int)loc.x, (int)loc.y) ){
            loc.x = safeX;
            loc.y = safeY;
            loc.xv *= 3.0; // I'll also increase the velocity so it bounces out of there more quickly.
            loc.yv *= 3.0;
        }
    }

    /**
     * nextLoc()
     * ---------
     * This returns a LocationAndVelocity object representing where the egg/creature
     * will be next if it doesn't bounce off of anything or get obstructed in some way.
     * PRE: this assumes that the egg is in HATCHED stage, so it can actually move
     * POST: none; this doesn't change anything, it just returns a new LocationAndVelocity object
     *       representing where the creature would be if it moved (based on it's current velocity,etc.)
     *       and didn't bounce off of anything.
     */
    public LocationAndVelocity nextLoc(){
        LocationAndVelocity tempLoc = new LocationAndVelocity(this.loc);

        // is the creature currently chasing the dragon, is it chasing it up (i.e., the dragon is above it?) or down, etc.?
        // These booleans mark that.
        boolean chasingUp = false;
        boolean chasingDown = false;
        boolean chasingLeft = false;
        boolean chasingRight = false;

        // Check for chasing:
        int distanceAtWhichTheCreatureSensesTheDragon = 600; // about half of the screen width.
        int screenX = (int)(this.loc.x + background.getX()); // screenX is where the creature would be drawn on the screen horizontally.
        int screenY = (int)(this.loc.y + background.getY());
        int distanceToDragon = loc.distance(dragon.getX(), dragon.getY(), screenX, screenY);
        if (distanceToDragon < distanceAtWhichTheCreatureSensesTheDragon){
            // we're chasing!
            if (screenX < dragon.getX()) chasingRight = true;
            if (dragon.getX() < screenX) chasingLeft = true;
            if (dragon.getY() < screenY) chasingUp = true;
            if (screenY < dragon.getY()) chasingDown = true;
        }

        if (this.isHungry == false){ // they don't chase when they're full.
            chasingUp = false;
            chasingDown = false;
            chasingLeft = false;
            chasingRight = false;
        }

        double acceleration = tempLoc.acceleration;
        double maxVelocity = tempLoc.maxVelocity;
        // take care of up/down motion:
        if (chasingUp) {
            tempLoc.yv -= acceleration;  // velocity changes by acceleration amount
        } else if (chasingDown) {
            tempLoc.yv += acceleration;  // velocity changes by acceleration amount
        } else if (!chasingUp && !chasingDown) {
            tempLoc.yv *= loc.gravity; // gravity slows you down
        }
        // make sure yv doesn't exceed maxVelocity:
        if (tempLoc.yv >= maxVelocity) {
            tempLoc.yv = maxVelocity;
        } else if (tempLoc.yv <= -maxVelocity) {
            tempLoc.yv = -maxVelocity;
        }
        tempLoc.y += tempLoc.yv; // (location changes by velocity amount)

        // stop it from going off screen up or down:
        if (tempLoc.y <= 0) {
            tempLoc.y = 0;
        } else if (tempLoc.y >= background.imageHeight) {
            tempLoc.y = background.imageHeight;
        }

        // take care of left/right motion:
        if (chasingLeft) {
            tempLoc.xv -= acceleration;  // velocity changes by acceleration amount
        } else if (chasingRight) {
            tempLoc.xv += acceleration;  // velocity changes by acceleration amount
        } else if (!chasingLeft && !chasingRight) {
            tempLoc.xv *= loc.gravity; // gravity slows you down
        }
        // make sure xv doesn't exceed maxVelocity:
        if (tempLoc.xv >= maxVelocity) {
            tempLoc.xv = maxVelocity;
        } else if (tempLoc.xv <= -maxVelocity) {
            tempLoc.xv = -maxVelocity;
        }
        tempLoc.x += tempLoc.xv; // (location changes by velocity amount)

        // stop it from going off screen up or down:
        if (tempLoc.x <= 0) {
            tempLoc.x = 0;
        } else if (tempLoc.y >= background.imageWidth) {
            tempLoc.x = background.imageWidth - 1;
        }

        return tempLoc;

    }

    /**
     * draw
     * ----
     * This draws the egg (or creature, if it is hatched), on the screen.
     * PRE: g is not null
     */
    @Override
    public void draw(Graphics g) {
        //System.out.println("egg draw!!!!");
        double bx = background.getX();
        double by = background.getY();

        int centerX = ((int) (bx + getX())) - (eggWidth/2); // so it draws the egg centered on it's x,y coordinates
        int centerY = ((int) (by + getY())) - (eggHeight/2);

        switch(stage) {
            case NONEXISTENT:
                return; // don't draw anything

            case NOTREADYTOEAT:
                g.setColor(this.color);
                g.fillOval(centerX, centerY, eggWidth, eggHeight);
                break;
            case READYTOEAT:
                g.setColor(this.color);
                g.fillOval(centerX, centerY, eggWidth, eggHeight);
                // a little dot in the center:
                g.setColor(Color.RED);
                g.fillOval((int) (bx+loc.x), (int) (by+loc.y), 2, 2);

                break;
            case HATCHED:
                int imageWidthToDraw = imageWidth;
                int imageHeightToDraw = imageHeight;
                centerX = ((int) (bx+loc.x)) - (imageWidth/2);
                centerY = ((int) (by+loc.y)) - (imageHeight/2);
                // We'll switch creatures every 10 times it redraws it.
                // (if we change it each time we draw the animation, it's too fast)
                numberOfDraws++;
                if (numberOfDraws > 10) {
                    numberOfDraws = 0;
                    this.creatureImage = nextCreatureImage();
                }

                g.drawImage(creatureImage, centerX, centerY, imageWidthToDraw, imageHeightToDraw, null);

                break;

            case KILLED:
                numberOfDraws++;
                if (numberOfDraws > 10){
                    numberOfDraws = 0;
                    this.creatureImage = nextCreatureFireImage();
                    
                }
                imageWidthToDraw = imageWidth;
                imageHeightToDraw = imageHeight;
                centerX = ((int) (bx+loc.x)) - (imageWidth/2);
                centerY = ((int) (by+loc.y)) - (imageHeight/2);
                g.drawImage(creatureImage, centerX, centerY, imageWidthToDraw, imageHeightToDraw, null);
                imageWidth = (int)((double)imageWidth * 0.99);
                imageHeight = (int)((double)imageWidth * 0.99);
                break;

            case EATEN:
                return; // don't draw anything

        }

    }
    
    /**
     * nextCreatureImage
     * -----------------
     * This just grabs the next image from the creatureImageList and returns it.
     * PRE: none
     * POST: this.whichCreature is incremented.
     */
    private BufferedImage nextCreatureImage(){
        this.whichCreature++; // whichCreature is essentially the index of which creature in the list to use.
        if (this.whichCreature >= this.creatureImageList.size()) this.whichCreature = 0;
        return this.creatureImageList.get(this.whichCreature);
    }
    
    /**
     * nextCreatureFireImage()
     * -----------------------
     * This grabs the next image in the creatureFireImageList,
     * which holds all the images of the creature on fire.
     * PRE: none
     * POST: this.whichCreatureFire is incremented.
     */
    private BufferedImage nextCreatureFireImage(){
        this.whichCreatureFire++; // whichCreature is essentially the index of which creature in the list to use.
        if (this.whichCreatureFire >= this.creatureFireImageList.size()) this.whichCreatureFire = 0;
        return this.creatureFireImageList.get(this.whichCreatureFire);
    }

    /**
     * notTransparentAt
     * ----------------
     * This returns true if there's a non-transparent pixel in the (hatched) image at x, y.
     * This version of the method returns FALSE if x or y is off the image enirely.
     * PRE: the following varaibles have been set (and are not null): creatureImage, imageWidth, imageHeight
     * PRE: none
     */
    public boolean notTransparentAt(int x, int y){
        /*
         * we first must translate this x,y (which is relative to the background image),
         * into coordinates that are relative to the creatureImage.
         */

        x = getX() - x;// now the x should be relative to the creature's own internal image grid.
        y = getY() - y; // same with the y.
        
        // Since this method is used for determining, for example, if a fireball has hit a creature,
        // we want it to return true when it LOOKS like the fireball has hit (overlapped with) the creature.
        // Thus, it's relevant to the math here that when we draw the creature (and the fireball for that matter),
        // we draw it such that its (x,y) location is at its center. Therefore, we need to compensate for that
        // when determining where to look for an overlap, since the creature's own internal grid
        // (on the creatureImage image) starts with (0,0) being the top left of the image.
        // So, we'll add half the creature's width and height to the x and y coordinates respectively:
        x = x + this.imageWidth/2;
        y = y + this.imageHeight/2;

        if (x < 0 || y < 0) return false;
        if (x >= this.imageWidth || y >= this.imageHeight) return false;

        Color pixel = new Color(this.creatureImage.getRGB(x,y), true);
        if (pixel.getAlpha() != 0) return true;
        return false;
    }

}
