package movement;

import org.usfirst.frc.team3793.robot.Motors;
import org.usfirst.frc.team3793.robot.Sensors;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;

/**
 * class to calculate the desired speed to make the robot go in a straight line
 * @author Warren Funk
 */
public class Straight extends MovementAction implements PIDOutput{
	float distance;
	Point startPos;
	PIDController straightPID;
	
	final static float kP = 0.23f;
	final static float kI = 0;
	final static float kD = 0;
	final static float kF = 0.0f;
	final static float kTolerance = 0;
	
	/*
	 * moves a specified distance in meters, forward or backwards with a negative or positive distance
	 */
	public Straight(float distance, float maxSpeed) {
		super((int)Math.signum(distance), maxSpeed);
		this.distance = distance;
		degrees = Sensors.navX.getYaw();
		startPos = new Point(Motors.talonLeft.getSelectedSensorPosition(0), Motors.talonRight.getSelectedSensorPosition(0));
		straightPID = new PIDController(kP, kI, kD, kF, Sensors.navX, this, 0.005);
		straightPID.setInputRange(-180.0f,  180.0f);
		straightPID.setOutputRange(0.8f, 1.2f);
		straightPID.setAbsoluteTolerance(kTolerance);
		straightPID.setContinuous(true);
	    straightPID.setSetpoint(degrees);
	    straightPID.enable();
	}
	
	/**
	 * @return {@link Speed} required to go in a straight line
	 */
	public Speed getSpeed() {
		return PID;
	}
	
	/**
	 * @return the distance traveled so far by the robot
	 */
	private double distTraveled() {
		return (startPos.getDist(new Point(Motors.talonLeft.getSelectedSensorPosition(0), Motors.talonRight.getSelectedSensorPosition(0)))/4096)*0.15*Math.PI;
	}
	
	/**
	 * @return whether or not the action is complete
	 */
	public boolean isComplete() {
		return distTraveled() >= distance;
	}

	@Override
	public void pidWrite(double output) {
		PID = new Speed(maxSpeed*output, maxSpeed);
	}
}
