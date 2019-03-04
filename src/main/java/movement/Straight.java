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
	double xPos;
	double yPos;
	double time;

	final static float kP = .3f;
	final static float kI = 0;
	final static float kD = 1.9f;
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
		System.out.println(" straight trying its hardest to initialize");
		degrees = Sensors.navX.getYaw();
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
		if (distance - distTraveled() < 0.8 && speedMult >.5) {
			speedMult -= .05;
		}
		return PID;
	}

	/**
	 * @return the distance traveled so far by the robot
	 */
	private double distTraveled() {
		double num = (Math.sqrt(xPos*xPos + yPos*yPos) / 4096d) * 1.5d * Math.PI;
		System.out.println(num);
		return num;
	}

	/**
	 * @return whether or not the action is complete
	 */
	public boolean isComplete() {
		if (distTraveled() >= distance) {
			System.out.println(" Straight Complete");
			MovementController.addAction(new Turn(degrees - Sensors.navX.getYaw(), 0.8f));
		}
		return distTraveled() >= distance;
	}
	//nice
	@Override
	public void pidWrite(double output) {
		xPos += Motors.talonRight.getSelectedSensorVelocity(0) * (System.currentTimeMillis() - time) * (1d/1000d);
		yPos += Motors.talonLeft.getSelectedSensorVelocity(0) * (System.currentTimeMillis() - time) * (1d/1000d);
		time = System.currentTimeMillis();
		PID = new Speed(maxSpeed * output * speedMult, -maxSpeed * (1/output) * speedMult);
	}
	public void resetStartPos(){
		super.resetStartPos();
		degrees = Sensors.navX.getYaw();
		controller.setSetpoint(degrees);
		System.out.println("Reset Start Position");
	}
}