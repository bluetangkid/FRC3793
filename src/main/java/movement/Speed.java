package movement;

/**
 * Simple 2d vector class that has more friendly names
 * @author Warren Funk
 *
 */
public class Speed {
	double l, r;
	
	Speed(double d, double e) {
		this.l = d;
		this.r = e;
	}
	/**
	 * @return the desired speed for the left side
	 */
	public double getL() {
		return l;
	}
	/**
	 * @return the desired speed for the right side
	 */
	public double getR() {
		return r;
	}
	
	public String justWork() {
		return l + " : " + r;
	}
}
