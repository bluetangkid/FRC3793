package movement;

import edu.wpi.first.wpilibj.PIDController;
/**
 * abstract class that {@link Straight} and {@link Turn} implement
 * @author Warren Funk
 */
public abstract class MotorAction extends Action{
	protected PIDController controller;
    protected float degrees;
	
	public MotorAction() {
		super();
		beginTime = System.currentTimeMillis();
	}
	
	/*
	 * returns a speed object containing the speeds from -1 to 1 for each motor
	 */
	public abstract void set();
	public abstract boolean isComplete();
}
