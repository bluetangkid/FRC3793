package org.usfirst.frc.team3793.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;

/**
 * A nice list of all the motors on the robot to be referenced in the appropriate place
 * @author Warren Funk
 *
 */
public class Motors {
	
	// ----------------------------- Driving Motors -----------------------------
	
	public static WPI_TalonSRX talonLeft;
	public static WPI_TalonSRX talonRight;
	private static WPI_VictorSPX victorLeft;
	private static WPI_VictorSPX victorRight;
	private static SpeedControllerGroup left;
	private static SpeedControllerGroup right;
	public static DifferentialDrive drive;
	
	// ------------------------------ Other Motors ------------------------------
	
	public static Spark scissorMotorOne;
	public static Spark scissorMotorTwo;
	public static SpeedControllerGroup scissorMotors;

	public static VictorSP vacuumPivotMotor;
	public static Talon vacuumMotor;

	public static VictorSP cubeMotorLeft;
	public static Talon cubeMotorRight;
	public static SpeedControllerGroup cubeMotors;
	
	public static Spark blinkin;

	// public static Spark armMotor;
	// public static Talon avocadoMotor;
	// public static Spark beltMotor;
	// public static Spark blinkin2019;

	// public static Compressor compressor;
	// public static Solenoid avocadoSlide;
	// public static Solenoid landingGear;
	// public static Solenoid hippy;
		
	/**
	 * initializes all of the motors using the pins as specified in {@link RobotMap}
	 */
	public static void initialize() {
		// ----------------------------- Driving Motors -----------------------------

		talonLeft = new WPI_TalonSRX(RobotMap.TALON_LEFT.getPin());
		talonRight = new WPI_TalonSRX(RobotMap.TALON_RIGHT.getPin());
		victorLeft = new WPI_VictorSPX(RobotMap.VICTOR_LEFT.getPin());
		victorRight = new WPI_VictorSPX(RobotMap.VICTOR_RIGHT.getPin());
		left = new SpeedControllerGroup(talonLeft, victorLeft);
		right = new SpeedControllerGroup(talonRight, victorRight);
		drive = new DifferentialDrive(left, right);
		drive.setSafetyEnabled(false);

		// ------------------------------ Other Motors ------------------------------

		scissorMotorOne = new Spark(RobotMap.SCISSOR1.getPin());
		scissorMotorTwo = new Spark(RobotMap.SCISSOR2.getPin());
		scissorMotors = new SpeedControllerGroup(scissorMotorOne, scissorMotorTwo);

		vacuumPivotMotor = new VictorSP(RobotMap.VACUUM_PIVOT.getPin());
		vacuumMotor = new Talon(RobotMap.VACUUM_MOTOR.getPin());

		cubeMotorLeft = new VictorSP(RobotMap.INTAKE_LEFT.getPin());
		cubeMotorRight = new Talon(RobotMap.INTAKE_RIGHT.getPin());
		cubeMotors = new SpeedControllerGroup(cubeMotorLeft, cubeMotorRight);

		blinkin = new Spark(RobotMap.BLINKIN.getPin());

		// armMotor = new Spark(RobotMap.ARM_MOTOR.getPin());
		
		// avocadoMotor = new Talon(RobotMap.AVACADO_MOTOR.getPin());
		
		// beltMotor = new Spark(RobotMap.BELT_MOTOR.getPin());
		// blinkin2019 = new Spark(RobotMap.BLINKIN_2019.getPin());

		// compressor = new Compressor(RobotMap.COMPRESSOR.getPin());
		// avocadoSlide = new Solenoid(RobotMap.AVACADO_SLIDE.getPin());
		// landingGear = new Solenoid(RobotMap.LANDING_GEAR.getPin());
		// hippy = new Solenoid(RobotMap.HIPPY.getPin());
		
		Motors.victorRight.follow(Motors.talonRight);
		Motors.victorLeft.follow(Motors.talonLeft);
	}
}
