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
	public static final float BLUE = .87f;
	public static final float RED = .61f;
	public static final float YELLOW = .69f; //nice
	public static final float GREEN = .77f;
	public static final float WHITE = .93f;
	public static final float PURPLE = .91f;
	public static final float HOT_PINK = .57f;
	public static final float WAVE_FOREST = -.37f;
	public static final float LIME = .73f;
}
