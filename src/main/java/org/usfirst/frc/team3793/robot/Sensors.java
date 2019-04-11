package org.usfirst.frc.team3793.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Ultrasonic;
import util.*;

/**
 * Similar to {@link Motors}, this class stores all of the sensors to make them
 * easier to find.
 * 
 * @author Warren Funk
 *
 */
public class Sensors {
	public static DigitalInput vacuumPivotSwitch;
	public static AHRS navX;
	public static SerialPort jeVois1;
	public static SerialPort jeVois2;

	public static Ultrasonic downDist;

	public static DigitalInput avocadoLimit;
	public static DigitalInput beltLimit;

	public static I2C avocadoLidar;
	public static Lidar lidar;

	public static void initialize() {
		new JeVois().start();
		vacuumPivotSwitch = new DigitalInput(4);
		// beltLimit = new DigitalInput(RobotMap.BELT_LIMIT.getPin());
		lidar = new Lidar();
		lidar.start();
		navX = new AHRS(SPI.Port.kMXP);
	}
}