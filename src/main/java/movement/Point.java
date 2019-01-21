package movement;

/**
 * Simple 3d point class
 * @author Warren Funk
 *
 */
public class Point {
	private double x;
	private double y;
	private double z;
	
	Point(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * @return a double representing the distance from this point to p
	 */
	public double getDist(Point a) {
		return Math.sqrt(Math.pow(a.x - x, 2) + Math.pow(a.y - y, 2) + Math.pow(a.z - z, 2));
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
	public double getZ() {
		return z;
	}
}
