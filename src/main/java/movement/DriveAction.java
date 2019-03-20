package movement;

import edu.wpi.first.wpilibj.PIDController;
/**
 * abstract class that {@link Straight} and {@link Turn} implement
 * @author Warren Funk
 */
public abstract class DriveAction extends Action{
	protected boolean direction;
	protected float maxSpeed;
	protected PIDController controller;
	protected float degrees;
	
	public DriveAction(int direction, float maxSpeed) {
		super();
		beginTime = System.currentTimeMillis();
		this.direction = direction > 0;
		this.maxSpeed = maxSpeed;
	}
	
	/*
	 * returns a speed object containing the speeds from -1 to 1 for each motor
	 */
	public void set() {}
	public abstract boolean isComplete();
	public void resetStartPos(){
	}
}
