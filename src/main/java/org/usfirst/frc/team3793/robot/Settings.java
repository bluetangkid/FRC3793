package org.usfirst.frc.team3793.robot;

/**
 * Basic settings for the robot. {@code public static final objects} ONLY.
 * Hopefully this reduces magic numbers.
 * @author Warren Funk
 *
 */
public interface Settings {
	public static final float SPEED_MULT = 1.0f;
	public static final float TURN_MULT = 0.6f;
	public static final float LSTICK_DEADZONE = 0.35f;
	public static final float BUMPER_DEADZONE = 0.05f;
	public static final int TIMER_DELAY = 15;
}
