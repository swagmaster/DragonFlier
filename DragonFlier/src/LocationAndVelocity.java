/**
 * LocationAndVelocity
 * ------------------------
 * A small class just to wrap up some variables together.
 * Variables are made public for ease of access
 * (though when you create a LocationAndVelocity object, you could make it private.)
 */

public class LocationAndVelocity {
    public double x = 0; // location on the x axis
    public double y = 0; // location on the y axis
    public double xv = 0; // velocity in the x direction
    public double yv = 0; // velocity in the y direction
    public double acceleration = 0.05; // overall acceleration (the amount that the velocity increases)
    public double maxVelocity = 5; // maximum velocity.
    public double gravity = 0.97;

    public LocationAndVelocity(){
    }

    public LocationAndVelocity(double x, double y){
        this.x = x;
        this.y = y;
    }

    public LocationAndVelocity(double x, double y, double xv, double yv){
        this.x = x;
        this.y = y;
        this.xv = xv;
        this.yv = yv;
    }

    public LocationAndVelocity(double x, double y, double xv, double yv, double acceleration){
        this.x = x;
        this.y = y;
        this.xv = xv;
        this.yv = yv;
        this.acceleration = acceleration;
    }

    public LocationAndVelocity(LocationAndVelocity other){
        this.x = other.x;
        this.y = other.y;
        this.xv = other.xv;
        this.yv = other.yv;
        this.acceleration = other.acceleration;
        this.maxVelocity = other.maxVelocity;
    }

    
    /**
     * distance
     * --------
     * This calculates the distance between two points (x1,y2) and (x2,y2)
     * using our old friend, the Pythagorean Theorem.
     */
    public int distance(int x1, int y1, int x2, int y2){
        int x_dist = Math.abs(x1-x2);
        int y_dist = Math.abs(y1-y2);
        int x_squared = x_dist * x_dist;
        int y_squared = y_dist * y_dist;

        return (int)Math.sqrt(x_squared + y_squared);
    }

    /** distance: a version that takes in doubles: */
    public int distance(double x1, double y1, double x2, double y2){
        int x_dist = Math.abs((int)(x1-x2));
        int y_dist = Math.abs((int)(y1-y2));
        int x_squared = x_dist * x_dist;
        int y_squared = y_dist * y_dist;

        return (int)Math.sqrt(x_squared + y_squared);
    }

    public int distance(Dragon d, Egg e){
        return distance(d.getX(), d.getY(), e.getX(), e.getY());

    }
} // end LocationSpeedAndVelocity class

