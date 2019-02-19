package org.usfirst.frc.team3793.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Ultrasonic;

/**
 * Similar to {@link Motors}, this class stores all of the sensors to make them easier to find.
 * @author Warren Funk
 *
 */
public class Sensors {
	public static DigitalInput vacuumPivotSwitch;
	public static AHRS navX;
	public static SerialPort jeVois1;
	public static SerialPort jeVois2;
	public static SerialPort jeVoisTracking;

	public static Ultrasonic backDist;

	public static DigitalInput avocadoLimit;
	public static DigitalInput beltLimit;
	
	public static void initialize() {
		vacuumPivotSwitch = new DigitalInput(4);
		System.out.println(SerialPort.Port.values());
		navX = new AHRS(SerialPort.Port.kUSB2);
		
		//backDist = new Ultrasonic(1, 1);
		avocadoLimit = new DigitalInput(RobotMap.AVOCADO_LIMIT_SWITCH.getPin());
		beltLimit = new DigitalInput(RobotMap.BELT_LIMIT.getPin());
		jeVois1 = new SerialPort(921600, SerialPort.Port.kUSB1);
		//jeVois2 = new SerialPort(921600, SerialPort.Port.kUSB2);
		//jeVoisTracking = new SerialPort(921600, SerialPort.Port.kUSB);
	}
}