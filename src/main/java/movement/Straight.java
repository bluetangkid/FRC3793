package movement;

import org.usfirst.frc.team3793.robot.Motors;
import org.usfirst.frc.team3793.robot.Sensors;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;

/**
 * class to calculate the desired speed to make the robot go in a straight line
 * 
 * @author Warren Funk and Ethan Durham
 */
public class Straight extends MovementAction implements PIDOutput {
	float distance;
	Point startPos;

	final static float kP = .23f;
	final static float kI = 0;
	final static float kD = 0;
	final static float kF = 0.0f;
	final static float kTolerance = 0;
	private double speedMult = 1;

	/*
	 * moves a specified distance in meters, forward or backwards with a negative or
	 * positive distance
	 */
	public Straight(float distance, float maxSpeed) {
		super((int) Math.signum(distance), maxSpeed);
		this.distance = distance;
		System.out.println(" straight trying to initialize");
		degrees = Sensors.navX.getYaw();
		startPos = new Point(Motors.talonLeft.getSelectedSensorPosition(0),
				Motors.talonRight.getSelectedSensorPosition(0));
		controller = new PIDController(kP, kI, kD, kF, Sensors.navX, this, 0.005);
		controller.setInputRange(-180.0f, 180.0f);
		controller.setOutputRange(0.8f, 1.2f);
		controller.setAbsoluteTolerance(kTolerance);
		controller.setContinuous(true);
		controller.setSetpoint(degrees);
		controller.enable();
	}

	/**
	 * @return {@link Speed} required to go in a straight line
	 */
	public Speed getSpeed() {
		if (distTraveled() / distance > .8) {
			speedMult -= .05;
		}
		return PID;
	}

	/**
	 * @return the distance traveled so far by the robot
	 */
	private double distTraveled() {
		return (startPos.getDist(new Point(Motors.talonLeft.getSelectedSensorPosition(0),
				Motors.talonRight.getSelectedSensorPosition(0))) / 4096) * 0.15 * Math.PI;
	}

	/**
	 * @return whether or not the action is complete
	 */
	public boolean isComplete() {
		if (distTraveled() >= distance) {
		System.out.println(" Straight Complete");
		}
		return distTraveled() >= distance;
	}

	@Override
	public void pidWrite(double output) {
		PID = new Speed(maxSpeed * output, -maxSpeed);
	}
	public void resetStartPos(){
		super.resetStartPos();
		degrees = Sensors.navX.getYaw();
		controller.setSetpoint(degrees);
		startPos = new Point(Motors.talonLeft.getSelectedSensorPosition(0),
				Motors.talonRight.getSelectedSensorPosition(0));
				System.out.println("Reset Start Position");
	}
}
