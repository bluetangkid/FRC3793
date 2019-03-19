package org.usfirst.frc.team3793.robot;

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

	public static Spark blinkin;

	public static Spark armMotor;
	public static Talon avocadoMotor;
	public static Spark beltMotor;
	public static Spark blinkin2019;

	public static Compressor compressor;
	public static Solenoid avocadoSlide;
	public static Solenoid landingGear2;
	public static Solenoid landingGear3;
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
		Motors.talonLeft.enableCurrentLimit(true);
		Motors.talonRight.enableCurrentLimit(true);
		victorLeft = new WPI_VictorSPX(RobotMap.VICTOR_LEFT.getPin());
		victorRight = new WPI_VictorSPX(RobotMap.VICTOR_RIGHT.getPin());
		left = new SpeedControllerGroup(talonLeft, victorLeft);
		right = new SpeedControllerGroup(talonRight, victorRight);
		drive = new DifferentialDrive(left, right);
		drive.setSafetyEnabled(false);
		drive.setDeadband(0);

		armEndMotor = new WPI_VictorSPX(RobotMap.END_ARM_MOTOR.getPin());
		
		// ------------------------------ Other Motors ------------------------------

		// blinkin = new Spark(RobotMap.BLINKIN.getPin());

		armMotor = new Spark(RobotMap.ARM_MOTOR.getPin());
		// nice
		avocadoMotor = new Talon(RobotMap.AVACADO_MOTOR.getPin());

		beltMotor = new Spark(RobotMap.BELT_MOTOR.getPin());
		try{
		blinkin2019 = new Spark(RobotMap.BLINKIN.getPin());
		}catch(Exception e){
			e.printStackTrace();
		}
		compressor = new Compressor(RobotMap.COMPRESSOR.getPin());
		avocadoSlide = new Solenoid(RobotMap.AVACADO_SLIDE.getPin());
		landingGear2 = new Solenoid(RobotMap.LANDING_GEAR2.getPin());
		landingGear3 = new Solenoid(RobotMap.LANDING_GEAR3.getPin());
		hinge = new Solenoid(RobotMap.HINGE.getPin());

		Motors.victorRight.follow(Motors.talonRight);
		Motors.victorLeft.follow(Motors.talonLeft);
	}
}
