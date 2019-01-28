package movement;

/**
 * Simple 3d point class
 * @author Warren Funk
 *
 */
public class Point {
	private double x;
	private double y;
	
	Point(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return a double representing the distance from this point to p
	 */
	public double getDist(Point a) {
		return Math.sqrt(Math.pow(a.x - x, 2) + Math.pow(a.y - y, 2));
	}
	
	/**
	 * @return x component of the 3 dimensional vector
	 */
	public double getX() {
		return x;
	}
	/**
	 * @return y component of the 3 dimensional vector
	 */
	public double getY() {
		return y;
	}
	/**
	 * @return z component of the 3 dimensional vector
	 */
}
