package org.usfirst.frc.team3793.robot;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 * @author Warren Funk
 */
public enum RobotMap {
	// DRIVE MOTORS
	TALON_LEFT(42), 
	TALON_RIGHT(41), 
	VICTOR_LEFT(43), 
	VICTOR_RIGHT(44), 
	// SCISSOR MOTORS
	SCISSOR1(0), 
	SCISSOR2(1), 
	// VACUUM MOTORS
	VACUUM_PIVOT(2), 
	VACUUM_MOTOR(5), 
	// INTAKE MOTORS
	INTAKE_LEFT(3), 
	INTAKE_RIGHT(4), 
	// BLINKIN CONTROLER(S)
	BLINKIN(6);
	
	private int pinNum;
	RobotMap(int num) {
		pinNum = num;
	}
	
	public int getPin() {
		return pinNum;
	}
}
