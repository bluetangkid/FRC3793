package org.usfirst.frc.team3793.robot;

import java.net.DatagramSocket;
import java.net.InetAddress;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import movement.MovementController;
import movement.Turn;
import movement.AvocadoSlide;
import movement.AvocadoTurn;

//Equation for Drift on tile where y is drift in clicks and x is velocity in clicks/100 ms
// Y=7.029608995X - 592.3469424, where domain is defined on (90,1700)
/**
 * Main Robot class. Does networking and Teleop control by thinking very hard
 * and very carefully.
 * 
 * @author Faris for teleop control, Warren for networking, FIRST provided an
 *         empty class template
 */

// default green, avocado down, avocado up, ball going up, ball going down
public class Robot extends TimedRobot {

	// Controller initialization
	static GenericHID driverController = new XboxController(0);
	static GenericHID operatorController = new XboxController(1);
	public GenericHID[] controllers = new GenericHID[2];
	private boolean singleControllerMode = true;
	public int controllerSelector = 0;
	public GenericHID Master = null;
	public float degToBall = 0;
	public float degToTape = 0;

	static RoboState state = RoboState.RobotInit;
	static Thread t;

	public static float targetDegrees;
	public static float targetDistance;

	static int switchNum = 0;

	static String gameData;

	static DatagramSocket socket;
	static InetAddress pi;

	public static final int DRIVER = 0;
	public static final int OPERATOR = 1;

	// avocado initialization
	static int avocadoRotationTimer = 0;
	static long timeAvocado = 0;

	static int avocadoPos = 180;
	static boolean isAvocadoTurning = false;

	static boolean avocadoLimitFunctions = true;

	static final int TIMER_DELAY = 15; // nice

	public static toggleSwitch avocadoSlideSwitch;

	static toggleSwitch hingeSwitch;

	static toggleSwitch landingGearSwitch;

	// beltstates
	static BeltController beltController;

	static SmartDashboard dashboard;
	// static PowerDistributionPanel pdp;
	static double minVoltage = 30;

	static boolean hasDone = false;

	static int rightBumperTimer = 0;
	static int leftBumperTimer = 0;

	static boolean rightBumperEngaged = false;
	static boolean leftBumperEngaged = false;

	public static boolean avocadoUp = true;
	public static boolean beltMovingUp = false;
	public static boolean beltMovingDown = false;

	@Override
	public void robotInit() {
		Motors.initialize();
		Sensors.initialize();

		controllers[DRIVER] = driverController;
		controllers[OPERATOR] = operatorController;
		if (singleControllerMode) {
			Master = controllers[DRIVER];
			System.out.println("controller 0");
		}
		Motors.compressor.setClosedLoopControl(false);
		// Motors.landingGear.set(false);
		// Motors.avocadoSlide.set(false);

		state = RoboState.RobotInit;
		t = new MovementController(this);
		t.start();
	}

	@Override
	public void disabledInit() {
		state = RoboState.Disabled;
		// Motors.blinkin.set(-0.59);
		Motors.compressor.setClosedLoopControl(true);
		// Motors.landingGear.set(false);
		// Motors.avocadoSlide.set(false);
	}

	@Override
	public void disabledPeriodic() {
		state = RoboState.Disabled;
		Scheduler.getInstance().run();
	}

	@Override
	public void autonomousInit() {
		state = RoboState.AutonomousInit;
		// Motors.blinkin.set(-0.43);

		grabHatch();

		gameData = DriverStation.getInstance().getGameSpecificMessage();

		// Motors.compressor.setClosedLoopControl(false);
		// Motors.avocadoSlide.set(false);
	}

