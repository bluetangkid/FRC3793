package org.usfirst.frc.team3793.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SerialPort;

/**
 * Similar to {@link Motors}, this class stores all of the sensors to make them easier to find.
 * @author Warren Funk
 *
 */
public class Sensors {
	public static DigitalInput vacuumPivotSwitch;
	public static AHRS navX;
	public static SerialPort jeVois;
	
	public static void initialize() {
		vacuumPivotSwitch = new DigitalInput(4);
		navX = new AHRS(SerialPort.Port.kUSB);
		jeVois = new SerialPort(921600, SerialPort.Port.kUSB1);
	}
}
