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
	TALON_LEFT(41), 
	TALON_RIGHT(42), 
	VICTOR_LEFT(43), 
	VICTOR_RIGHT(44), 

	// end of arm motor
	END_ARM_MOTOR(45),

	//ARM MOTOR - flip down to push front end of robot up to climb up platform
	ARM_MOTOR(8),

	//AVOCADO - avocado shaped object that rotates/ slides out to pick up a latch
	AVACADO_MOTOR(6),
	AVACADO_SLIDE(0),
	AVOCADO_LIMIT_SWITCH(8),

	//BELT - succ up ball
	BELT_MOTOR(7),
	BELT_LIMIT(9),
	//BLINKIN - lights
	//BLINKIN(6), // 9

	//COMPRESSOR - pneumatics compressor
	COMPRESSOR(0),
	
	//LANDING GEAR - used to lift up back end of robot to climb
	LANDING_GEAR(2),

	//HIPPY - used to change the height of the output of ball.
	HINGE(1),

	END(1);
	
	private int pinNum;
	RobotMap(int num) {
		pinNum = num;
	}
	
	public int getPin() {
		return pinNum;
	}
}
