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
import java.util.ArrayList;

/**
 * DragonFlier2
 * ------------
 * DragonFlier (v2) is a video game where you fly a dragon around trying to eat eggs.
 * The DragonFlier2 class primarily just creates a JFrame and 
 * puts a GamePane object (which is a JPanel) inside of it. 
 * The GamePane is an inner class where the real action of the game takes place.
 *
 * @author David Nixon
 * @version May 2022
 */
public class DragonFlier2
{
    // the width and height of the window (the "JFrame") where the video game happens:
    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 900;

    public static void main(String[] args) {
        new DragonFlier2();
    }

    /**
     * Constructor for objects of class DragonFlier2
     */
    public DragonFlier2() {
        
        /*
         * An explanation of "EventQueue.invokeLater(new Runnable....":
         * In this kind of program, things don't necessarily happen sequentially, executing line by line.
         * Instead, events occur when they need to, or in response to things you do (like pressing a key on the keyboard),
         * or when a timer runs out. Sometimes it might happen that two events happen relatively simultaneously. 
         * For example, a timer goes off that says it's time for an egg to hatch at the same time as you press
         * the space bar to shoot a fireball. So that we don't lose track of any of these events, all events
         * are put into an EventQueue, which then launches them in the order they arrived, as soon as it is able.
         * The invokeLater method essentially puts something into the EventQueue and says "launch this as soon as you are done
         * with any other events you might currently be working on"  -- and since we're starting this right at the beginning of 
         * the program, in the constructor, it will likely be launched right away.
         * What about the thing that's being launched, the "new Runnable(){...." stuff?  
         * Well, Runnable is an interface that has just one method, the run() method. So here, we're defining and creating this new
         * object of type Runnable, just by giving it the run() method.  The Runnable interface is used for creating what are called "threads".
         * The idea behind threads is that they are bits of code that can run on their own, perhaps at the same time as other bits of code.
         * (If your computer has multiple processors, then one thread could be running on one processor, while another thread runs on another.)
         * So these threads (bits of code) are the events that are put into the EventQueue and run in the order they arrive in the queue.
         * In this case, the run method just has stuff setting up the JFrame (window) that the video game will go inside.
         */
        EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    
                    // The UIManager class is used to determine the style of buttons and labels and widgets and whatnot.
                    // Here we try to set them so that they'll be consistent with whatever operating system you're using.
                    // (E.g., if you're running this on a Mac, the buttons will look like Mac-style buttons,
                    //  and if you're running it on a Windows box, they'll look like Windows buttons.)
                    // However, my program doesn't currently have any buttons or widgets, so this doesn't
                    // do much.
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (ClassNotFoundException | InstantiationException | 
                             IllegalAccessException | UnsupportedLookAndFeelException ex) {
                        ex.printStackTrace();
                    }
                    

                    // Here we create the main JFrame (or "window") that the game will be contained in,
                    // and we set the title and size of the frame, and what should happen if we hit the little 'x'
                    // button in the corner (end the program)
                    JFrame frame = new JFrame("Dragon Flier");
                    frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    
                    // And then inside this main frame, we place a JPanel, the GamePane.
                    // All the action takes place in the GamePane, and it's the main class where everything happens.
                    // It's defined as an inner class below.
                    frame.add(new GamePane()); 
                    //frame.pack(); // not needed because we defined the frame size.
                    frame.setLocationRelativeTo(null); // this centers the frame.
                    frame.setVisible(true); // this makes it visible!
                     
                }
            });

    }

    
    /**
     * GamePane
     * --------
     * The GamePane class extends the JPanel class, wihch is one way of drawing some stuff inside a JFrame.
     * One thing to know about the JPanel is that it automatically calls the paintComponent method.
     * In our constructor, we also set a timer that will, every 5 miliseconds, change the locations of 
     * various things in our video game (like move the dragon up if you've pushed the up arrow key),
     * and then call the repaint() method to clear the screen and call the paintComponent method again.
     * That's how we get the effect of things moving around on the screen.
     */
    private class GamePane extends JPanel {

        private Dragon dragon;
        private Background background;
        private Fireball fireball;
        private Egg egg1;
        private ArrayList<Egg> eggList = new ArrayList<Egg>();
        long startTime;

        double creatureDamage = 0.10; // how much damage it does (as a percent of lifeLeft) when a creature hits you.

        public int counter = 0; // in order to have things happen every so often when drawing...

        private int score = 0; // Your score for the game.
        
        private MessageBox messageBox;

        /**
         * GamePane Constructor
         * --------------------
         * This intializes everything and starts a timer that will move the dragon and background and eggs and fireball every 5 milliseconds 
         * PRE: all required images are in the images folder, all Action objects 
         *      (UpAction, DownAction, LeftAction, RightAction, SpaceAction, and R_Action)
         *      have been defined and their actionPerformed methods are set to do the right thing.
         * POST: This will load everything up and start the game.
         */
        public GamePane() {
            // calling the setBackground method sets the color that the background is
            // (for example, if you don't paint over the whole screen, you'll see this)
            setBackground(Color.BLACK);

            // The initialize method sets everything up: the dragon, the background, the fireball, the eggs.
            // also it does the keyBindings, which binds certain keyboard keys to certain actions
            // (For example, pressing the space bar shoots the fireball, and pressing the up arrow makes you go up)
            initialize(); 
            
            
            // The following is a Swing Timer, which, in this case, creates a new ActionLister object every 5 milliseconds.
            // The ActionListener object will automatically call its actionPerformed method.
            // In this case, the actionPerformed method will:
            // move the dragon, the background, the fireball, and the eggs (moving means changing their x,y coordinates and velocity, etc.)
            // and then check for any collisions (which will update the collided objects accordingly, and change score and health, etc.)
            // Then, after it's updated all the information, it will call the repaint() method (inherited from JPane) 
            // which just clears the screen and calls the paintComponent() method again.
            Timer timer = new Timer(5, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            dragon.move();
                            background.move();
                            fireball.move();
                            //messageBox.move();

                            for(Egg anEgg: eggList){
                                anEgg.move();
                            }
                            collisionchecker();

                            // If I wanted to do things every so often, I could use a little counter.
                            // Curently, I don't use this.
                            counter++;
                            if (counter == 100){
                                counter = 0;
                                //stuff to do from time to time...

                            }

                            repaint();
                        }
                    });
            timer.start();
        }
        
        
        /**
         * intialize
         * ---------
         * This is used to bring everything in the game to it's original starting position.
         * This could be used in the constructor to set everything up,
         * or it could be used later to reset the game (like if they lose and want to play again)
         * PRE: all required images are in the images folder. 
         *      (This method creates a dragon as well as the background and fireball and eggs, all of which need images loaded up)
         *      This method also assumes the existence of a bunch of Action objects: 
         *      UpAction, DownAction, LeftAction, RightAction, SpaceAction, and R_Action
         * POST: everything will be loaded up and ready to go for a new game.
         */
        private void initialize(){
        	
        	
            this.score = 0;
            this.eggList.clear();
            this.background = new Background(FRAME_WIDTH, FRAME_HEIGHT);
            this.dragon = new Dragon(background, FRAME_WIDTH/2, FRAME_HEIGHT/2); // dragon's starting location is in the center of the frame
            this.fireball = new Fireball(dragon, background,FRAME_WIDTH, FRAME_HEIGHT); // fireball is initially invisible
            
            this.messageBox = new MessageBox(0, 0, background);
            
            loadEggs(); // creates all the eggs and puts them into this.eggList
            
            // I keep track of how many seconds have elapsed in the game, and I do that by getting an inital startTime.
            this.startTime = System.currentTimeMillis();
            
            // the keyBindings bind particular keys to Action objects..
            // So for example, if they press the up arrow ("VK_UP"), it will create a new UpAction object,
            // and that object will (because it's a descendent of the Action class), call its 
            // ActionPerformed method (because an action -- the key pressing -- just occured).
            // Note that for the arrow keys, there's an action that happens when you press the key,
            // as well as an action that happens when you stop pressing the key.
            // These Action objects are defined as inner classes down below.
            // See about the getKeyStroke method here:
            // https://docs.oracle.com/javase/7/docs/api/javax/swing/KeyStroke.html#getKeyStroke(int,%20int,%20boolean)
            addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "dragon.up.pressed", new UpAction(dragon, true, background));
            addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "dragon.up.released", new UpAction(dragon, false, background));
            addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "dragon.down.pressed", new DownAction(dragon, true, background));
            addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "dragon.down.released", new DownAction(dragon, false,background));

            addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "dragon.left.pressed", new LeftAction(dragon, true, background));
            addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "dragon.left.released", new LeftAction(dragon, false, background));
            addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "dragon.right.pressed", new RightAction(dragon, true, background));
            addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "dragon.right.released", new RightAction(dragon, false, background));
            //Press the space bar to shoot the fireball:
            addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), "space pressed: fire fireball", 
                                                                                new SpaceAction(fireball));
            // Press the R key to reset the game:
            addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, true), "R pressed: restarting game", 
                                                                                new R_Action(this));
            addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0, true), "I pressed: ", new I_Action(messageBox));
        }
        
        
        /**
         * loadEggs
         * --------
         * This loads up all the eggs into the eggList.
         * Here is where each egg is given its initial coordinates, 
         * and the timing of when it appears, ripens, and hatches
         * PRE: eggList is empty.  (Otherwise, it will get loaded up with more eggs here)
         * POST: eggList will have a bunch of eggs in it.
         */
        private void loadEggs(){
            // Each egg is passed the background and the dragon,
            // as well as information about its initial location, 
            // the number of seconds until it apppears,
            // the number of seconds after it appears until it ripens (ready to eat),
            // and the number of seconds after it ripens before it hatches.
            this.eggList.add(new Egg(background, dragon, 1500, 850, 0, 10, 5));    // 1        
            this.eggList.add(new Egg(background, dragon, 875, 2645, 0, 17, 5));    // 2
            this.eggList.add(new Egg(background, dragon, 1800, 2893, 0, 23, 5 )); // 3   
            this.eggList.add(new Egg(background, dragon, 2000, 2902, 0, 24, 5));  // 4
            this.eggList.add(new Egg(background, dragon, 2500, 2893, 0 ,25, 5));  // 5
            this.eggList.add(new Egg(background, dragon, 2010, 2537, 0 , 30, 5)); // 6
            this.eggList.add(new Egg(background, dragon, 3400, 2031, 0 , 44, 5)); // 7
            this.eggList.add(new Egg(background, dragon, 1964, 2013, 0 , 47, 5)); // 8
            this.eggList.add(new Egg(background, dragon, 3840, 2335, 0 , 60, 5)); // 9
            this.eggList.add(new Egg(background, dragon, 4239, 2960, 0 , 73, 5)); // 10
            this.eggList.add(new Egg(background, dragon, 3225, 2705, 0 , 81, 5)); // 11
            this.eggList.add(new Egg(background, dragon, 6000, 3000, 0 , 125, 10)); // 12
            
        }

        
         /**
         * paintComponent
         * --------------
         * This automatically gets called whenever it needs to be called,
         * and it's what draws (and re-draws) everything on the screen
         * The Graphics object, g, automatically gets passed in too,
         *  and it's that object that will run all the drawing methods.
         *  PRE: none (since you don't call this method directly, you don't need to worry about it)
         *  POST: paints a bunch of stuff on the screen: 
         *        the background, the fireball, the eggs, the dragon, the score, the life left, the seconds elapsed,
         *        maybe "YOU DIED" (if you died).
         */
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // If we thought we might do something to the Graphics object that we wouldn't want to be permenant,
            // We could create a copy of the Graphics object 
            // (and maybe cast it to Graphics2d which has some extra methods):
            // Graphics2D g2d = (Graphics2D) g.create();  // later we'll have to dispose of this Graphics object.
            
            // We pass the Graphics object to the background's draw method, so it can draw itself.
            
            // To get something (background, dragon, egg, etc.) to be drawn,
            // Each thing has it's own draw method, and we just need to pass the Graphics object to it:
            this.background.draw(g);       
            this.fireball.draw(g);
            if(messageBox.getRun()) {
            	this.messageBox.draw(g);
            }
            
            
            for (Egg thisEgg: eggList){ // draw all the eggs in the eggList.
                thisEgg.draw(g);
            }
            this.dragon.draw(g);
            
            displayScore(g);
            
            displayLifeLeft(g, 25,12, dragon.getLifeLeft());
            
            if (dragon.getLifeLeft() <= 0) displayYouDied(g);
            
            displaySecondsElapsed(g);

            
            //g2d.dispose(); // only needed if we created a new Graphics object copy.
        }
        
        
        /**
         * displayScore
         * ------------
         * Just what it says!
         * This is meant to be called inside the paintComponent method, since that's where the Graphics object g comes from.
         * PRE: none
         * POST: displays the score
         */
        public void displayScore(Graphics g){
            g.setColor(Color.BLACK);
            int fontSize = 36;
            g.setFont(new Font("Arial", Font.PLAIN, fontSize)); 
            // drop shadow in black:
            g.drawString("SCORE: " + this.score, FRAME_WIDTH-201, 41);
            g.setColor(Color.WHITE);
            g.drawString("SCORE: " + this.score, FRAME_WIDTH-200, 40);

        }

        
        /**
         * displayLifeLeft
         * ----------------
         * This displays a little rectangle that shows how much life you have left.
         * green represents life you still have, while red represents life lost.
         * the double lifeLeft is the percentage of life you have left.
         * (So, for example,  if lifeLeft== .25, then the rectangle would be 25% green (on the left side),
         *  and 75% red (on the right side).)
         * x & y are where to place the top left corner of the rectangle.
         * This method is meant to be called inside the paintComponent method, since that's where the Graphics object g comes from.
         * PRE: 0 <= lifeLeft <= 1,  x & y are in a valid region of the screen (otherwise this might not get seen)
         * POST: displays a rectangle showing how much life you have left next to the word "LIFE:"
         */
        public void displayLifeLeft(Graphics g, int x, int y, double lifeLeft) {
            g.setColor(Color.BLACK);
            int fontSize = 36;
            g.setFont(new Font("Arial", Font.PLAIN, fontSize)); 
            // drop shadow in black:
            g.drawString("LIFE:" , x, y+32);
            g.setColor(Color.WHITE);
            g.drawString("LIFE:" , x+1, y+33);

            x += 100; // move over to make room for the word LIFE.
            int bigRecWidth = 250;
            int bigRecHeight = 40;
            int smallRecWidth = bigRecWidth - 10;
            int smallRecHeight = bigRecHeight - 10;
            int widthDiff = bigRecWidth - smallRecWidth;
            int smallX = x + (widthDiff/2);
            int heightDiff = bigRecHeight - smallRecHeight;
            int smallY = y + (heightDiff/2);
            // black outer rectangle:
            g.setColor(Color.BLACK);
            g.fillRect(x, y, bigRecWidth, bigRecHeight);
            // red rectangle:
            g.setColor(Color.RED);
            g.fillRect(smallX, smallY, smallRecWidth, smallRecHeight);
            // green (life) rectangle:
            g.setColor(Color.GREEN);
            g.fillRect(smallX, smallY, (int)((double)smallRecWidth * lifeLeft), smallRecHeight);
        }

        /**
         * displayYouDied
         * --------------
         * This draws the words YOU DIED on the screen
         * This method is meant to be called inside the paintComponent method, since that's where the Graphics object g comes from.
         * PRE: none
         * POST: draws the words "YOU DIED"
         */
        public void displayYouDied(Graphics g){
            g.setColor(Color.WHITE);
            int fontSize = 200;
            g.setFont(new Font("Arial", Font.PLAIN, fontSize)); 
            // drop shadow in black:
            int x = 110;
            int y = 525;
            int shadowOffset = 3;
            g.drawString("YOU  DIED" , x, y);
            g.setColor(Color.RED);
            g.drawString("YOU  DIED" , x + shadowOffset, y + shadowOffset);

        }
        
        /**
         * displaySecondsElapsed
         * ---------------------
         * This displays the seconds elapsed since the beginning of the game.
         * This method is meant to be called inside the paintComponent method, since that's where the Graphics object g comes from.
         * PRE: this.startTime had been initialized at the beginning of the game.
         * POST: draws the seconds elapsed at the top of the screen.
         */
        public void displaySecondsElapsed(Graphics g){
            g.setColor(Color.BLACK);
            int fontSize = 36;
            g.setFont(new Font("Arial", Font.PLAIN, fontSize)); 
            int x = 850;
            int y = 35;
            int shadowOffset = 3;
            int seconds = (int)(System.currentTimeMillis() - this.startTime)/ 1000;
            String s = "" + seconds;
            g.drawString(s , x, y);
            g.setColor(Color.YELLOW);
            g.drawString(s , x + shadowOffset, y + shadowOffset);
        }

        /**
         * addKeyBinding
         * -------------
         * This just binds a keyStroke to an Action.
         * Then, any time that keyStroke is detected, it will cause that Action to be created.
         * Learn about keyBindings here: https://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html 
         * PRE: none of the parameters are null.
         * POST: keyStroke ks will cause Action action to be created (which will call that Action's actionPerformed method).
         */
        protected void addKeyBinding(KeyStroke ks, String name, Action action) {
            // The idea is that you use the InputMap and ActionMap classes.
            // You use an InputMap to bind a particular keyStroke to a string,
            // and then use an ActionMap to bind that string to an Action.
            
            InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
            ActionMap am = getActionMap();

            im.put(ks, name);
            am.put(name, action);
        }

        /**
         * distance
         * --------
         * This calculates the distance between two points (x1,y1) and (x2,y2)
         * using our old friend, the Pythagorean Theorem.
         * PRE: none? 
         * POST: an int returned representing the distance between (x1,y1) and (x2,y2)
         */
        public int distance(int x1, int y1, int x2, int y2){
            int x_dist = Math.abs(x1-x2);
            int y_dist = Math.abs(y1-y2);
            int x_squared = x_dist * x_dist;
            int y_squared = y_dist * y_dist;

            return (int)Math.sqrt(x_squared + y_squared);
        }

        /** distance: 
         *  ---------
         *  a version that takes in doubles: 
         */
        private int distance(double x1, double y1, double x2, double y2){
            int x_dist = Math.abs((int)(x1-x2));
            int y_dist = Math.abs((int)(y1-y2));
            int x_squared = x_dist * x_dist;
            int y_squared = y_dist * y_dist;

            return (int)Math.sqrt(x_squared + y_squared);
        }

        /**
         * distance
         * ---------
         * a version to calculate the distance between the dragon and an egg.
         * PRE: d and e aren't null
         * POST: returns pixel distance between d and e
         */
        private int distance(Dragon d, Egg e){
            if (d == null || e == null) return 0;
            return distance(d.getX(), d.getY(), e.getX(), e.getY());

        }

        /**
         * collisionchecker
         * ----------------
         * This checks for dragon collision with eggs/creatures  and fireball collision with eggs:
         * (Note: this method DOES NOT check for collisions with the WALL (i.e, background), since each moving object 
         *  -- dragon, egg fireball -- deals with possible collisions with the wall in their own move methods.)
         *  When collisions are detected, it does the appropriate thing.  For example:
         *  If the dragon collides with an "egg", then it checks the egg's eggStage. 
         *     If the eggStage is "ready to eat", then the egg is eaten and score goes up.
         *     If the eggStage is "not ready to eat", then egg is eaten and health goes down by a ten percent.
         *     If the eggStage is "hatched", then if it's hungry, it'll reduce dragon's life a bit and then be not hungry for a couple of seconds
         *  If the fireball collides with an egg, then (so long as the fireball isn't in smoke form), again we check the stage of the egg:
         *     If the eggStage is "hatched", then the creature is shot and the fireball disappears
         *     If the egg isn't hatched yet, then you lose a point for destroying an egg that could have been eaten.
         *  PRE: loadEggs() has been called
         *  POST: eggs, dragon, and fireball have all been updated in light of any possible collisions.
         */
        public void collisionchecker() {
            // check for dragon collision with eggs  and fireball collision with eggs:

            int eggDragonCollisionDistance = dragon.getImageWidth()/3;
            int eggFireballCollisionDistance = fireball.imageWidth * 2; 
            
            for(Egg thisEgg: eggList){
                int eggDragonDistance = distance(thisEgg.getX()+background.loc.x, thisEgg.getY()+background.loc.y, 
                        dragon.getX(), dragon.getY());

                // check for dragon-egg collisions:
                if (eggDragonDistance < eggDragonCollisionDistance){
                    if (thisEgg.stage == eggStage.READYTOEAT){

                        this.score++;
                        thisEgg.getEaten();
                    }
                    else if (thisEgg.stage == eggStage.NOTREADYTOEAT){

                        // eating raw eggs doesn't affect your score, just your life:
                        dragon.subtractLife(0.1); // a tenth of your life is lost!
                        System.out.println("That egg was not ready to be eaten!");
                        thisEgg.getEaten();

                    }
                    else if (thisEgg.stage == eggStage.HATCHED){
                        // life goes down!
                        if (thisEgg.isHungry == true) {
                            dragon.subtractLife(this.creatureDamage);
                            thisEgg.tookABiteOfTheDragon();
                        }
                        
                        // bounce the creature back a little:
                        thisEgg.loc.xv = -thisEgg.loc.xv;
                        thisEgg.loc.yv = -thisEgg.loc.yv;

                        // just in case the creature is moving slowly so the bounce isn't noticable, make it a big bounce:
                        if (Math.abs(thisEgg.loc.xv) < 2){
                            if (thisEgg.loc.xv < 0) thisEgg.loc.xv = -5;
                            else thisEgg.loc.xv = 5;
                        }
                        if (Math.abs(thisEgg.loc.yv) < 2){
                            if (thisEgg.loc.yv < 0) thisEgg.loc.yv = -5;
                            else thisEgg.loc.yv = 5;
                        }

                    }
                }

                // Check for the fireball hitting the egg (or creature):
                // (but only if it's been fired and is in fireball (not smoke) form)
                if (fireball.hasFired() && fireball.currentFireballImage == fireball.fireballImage){
                    // first check if the egg is still in egg form (in which case you lose a point for shooting it):
                    if (thisEgg.stage == eggStage.READYTOEAT || thisEgg.stage == eggStage.NOTREADYTOEAT){
                        int eggFireballDistance = distance(thisEgg.getX(), thisEgg.getY(), 
                                fireball.getX(), fireball.getY());
                        if (eggFireballDistance < eggFireballCollisionDistance){
                            thisEgg.stage = eggStage.EATEN;
                            this.score--;
                            System.out.println("Your fireball hit an egg! how sad!");
                        }
                    }
                    // then check if the egg has hatched and is in creature form:
                    else if (thisEgg.stage == eggStage.HATCHED && fireballHasHitTheCreature(thisEgg)){
                        //thisEgg.stage = eggStage.KILLED;
                        thisEgg.creatureShot();
                        //this.score++; // killing the creature doesn't get you points, it just saves you.
                        fireball.setFired(false);
                        System.out.println("You killed the creature!");
                    }
                }

            }
        } // end collisionChecker()
        

        /**
         * fireballHasHitTheCreature
         * -------------------------
         * This returns a boolean if the fireball is currently hitting the creature.
         * It does so by checking the center of the fireball as well as 4 points at the corners
         * (NW, NE, SW, SE), of the fireball, to see if any of them overlapped with 
         * non-transparent parts of the creature's image.
         * PRE: this should only be called if the creature has hatched and not been killed., e is not null.
         * POST: returns true if this.fireball has hit the egg, false otherwise.
         */
        public boolean fireballHasHitTheCreature(Egg e){
            if (e == null) return false;

            int fx = (int)(this.fireball.getX() );
            int fy = (int)(this.fireball.getY() );
            //System.out.println("fireball (" + fx + ", " + fy + "), creature(" + e.getX() + ", " + e.getY() + ")");
            int offset = this.fireball.imageWidth/4;
            // we'll check the center of the fireball, then 4 points at 
            // the upper left, upper right, lower left, and lower right,
            // to see if any of those coincide with non-transparent parts of the creature image:
            // (These coordinates are relative to the background image.)
            if (e.notTransparentAt(fx, fy) || 
            e.notTransparentAt(fx-offset, fy-offset) ||
            e.notTransparentAt(fx+offset, fy-offset) ||
            e.notTransparentAt(fx-offset, fy+offset) ||
            e.notTransparentAt(fx+offset, fy+offset)) return true;
            else return false;

        }

        /*   // not needed.
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(FRAME_WIDTH, FRAME_HEIGHT);
        }
        */



    } // end GamePane class
    
    
    
    /**
     * ACTION CLASSES:
     * ---------------
     * Each of these classes represents one kind of action that can be done-
     * For example, pressing the up-arrow key causes an UpAction object to be created.
     * Every time one of these action objects is created, it automatically calls its actionPerformed method
     * to do the appropriate thing.  
     * (For example, when the UpAction's actionPerformed method is called,
     * it calls the background's setUP method so that the background knows that the person is trying to move up.)
     */

    public class UpAction extends AbstractAction {
        private Dragon dragon;
        private boolean pressed;
        private Background background;

        public UpAction(Dragon d, boolean pressed, Background b) {
            this.dragon = d;
            this.pressed = pressed;
            this.background = b;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            background.setUp(pressed); // "setUP" means set the background to the "UP" condition, since it person just pressed the UP button.
        }

    }

    public class DownAction extends AbstractAction {
        private Dragon dragon;
        private boolean pressed;
        private Background background;

        public DownAction(Dragon d, boolean pressed, Background b) {
            this.dragon = d;
            this.pressed = pressed;
            this.background = b;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            background.setDown(pressed);
        }

    }

    public class LeftAction extends AbstractAction {
        private Dragon dragon;
        private boolean pressed;
        private Background background;

        public LeftAction(Dragon d, boolean pressed, Background b) {
            this.dragon = d;
            this.pressed = pressed;
            this.background = b;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dragon.setLeft(pressed); 
            dragon.setFacingRight(false);
            background.setRight(pressed); // pressing left makes the backround move right
        }

    }

    public class RightAction extends AbstractAction {
        private Dragon dragon;
        private boolean pressed;
        private Background background;

        public RightAction(Dragon d, boolean pressed, Background b) {
            this.dragon = d;
            this.pressed = pressed;
            this.background = b;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dragon.setRight(pressed);
            dragon.setFacingRight(true);
            background.setLeft(pressed); // pressing right makes the background move left.
        }

    }

    public class SpaceAction extends AbstractAction {
        private Fireball fireball;

        public SpaceAction(Fireball f) {
            this.fireball = f;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //if (!fireball.fired) fireball.fire();
            fireball.fire();
        }
    }
    
    public class R_Action extends AbstractAction {
        GamePane gamePane;
        public R_Action(GamePane g){
            this.gamePane = g;
            
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("You typed 'R' so we'll RESTART the game!");
            this.gamePane.initialize();
        }
    }
    
    public class I_Action extends AbstractAction {
    	private MessageBox messageBox;
        public I_Action(MessageBox g){
            this.messageBox = g;
            
        }
        
		@Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("You typed I. ");
            messageBox.setRun(true);
            
        }
    }

    

} // end DragonFlier2 class

