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
public class Turn extends MovementAction implements PIDOutput {
	PIDController turnController;
	// 0.58s oscillaty
	final static float kP = .05f;// .175
	final static float kI = 0.00000f;// .000001
	final static float kD = 1f;//1
	final static float kF = 0f;
	final static float kTolerance = 1;
	public int framedoodad = 0;

	public Turn(float degrees, float maxSpeed) {
		super((int) Math.signum(degrees), maxSpeed);
		System.out.println(Sensors.navX.getYaw() + " Start of turn");
		
		this.degrees = Sensors.navX.getYaw() + (degrees);
		System.out.println(this.degrees + " setPoint Pre wrap");

		if (this.degrees > 180) {
			this.degrees = -180 + this.degrees % 180;
		} else if (this.degrees < -180) {
			this.degrees = 180 - this.degrees % 180;
		}
		System.out.println(this.degrees + " setPoint post wrap");

		turnController = new PIDController(kP, kI, kD, kF, Sensors.navX, this, 0.005);
		turnController.setInputRange(-180.0f, 180.0f);
		turnController.setOutputRange(-1.0, 1.0);
		turnController.setAbsoluteTolerance(kTolerance);
		turnController.setContinuous(true);
		turnController.setSetpoint(this.degrees);
		turnController.enable();
		System.out.println(turnController.getSetpoint() + " setPoint");
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
		// 	System.out.println(Sensors.navX.getYaw() + " End of turn");
		// }
		// return onSetpoint();
		
		if (onSetpoint()) {
			framedoodad++;
			System.out.println(framedoodad + " framedoodad");
		} else framedoodad = 0;

		if (framedoodad > 9) {
			System.out.println(Sensors.navX.getYaw() + " end of turn");
			return true;
		}
		return false;
		// if(onSetpoint() && !Sensors.navX.isMoving()){
			// System.out.println(" Baby girl what you doin' where's your man, I just popped a xan, 50,000 in Japan");
		// }
		// return onSetpoint() && !Sensors.navX.isMoving();
	}

	@Override
	public void pidWrite(double output) {
		PID = new Speed(maxSpeed * output, -maxSpeed * output);
	}

	public boolean onSetpoint() {
		
		// if(timer > 0){
		// 	timer--;
		// }else{
		// 	timer = 10;
		// 	//System.out.println(Math.abs(turnController.getSetpoint() - Sensors.navX.getYaw()) + " distance from target");
		// }

		return Sensors.navX.getYaw() > turnController.getSetpoint() - 3 && Sensors.navX.getYaw() < turnController.getSetpoint() + 3;
	}
}
