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
import movement.Straight;
import movement.Turn;
import movement.Point;

//Equation for Drift on tile where y is drift in clicks and x is velocity in clicks/100 ms
// Y=7.029608995X - 592.3469424, where domain is defined on (90,1700)
/**
 * Main Robot class. Does networking and Teleop control by thinking very hard
 * and very carefully.
 * 
 * @author Faris for teleop control, Warren for networking, FIRST provided an
 *         empty class template
 */
public class Robot extends TimedRobot {

	// Controller initialization
	static GenericHID driverController = new XboxController(0);
	static GenericHID operatorController = new XboxController(1);
	public static final float INCHES_TO_METERS = .0254f;
	public GenericHID[] controllers = new GenericHID[2];
	private boolean singleControllerMode = true;
	public int controllerSelector = 0;
	public GenericHID Master = null;

	public float degToBall = 0;
	public float degToTape = 0;

	static RoboState state = RoboState.RobotInit;
	Thread t;

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

	boolean avocadoLimitFunctions = true;

	static final int TIMER_DELAY = 15;

	public toggleSwitch avocadoSlideSwitch;

	toggleSwitch hingeSwitch;

	toggleSwitch landingGearSwitch;

	// beltstates
	BeltController beltController;

	static SmartDashboard dashboard;
	// static PowerDistributionPanel pdp;
	static double minVoltage = 30;

	boolean hasDone = false;

	int rightBumperTimer = 0;
	int leftBumperTimer = 0;

	boolean rightBumperEngaged = false;
	boolean leftBumperEngaged = false;

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
		t = new MovementController();
		t.start();

		// try {
		// socket = new DatagramSocket(5808);
		// socket.setSoTimeout(3);
		// } catch (SocketException e) {
		// e.printStackTrace();
		// }

