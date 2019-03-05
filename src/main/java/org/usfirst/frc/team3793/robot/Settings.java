package org.usfirst.frc.team3793.robot;

/**
 * Basic settings for the robot. {@code public static final objects} ONLY.
 * Hopefully this reduces magic numbers.
 * @author Warren Funk
 *
 */
public interface Settings {
	public static final double SPEED_MULT = 1.0f;
	public static final double TURN_MULT = 0.6f;
	public static final float LSTICK_DEADZONE = 0.3f;
	public static final float BUMPER_DEADZONE = 0.05f;
	public static final int TIMER_DELAY = 15;
	public static final float INCHES_TO_METERS = .0254f;
	public static final double BLUE = .87;
	public static final double RED = .61;
	public static final double YELLOW = .69;
	public static final double GREEN = .77;
	public static final double WHITE = .93;
	public static final double PURPLE = .91;
	public static final double HOT_PINK = .57;
	public static final double WAVE_FOREST = -.37;
	public static final double LIME = .73;
}
