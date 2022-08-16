import java.awt.Graphics;

/**
 * Abstract class DragonGameShape
 * ------------------------------
 * This class is used as the parent class of any objects that are to be 
 * drawn on the screen in the DragonFlier game.
 * Each DragonGameShape will have a LocationAndVelocity object called loc
 * which stores information about its location and velocity and acceleration, etc.
 * Each DragonGameShape will also have a draw method (abstract) to draw it on the screen,
 * and a move method (abstract) to indicate how its position on the screen changes 
 * They also have some getters to get the location and x and y coordinates.
 */
public abstract class DragonGameShape
{
    protected LocationAndVelocity loc;
    
    public DragonGameShape(){
        this.loc = new LocationAndVelocity();
    }
    
    /**
     * move
     * ----
     * Each time this method is called, the shape moves;
     * which means that its x,y and its velocity (xv, yv) might change.
     * These are all stored in loc.
     * This method is abstract and must be implemented by subclasses.
     */
    public abstract void move();
    
    /**
     * draw
     * ----
     * This method is used to draw the shape.
     * This method is abstract and must be implemented by subclasses.
     */
    public abstract void draw(Graphics g);
    
    public double getMaxVelocity(){
        return this.loc.maxVelocity;
    }
    
    public LocationAndVelocity getLoc(){
        return new LocationAndVelocity(this.loc);
    }
    
    public int getY() {
        return (int) loc.y;
    }

    public int getX() {
        return (int) loc.x;
    }
    
}
