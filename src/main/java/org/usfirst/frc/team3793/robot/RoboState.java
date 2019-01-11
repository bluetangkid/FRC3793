package org.usfirst.frc.team3793.robot;

/**
 * An Enum used to communicate between the {@link MovementController} and {@link Robot} classes
 * so the {@link MovementController} class knows what is going on
 * @author Warren Funk
 *
 */
public enum RoboState {
	AutonomousInit("AutonomousInit"), TeleopInit("TeleopInit"), RobotInit("RobotInit"), Teleop("Teleop"), Autonomous("Autonomous"), Disabled("Disabled")/*@me*/;
	
	String state;
	RoboState(String state){
		this.state = state;
	}
	public String toString() {
		return state;
	}
}
