package movement;

import org.usfirst.frc.team3793.robot.Sensors;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;

/**
 * Class to cause a desired stationary turn to a specified number of degrees
 * 
 * @author Warren Funk, Ethan Durham, Faris Prasetiawan
 *
 */
public class Turn extends MovementAction implements PIDOutput {
	// 0.58s oscillaty
	final static float kP = .05f;// .175
	final static float kI = 0.00000f;// .000001
	final static float kD = 1f;// 1
	final static float kF = 0f;
	final static float kTolerance = 1;
	public int framedoodad = 0;

	public Turn(float degrees, float maxSpeed) {
		super((int) Math.signum(degrees), maxSpeed);
		System.out.println(" Initializing Turn");
		this.degrees = Sensors.navX.getYaw() + (degrees);

		if (this.degrees > 180) {
			this.degrees = -180 + this.degrees % 180;
		} else if (this.degrees < -180) {
			this.degrees = 180 - this.degrees % 180;
		}

		controller = new PIDController(kP, kI, kD, kF, Sensors.navX, this, 0.005);
		controller.setInputRange(-180.0f, 180.0f);
		controller.setOutputRange(-1.0, 1.0);
		controller.setAbsoluteTolerance(kTolerance);
		controller.setContinuous(true);
		controller.setSetpoint(this.degrees);
		controller.enable();
	}

	/**
	 * @return required {@link Speed} to turn correctly
	 */
	public Speed getSpeed() {
		return PID;
	}

	/**
	 * @return whether or not the turn is within tolerance according to the PID
	 *         controller
	 */
	public boolean isComplete() {
		// if(onSetpoint()){
		// System.out.println(Sensors.navX.getYaw() + " End of turn");
		// }
		// return onSetpoint();

		if (onSetpoint()) {
			framedoodad++;
		} else
			framedoodad = 0;

		if (framedoodad > 9) {
			System.out.println(" Turn is Complete");
			return true;
		}
		// nice
		return false;
		// if(onSetpoint() && !Sensors.navX.isMoving()){
		// System.out.println(" Baby girl what you doin' where's your man, I just popped
		// a xan, 50,000 in Japan");
		// }
		// return onSetpoint() && !Sensors.navX.isMoving();
	}

	@Override
	public void pidWrite(double output) {
		PID = new Speed(maxSpeed * output, maxSpeed * output);
	}

	public boolean onSetpoint() {

		// if(timer > 0){
		// timer--;
		// }else{
		// timer = 10;
		// //System.out.println(Math.abs(controller.getSetpoint() -
		// Sensors.navX.getYaw()) + " distance from target");
		// }

		return Sensors.navX.getYaw() > controller.getSetpoint() - 3
				&& Sensors.navX.getYaw() < controller.getSetpoint() + 3;
	}

	public void resetStartPos() {
		this.degrees = Sensors.navX.getYaw() + (degrees);
		if (this.degrees > 180) {
			this.degrees = -180 + this.degrees % 180;
		} else if (this.degrees < -180) {
			this.degrees = 180 - this.degrees % 180;
		}
		controller.setSetpoint(this.degrees);
	}
}