		// try {
		// pi = InetAddress.getByName("10.37.93.50");
		// } catch (UnknownHostException e) {
		// e.printStackTrace();
		// }
		// pdp = new PowerDistributionPanel();
	}

	@Override
	public void disabledInit() {
		state = RoboState.Disabled;
		// Motors.blinkin.set(-0.59);
		try {
			t.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		gameData = DriverStation.getInstance().getGameSpecificMessage();

		// t = new MovementController();
		// t.start();
		// Motors.compressor.setClosedLoopControl(false);
		// Motors.avocadoSlide.set(false);
	}

	@Override
	public void autonomousPeriodic() {
		SmartDashboard.putString("State", Robot.getState().name());
		SmartDashboard.putNumber("Angle", Sensors.navX.getYaw());

		state = RoboState.Autonomous;
		Scheduler.getInstance().run();
		// System.out.println(Sensors.navX.getYaw()+ "autoPeriodic");
	}

	@Override
	public void teleopInit() {
		beltController = new BeltController(controllers[OPERATOR], Motors.beltMotor, ControllerMap.X, ControllerMap.B);

		try {
			hingeSwitch = new toggleSwitch(controllers[OPERATOR], ControllerMap.RB, Motors.hinge,
					Solenoid.class.getMethod("set", boolean.class));
			avocadoSlideSwitch = new toggleSwitch(controllers[OPERATOR], ControllerMap.A, Motors.avocadoSlide,
					Solenoid.class.getMethod("set", boolean.class));
			//landingGearSwitch = new toggleSwitch(controllers[OPERATOR], ControllerMap.back, Motors.landingGear,
					//Solenoid.class.getMethod("set", boolean.class));

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

		// if(pdp.getVoltage() < minVoltage) minVoltage = pdp.getVoltage();
		// SmartDashboard.putNumber("Min Voltage", minVoltage);

		/*
		 * if(controllers[DRIVER].getBumper(Hand.kRight)) { byte[] data = new byte[1];
		 * data[0] = 1; DatagramPacket p = new DatagramPacket(data, 1, pi, 5808); try
		 * {socket.send(p);} catch (IOException e) {e.printStackTrace();} } byte[] data
		 * = new byte[8]; DatagramPacket p = new DatagramPacket(data, data.length, pi,
		 * 5808); try { socket.receive(p);
		 * 
		 * if(data[0] != 0) { byte[] a = new byte[4]; byte[] b = new byte[4];
		 * 
		 * a[0] = data[0]; a[1] = data[1]; a[2] = data[2]; a[3] = data[3];
		 * 
		 * b[0] = data[4]; b[1] = data[5]; b[2] = data[6]; b[3] = data[7];
		 * 
		 * float floatA = ByteBuffer.wrap(a).order(ByteOrder.LITTLE_ENDIAN).getFloat();
		 * float floatB = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getFloat();
		 * if(floatA - Math.floor(floatA) == 0.151) { targetDegrees =
		 * (float)Math.floor(floatA); targetDistance = floatB; } else { targetDegrees =
		 * (float)Math.floor(floatB); targetDistance = floatA; }
		 * MovementController.addAction(new Turn(targetDegrees, 0.5f));
		 * MovementController.addAction(new Straight(targetDistance*0.8f, 0.5f)); } }
		 * catch (Exception e) { e.printStackTrace(); }
		 */

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
			//landingGearSwitch.update();
			rightBumper(); // driver
			leftBumper(); // driver
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------
		// Motors.blinkin.set(-0.01);
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
			isAvocadoTurning = false;
		}

		if (isAvocadoTurning) {
			if(System.currentTimeMillis() - timeAvocado > 1600) isAvocadoTurning = false;
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
		double armMovement = controllers[OPERATOR].getRawAxis(ControllerMap.rightY);

		if (Math.abs(armMovement) > .1) {
			Motors.armMotor.set(armMovement * .6);
		} else {
			Motors.armMotor.set(0);
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
			dif = Math.signum(Math.pow(leftY, 3));
		System.out.println(leftY + " " + dif);

		// Point stickInput = new Point(controllers[DRIVER].getRawAxis(ControllerMap.leftX),
		// 		controllers[DRIVER].getRawAxis(ControllerMap.leftY));
		// if (stickInput.getDist(new Point(0, 0)) < Settings.LSTICK_DEADZONE)
		// 	stickInput = new Point(0, 0);
		// else
		// 	stickInput = stickInput.normalize().mul(((stickInput.getDist(new Point(0, 0)) - Settings.LSTICK_DEADZONE)
		// 			/ (1 - Settings.LSTICK_DEADZONE)));
		double lx = controllers[DRIVER].getRawAxis(ControllerMap.leftX);
		double lNum;
		if(Math.abs(lx) > Settings.LSTICK_DEADZONE)
			lNum = Math.pow(controllers[DRIVER].getRawAxis(ControllerMap.leftX), 3);
		else lNum = 0;
		
		if(lNum == 0 && dif == 0 && Motors.talonLeft.getSelectedSensorVelocity(0) > 100)
			Motors.drive.arcadeDrive(0, 0);
		else Motors.drive.arcadeDrive(lNum * Settings.TURN_MULT, -dif * Settings.SPEED_MULT, false);
	}

	public void degreeSync() {
		String[] info = null;
		try {
			info = Sensors.jeVois1.readString().split(",");
			for (int i = 0; i < info.length; i++) {
				String temp = info[i];
				if(temp.length() > 1) {
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
		//	MovementController.addAction((new Turn(90 - angle, .8f)));
		//	MovementController.addAction(new Straight((float) (Math.cos(Math.toRadians(90 - angle)) * distance), .8f));
		//	MovementController.addAction((new Turn(-90, .8f)));
		//	MovementController.addAction(new Straight((float) (Math.sin(Math.toRadians(90 - angle)) * distance), .8f));
		} else {
		//	MovementController.addAction((new Turn(-90 - angle, .8f)));
		//	MovementController.addAction(new Straight((float) (Math.cos(Math.toRadians(-90 - angle)) * distance), .8f));
		//	MovementController.addAction((new Turn(90, .8f)));
		//	MovementController.addAction(new Straight((float) (Math.sin(Math.toRadians(-90 - angle)) * distance), .8f));		}
		}
	}

	public void grabHatch(){
		
	}

	@Override
	public void testPeriodic() {
	}

	public static synchronized RoboState getState() {
		return state;
	}
}
