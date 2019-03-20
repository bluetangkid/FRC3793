package climbing;

import org.usfirst.frc.team3793.robot.Motors;
import movement.*;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;

public class ArmPivot extends MotorAction implements PIDOutput{
    final static float kP = 0.1f;// .05
	final static float kI = 0f;// .000001
	final static float kD = 1f;// 1
    final static float kF = 0f;
    PIDController controller;

    public ArmPivot() {
        super();
        controller = new PIDController(kP, kI, kD, kF, new PitchSource(), this, 0.005f);
		controller.setInputRange(-180.0f, 180.0f);
		controller.setOutputRange(-0.2, 0.2);
		controller.setContinuous(true);
		controller.setSetpoint(this.degrees);
		controller.enable();
    }

    public boolean isComplete() {
        return false;
    }

    public void set() {}

    public void pidWrite(double d){
        Motors.armMotor.set(d+0.4);
    }
}