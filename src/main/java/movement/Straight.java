package movement;

import org.usfirst.frc.team3793.robot.Sensors;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;

/**
 * class to calculate the desired speed to make the robot go in a straight line
 * @author Warren Funk
 *
 */
public class Straight extends MovementAction implements PIDOutput{
	float distance;
	Point startPos;
	PIDController straightPID;
	
	final static float kP = 0.1f;
	final static float kI = 0.00002f;
	final static float kD = 0.0f;
	final static float kF = 0.0f;
	final static float kTolerance = 0;
	
	/*
	 * moves a specified distance in meters, forward or backwards with a negative or positive distance
	 */
	public Straight(float distance, float maxSpeed) {
		super((int)Math.signum(distance), maxSpeed);
		this.distance = distance;
		degrees = Sensors.navX.getYaw();
		startPos = new Point((float)Sensors.navX.getDisplacementX(), (float)Sensors.navX.getDisplacementY(), (float)Sensors.navX.getDisplacementZ());
		
		straightPID = new PIDController(kP, kI, kD, kF, Sensors.navX, this);
		straightPID.setInputRange(-180.0f,  180.0f);
		straightPID.setOutputRange(-1.0, 1.0);
		straightPID.setAbsoluteTolerance(kTolerance);
		straightPID.setContinuous(true);
	    straightPID.setSetpoint(degrees);
	    straightPID.enable();
	}
	
	
	/**
	 * @return {@link Speed} required to go in a straight line
	 */
	public Speed getSpeed() {
		double pidOut;
		if(direction) {
			pidOut = 1-straightPID.get();
			System.out.println(Sensors.navX.getDisplacementX() + " ::: " + Sensors.navX.getDisplacementY() + " ::: " + Sensors.navX.getDisplacementZ());
			
			return new Speed(maxSpeed, maxSpeed*pidOut);
		}
		pidOut = straightPID.get();
		
		
		return new Speed(-maxSpeed*pidOut, -maxSpeed);
	}
	
	/**
	 * @return the distance traveled so far by the robot
	 */
	private double distTraveled() {
		return startPos.getDist(new Point(Sensors.navX.getDisplacementX(), Sensors.navX.getDisplacementY(), Sensors.navX.getDisplacementZ()));
	}
	
	/**
	 * @return whether or not the action is complete
	 */
	public boolean isComplete() {
		if(distTraveled() >= distance) return true;
		return false;
	}

	@Override
	public void pidWrite(double output) {
		PID = output;
	}
}
