package org.usfirst.frc.team3793.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;

/**
 * A nice list of all the motors on the robot to be referenced in the
 * appropriate place
 * 
 * @author Warren Funk
 *
 */
public class Motors {

	// ----------------------------- Driving Motors -----------------------------

	public static WPI_TalonSRX talonLeft;
	public static WPI_TalonSRX talonRight;
	public static WPI_VictorSPX victorLeft;
	public static WPI_VictorSPX victorRight;
	public static WPI_VictorSPX armEndMotor;
	private static SpeedControllerGroup left;
	private static SpeedControllerGroup right;
	public static DifferentialDrive drive;

	// ------------------------------ Other Motors ------------------------------

	public static Spark armMotor;
	public static Spark avocadoMotor;
	public static Talon beltMotor;
	public static Spark blinkin2019;

	public static Compressor compressor;
	public static Solenoid avocadoSlide;
	public static Solenoid landingGearExtend;
	public static Solenoid landingGearRetract;
	public static Solenoid landingGearStop;
	public static Solenoid hinge;

	/**
	 * initializes all of the motors using the pins as specified in {@link RobotMap}
	 */
	public static void initialize() {
		// ----------------------------- Driving Motors -----------------------------

		talonLeft = new WPI_TalonSRX(RobotMap.TALON_LEFT.getPin());
		talonRight = new WPI_TalonSRX(RobotMap.TALON_RIGHT.getPin());
		talonLeft.configPeakCurrentLimit(0);
		talonRight.configPeakCurrentLimit(0);
		talonLeft.configContinuousCurrentLimit(40);
		talonRight.configContinuousCurrentLimit(40);
		talonLeft.enableCurrentLimit(true);
		talonRight.enableCurrentLimit(true);
		victorLeft = new WPI_VictorSPX(RobotMap.VICTOR_LEFT.getPin());
		victorRight = new WPI_VictorSPX(RobotMap.VICTOR_RIGHT.getPin());
		left = new SpeedControllerGroup(talonLeft, victorLeft);
		right = new SpeedControllerGroup(talonRight, victorRight);
		drive = new DifferentialDrive(left, right);
		drive.setDeadband(0);

		armEndMotor = new WPI_VictorSPX(RobotMap.END_ARM_MOTOR.getPin());
		
		// ------------------------------ Other Motors ------------------------------

		// blinkin = new Spark(RobotMap.BLINKIN.getPin());

		armMotor = new Spark(RobotMap.ARM_MOTOR.getPin());
		avocadoMotor = new Spark(RobotMap.AVACADO_MOTOR.getPin());

		beltMotor = new Talon(RobotMap.BELT_MOTOR.getPin());
		try{
		blinkin2019 = new Spark(RobotMap.BLINKIN.getPin());
		}catch(Exception e){
			e.printStackTrace();
		}
		compressor = new Compressor(RobotMap.COMPRESSOR.getPin());
		avocadoSlide = new Solenoid(RobotMap.AVACADO_SLIDE.getPin());
		landingGearExtend = new Solenoid(RobotMap.LANDING_GEAR_EXTEND.getPin());
		landingGearRetract = new Solenoid(RobotMap.LANDING_GEAR_RETRACT.getPin());
		landingGearStop = new Solenoid(RobotMap.LANDING_GEAR_STOP.getPin());
		hinge = new Solenoid(RobotMap.HINGE.getPin());

		Motors.victorRight.follow(Motors.talonRight);
		Motors.victorLeft.follow(Motors.talonLeft);
	}
}