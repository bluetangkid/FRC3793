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
	
	public static DigitalInput switch1;
	public static DigitalInput switch2;
	public static DigitalInput switch3;
	public static DigitalInput switch4;
	public static DigitalInput vacuumPivotSwitch;
	public static AHRS navX;
	public static SerialPort jeVois;
	
	public static void initialize() {
		switch1 = new DigitalInput(0);
		switch2 = new DigitalInput(1);
		switch3 = new DigitalInput(2);
		switch4 = new DigitalInput(3);
		vacuumPivotSwitch = new DigitalInput(4);
		navX = new AHRS(SerialPort.Port.kUSB);
		jeVois = new SerialPort(921600, SerialPort.Port.kUSB1);
	}
}
