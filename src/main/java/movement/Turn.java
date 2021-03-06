package movement;

import org.usfirst.frc.team3793.robot.Motors;
import org.usfirst.frc.team3793.robot.Sensors;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;

/**
 * Class to cause a desired stationary turn to a specified number of degrees
 * 
 * @author Warren Funk, Ethan Durham, Faris Prasetiawan
 *
 */
public class Turn extends DriveAction implements PIDOutput {
	// 0.58s oscillaty
	final static float kP = 0.1f;// .05
	final static float kI = 0f;// .000001
	final static float kD = 1.6f;// 1
	final static float kF = 0f;
	final static float kTolerance = 1;
	public int framedoodad = 0;
	public float targetDegrees = 0;
	private double output;

	public Turn(float degrees, float maxSpeed) {
		super((int) Math.signum(degrees), maxSpeed);
		System.out.println(" Initializing Turn");
		this.degrees = Sensors.navX.getYaw() + (degrees);
		targetDegrees = degrees;
		if (this.degrees > 180)
			this.degrees = -180 + this.degrees % 180;
		else if (this.degrees < -180)
			this.degrees = 180 - this.degrees % 180;

		controller = new PIDController(kP, kI, kD, kF, Sensors.navX, this, 0.005);
		controller.setInputRange(-180.0f, 180.0f);
		controller.setOutputRange(-1.0, 1.0);
		controller.setAbsoluteTolerance(kTolerance);
		controller.setContinuous(true);
		controller.setSetpoint(this.degrees);
		controller.enable();
	}

	/**
	 * @return whether or not the turn is within tolerance according to the PID
	 *         controller
	 */
	public boolean isComplete() {
		if (onSetpoint()) framedoodad++;
		else framedoodad = 0;

		if (framedoodad > 9) {
			System.out.println("Turn is Complete");
			return true;
		}
		return false;
	}

	@Override
	public void pidWrite(double output) { 
		this.output = output;
	}

	public void set(){
		Motors.drive.tankDrive(maxSpeed * output, -maxSpeed * output);
	}

	public boolean onSetpoint() {
		return Sensors.navX.getYaw() > controller.getSetpoint() - 3
			&& Sensors.navX.getYaw() < controller.getSetpoint() + 3;
	}

	public void resetStartPos() {
		this.degrees = Sensors.navX.getYaw() + (targetDegrees);
		if (this.degrees > 180) {
			this.degrees = -180 + this.degrees % 180;
		} else if (this.degrees < -180) {
			this.degrees = 180 - this.degrees % 180;
		}
		System.out.println(controller.getSetpoint() + " Pre reset setpoint");
		controller.setSetpoint(this.degrees);
		System.out.println(controller.getSetpoint() + " Post reset setpoint");
	}
}
