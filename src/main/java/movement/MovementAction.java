package movement;

import org.usfirst.frc.team3793.robot.Sensors;
import edu.wpi.first.wpilibj.PIDController;
import org.usfirst.frc.team3793.robot.Robot;
import org.usfirst.frc.team3793.robot.Motors;
/**
 * abstract class that {@link Straight} and {@link Turn} implement
 * @author Warren Funk
 */
public abstract class MovementAction {
	protected long beginTime;
	protected boolean direction;
	protected float maxSpeed;
	protected Speed PID;
	protected PIDController controller;
	protected float degrees;
	
	public MovementAction(int direction, float maxSpeed) {
		beginTime = System.currentTimeMillis();
		this.direction = direction > 0;
		this.maxSpeed = maxSpeed;
		this.PID = new Speed(0, 0);
	}
	
	/*
	 * returns a speed object containing the speeds from -1 to 1 for each motor
	 */
	public abstract Speed getSpeed();
	public abstract boolean isComplete();
	public void resetStartPos(){
	}
}