	@Override
	public void autonomousPeriodic() {
		state = RoboState.Autonomous;
		Scheduler.getInstance().run();
		// System.out.println(Sensors.navX.getYaw()+ "autoPeriodic");
		try {
			Motors.blinkin2019.set(setColors());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void teleopInit() {
		beltController = new BeltController(controllers[OPERATOR], Motors.beltMotor, ControllerMap.X, ControllerMap.B);

		try {
			hingeSwitch = new toggleSwitch(controllers[OPERATOR], ControllerMap.RB, Motors.hinge,
					Solenoid.class.getMethod("set", boolean.class));
			avocadoSlideSwitch = new toggleSwitch(controllers[OPERATOR], ControllerMap.A, Motors.avocadoSlide,
					Solenoid.class.getMethod("set", boolean.class));
			landingGearSwitch = new toggleSwitch(controllers[OPERATOR], ControllerMap.back, Motors.landingGear,
					Solenoid.class.getMethod("set", boolean.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
		state = RoboState.TeleopInit;
	}

	@Override
	public void teleopPeriodic() {
		degreeSync();
		if (singleControllerMode && Master.getRawButton(ControllerMap.leftClick)) {
			controllerSelector++;
			GenericHID c;
			if (controllerSelector > controllers.length - 1) {

				controllerSelector = DRIVER;
				c = controllers[DRIVER];
				controllers[DRIVER] = controllers[OPERATOR];
				controllers[OPERATOR] = c;
			}
			if (controllerSelector == OPERATOR) {
				c = controllers[OPERATOR];
				controllers[OPERATOR] = controllers[DRIVER];
				controllers[DRIVER] = c;
			}
			System.out.println(controllerSelector + " controllerSelector");
			Master = controllers[controllerSelector];
		}

		state = RoboState.Teleop;

		Scheduler.getInstance().run();

		// ---------------------------- ARCADE DRIVE ----------------------------

		try {
			if (!rightBumperEngaged && !leftBumperEngaged) {
				driveControl(); // work Driver
			}
			avocadoControl(); // both work operator
			climbingArm(); // operator RIGHT STICK
			// cargoIntake(); // operator
			beltController.update(); // operator X - UP AND B - DOWN Button
			hingeSwitch.update();// opperator Y button
			landingGearSwitch.update();
			rightBumper(); // driver
			leftBumper(); // driver
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------
		// Motors.blinkin.set(-0.01);
		try {
			Motors.blinkin2019.set(setColors());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void rightBumper() {
		if (controllers[DRIVER].getRawButton(ControllerMap.RB) && !rightBumperEngaged) {
			rightBumperEngaged = true;
			moveToBall();
		}

		if (!controllers[DRIVER].getRawButton(ControllerMap.RB) && rightBumperEngaged) {
			rightBumperEngaged = false;
			MovementController.clearActions();
		}
	}

	public void leftBumper() {
		if (controllers[DRIVER].getRawButton(ControllerMap.LB) && !leftBumperEngaged) {
			leftBumperEngaged = true;
			moveToHatch();
		}

		if (!controllers[DRIVER].getRawButton(ControllerMap.LB) && leftBumperEngaged) {
			leftBumperEngaged = false;
			MovementController.clearActions();
		}
	}

	public boolean masterIsDriver() {
		return Master == controllers[DRIVER];
	}

	public boolean masterIsOperator() {
		return Master == controllers[OPERATOR];
	}

	public void avocadoTurningControl() {
		// 1330 miliseconds, doesn't work
		if (isAvocadoTurning = true) {
			// Motors.blinkin2019.set();
		}
		avocadoRotationTimer++;
		if (avocadoRotationTimer > TIMER_DELAY) {
			avocadoRotationTimer = TIMER_DELAY;
		}

		if (avocadoRotationTimer == TIMER_DELAY && controllers[OPERATOR].getRawButton(ControllerMap.Y)) {
			avocadoRotationTimer = 0;
			timeAvocado = System.currentTimeMillis();
			isAvocadoTurning = true;
		}

		if (Sensors.avocadoLimit.get() && avocadoRotationTimer == TIMER_DELAY) {
			if (avocadoUp) {
				avocadoUp = false;
			} else {
				avocadoUp = true;
			}
			isAvocadoTurning = false;
		}

		if (isAvocadoTurning) {
			if (System.currentTimeMillis() - timeAvocado > 1600)
				isAvocadoTurning = false;
			Motors.avocadoMotor.set(-1);
		} else {
			timeAvocado = 0;
			Motors.avocadoMotor.set(0);
		}
	}

	private void avocadoControl() {
		avocadoSlideSwitch.update(); // Operator A
		avocadoTurningControl(); // operator Y
	}

	private void landingGear() {
		if (!Motors.landingGear.get() && controllers[OPERATOR].getRawButton(ControllerMap.start)) {
			Motors.landingGear.set(true);// extend
		}

		if (Motors.landingGear.get() && controllers[OPERATOR].getRawButton(ControllerMap.back)) {
			Motors.landingGear.set(false);// retract
		}
	}

	private void climbingArm() {
		double armPivot = controllers[OPERATOR].getRawAxis(ControllerMap.leftY);
		double armSpin = controllers[OPERATOR].getRawAxis(ControllerMap.rightY);

		if (Math.abs(armPivot) > .1) {
			Motors.armMotor.set(armPivot * .6);
		} else {
			Motors.armMotor.set(0);
		}
		if (Math.abs(armSpin) > .1) {
			Motors.armEndMotor.set(armSpin * .9);
		} else {
			Motors.armEndMotor.set(0);
		}
	}

	private void cargoIntake() {
		double dif = Math.signum(Math.pow(controllers[DRIVER].getRawAxis(ControllerMap.rightX), 3));
		if (Math.abs(dif) < 0.1)
			dif = 0.0;

		Motors.beltMotor.set(dif);

		beltController.update();
	}

	private void driveControl() {
		double dif;
		double leftY = controllers[DRIVER].getRawAxis(ControllerMap.leftTrigger)
				- controllers[DRIVER].getRawAxis(ControllerMap.rightTrigger);
		if (Math.abs(leftY) < Settings.BUMPER_DEADZONE)
			dif = 0.0;
		else
			dif = Math.signum(Math.pow(leftY, 7));
		System.out.println(leftY + " " + dif);

		double lx = controllers[DRIVER].getRawAxis(ControllerMap.leftX);
		double lNum;
		if (Math.abs(lx) > Settings.LSTICK_DEADZONE)
			lNum = Math.pow(controllers[DRIVER].getRawAxis(ControllerMap.leftX), 3);
		else
			lNum = 0;

		if (lNum == 0 && dif == 0 && Motors.talonLeft.getSelectedSensorVelocity(0) > 100)
			Motors.drive.arcadeDrive(0, 0);
		else
			Motors.drive.arcadeDrive(lNum * Settings.TURN_MULT, -dif * Settings.SPEED_MULT, false);
		// Try curvature with fast turn
	}

	public void degreeSync() {
		String[] info = null;
		try {
			info = Sensors.jeVois1.readString().split(",");
			for (int i = 0; i < info.length; i++) {
				String temp = info[i];
				if (temp.length() > 1) {
					System.out.println(temp);
					if (temp.startsWith("B")) {
						degToBall = Float.parseFloat(temp.substring(1));
					} else {
						degToTape = Float.parseFloat(temp.substring(1));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void moveToBall() {
		float angle = degToBall;
		MovementController.addAction(new Turn(180 - angle, .8f));
	}

	public void moveToHatch() {
		float angle = degToTape;
		MovementController.addAction((new Turn(180 - angle, .8f)));
		double distance = 2;// (double) Sensors.backDist.getRangeInches() * INCHES_TO_METERS;
		if (angle > 0) {
			// MovementController.addAction((new Turn(90 - angle, .8f)));
			// MovementController.addAction(new Straight((float) (Math.cos(Math.toRadians(90
			// - angle)) * distance), .8f));
			// MovementController.addAction((new Turn(-90, .8f)));
			// MovementController.addAction(new Straight((float) (Math.sin(Math.toRadians(90
			// - angle)) * distance), .8f));
		} else {
			// MovementController.addAction((new Turn(-90 - angle, .8f)));
			// MovementController.addAction(new Straight((float)
			// (Math.cos(Math.toRadians(-90 - angle)) * distance), .8f));
			// MovementController.addAction((new Turn(90, .8f)));
			// MovementController.addAction(new Straight((float)
			// (Math.sin(Math.toRadians(-90 - angle)) * distance), .8f)); }
		}
	}

	public void grabHatch() {
		System.out.println("grabbing hatch");
		MovementController.addAction(new AvocadoSlide(0, 0, this));
		MovementController.addAction(new AvocadoTurn(0, 0, this));
		MovementController.addAction(new AvocadoSlide(0, 0, this));
	}

	public double setColors() {
		double color = Settings.HOT_PINK;

		// if (avocadoUp && !isAvocadoTurning) {
		// 	color = Settings.LIME;
		// }
		// if (!avocadoUp && !isAvocadoTurning) {
		// 	color = Settings.GREEN;
		// }
		// if (isAvocadoTurning) {
		// 	color = Settings.WAVE_FOREST;
		// } else 
		if (beltMovingUp()) {
			color = Settings.BLUE;
		} else if (beltMovingDown()) {
			color = Settings.RED;
		}
		return color;
	}

	public boolean beltMovingUp() {
		return controllers[OPERATOR].getRawButton(ControllerMap.X);
	}

	public boolean beltMovingDown() {
		return controllers[OPERATOR].getRawButton(ControllerMap.B);
	}

	@Override
	public void testPeriodic() {
	}

	public static synchronized RoboState getState() {
		return state;
	}
}
