package org.usfirst.frc.team3793.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.VideoMode.PixelFormat;

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

	public static Ultrasonic backDist;

	public static UsbCamera visionCam;
	public static MjpegServer camServer;

	public static DigitalInput avocadoLimit;
	public static DigitalInput beltLimit;
	
	public static void initialize() {
		vacuumPivotSwitch = new DigitalInput(4);
		System.out.println(SerialPort.Port.values());
		
		avocadoLimit = new DigitalInput(RobotMap.AVOCADO_LIMIT_SWITCH.getPin());
		beltLimit = new DigitalInput(RobotMap.BELT_LIMIT.getPin());
		jeVois1 = new SerialPort(115200, SerialPort.Port.kUSB);

		navX = new AHRS(SPI.Port.kMXP);
	}
}