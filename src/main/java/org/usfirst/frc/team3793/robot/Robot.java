package org.usfirst.frc.team3793.robot;

import java.net.DatagramSocket;
import java.net.InetAddress;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import movement.MovementController;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import movement.*;
import util.JeVois;
import org.usfirst.frc.team3793.robot.PowerMonitor;

//Equation for Drift on tile where y is drift in clicks and x is velocity in clicks/100 ms
// Y=7.029608995X - 592.3469424, where domain is defined on (90,1700)
/**
 * Main Robot class. Does networking and Teleop control by thinking very hard
 * and very carefully.
 * 
 * @author Faris for teleop control, Warren for networking & drive control,
 *         FIRST provided an empty class template
 */

// default green, avocado down, avocado up, ball going up, ball going down
public class Robot extends TimedRobot {

	// Controller initialization
	static GenericHID driverController = new XboxController(0);
	static GenericHID operatorController = new XboxController(1);
	public static GenericHID[] controllers = new GenericHID[2];
	private static boolean singleControllerMode = (true);
	public static int controllerSelector = 0;
	private static GenericHID Master = null;

	public static long lastLightSwitch;
	public static boolean colorState;
	public static boolean armColl;
	public static double lastAmp;

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
	static boolean isAvocadoTurning = false;
	static boolean startTurn = false;

	// nice
	public static toggleSwitch avocadoSlideSwitch;

	public static toggleSwitch avocadoRotationSwitch;

	public static ShuffleboardTab main;
	public static NetworkTableEntry avocadoState;
	public static NetworkTableEntry hippieState;
	public static NetworkTableEntry avoSlideState;
	public static NetworkTableEntry LIDARDist;

	static toggleSwitch hingeSwitch;

	public static landingGearController landingGearControl;

	public static toggleSwitch landingGearSwitchExtend;
	public static toggleSwitch landingGearSwitchRetract;
	public static toggleSwitch landingGearSwitchStop;

	static int stabilizeTimer = 0;
	static boolean isOscillating = false;
	static int oscillationTimer = 0;

	// beltstates
	static BeltController beltController;

	static SmartDashboard dashboard;
	public static PowerDistributionPanel pdp;
	static double minVoltage = 30;

	static boolean hasDone = false;

	static boolean rightBumperEngaged = false;
	static boolean leftBumperEngaged = false;
	static int invincibilityTimer = 0;
	public static boolean avocadoUp = (true);
	public static boolean beltMovingUp = false;
	public static boolean beltMovingDown = false;
	public static double currentDrawn = 0;

	@Override
	public void robotInit() {
		main = Shuffleboard.getTab("main");
		avocadoState = main.add("Avocado", "Up").getEntry();
		hippieState = main.add("Hippie", "Down").getEntry();
		avoSlideState = main.add("Avo Slide", "In").getEntry();
		LIDARDist = main.add("LIDAR", 0).getEntry();
		pdp = new PowerDistributionPanel();
		try {
			Motors.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Sensors.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}

		controllers[DRIVER] = driverController;
		controllers[OPERATOR] = operatorController;
		if (singleControllerMode) {
			Master = controllers[DRIVER];
			System.out.println("controller 0");
		}
		Motors.compressor.setClosedLoopControl(true);

		state = RoboState.RobotInit;
		t = new MovementController(this);
		t.start();

		beltController = new BeltController(controllers[OPERATOR], Motors.beltMotor, ControllerMap.X, ControllerMap.B);

		try {
			avocadoRotationSwitch = new toggleSwitch(controllers[OPERATOR], ControllerMap.Y);
			landingGearSwitchExtend = new toggleSwitch(controllers[OPERATOR], ControllerMap.back,
					Motors.landingGearExtend, Solenoid.class.getMethod("set", boolean.class), Settings.TIMER_LANDING_GEAR_DELAY);
			landingGearSwitchRetract = new toggleSwitch(controllers[OPERATOR], ControllerMap.start,
					Motors.landingGearRetract, Solenoid.class.getMethod("set", boolean.class),Settings.TIMER_LANDING_GEAR_DELAY);
			landingGearSwitchStop = new toggleSwitch(controllers[OPERATOR], ControllerMap.LB, Motors.landingGearStop,
					Solenoid.class.getMethod("set", boolean.class),Settings.TIMER_LANDING_GEAR_DELAY);
			landingGearControl = new landingGearController(controllers[OPERATOR], ControllerMap.back,
					ControllerMap.start, landingGearSwitchExtend, landingGearSwitchRetract, landingGearSwitchStop);
			hingeSwitch = new toggleSwitch(controllers[OPERATOR], ControllerMap.RB, Motors.hinge,
					Solenoid.class.getMethod("set", boolean.class));
			avocadoSlideSwitch = new toggleSwitch(controllers[OPERATOR], ControllerMap.A, Motors.avocadoSlide,
					Solenoid.class.getMethod("set", boolean.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		landingGearSwitchExtend.setB(false);
		landingGearSwitchRetract.setB((true));
		hingeSwitch.setB(false);
		avocadoSlideSwitch.setB(false);
		state = RoboState.TeleopInit;
	}

	@Override
	public void disabledInit() {
		try {
			MovementController.clearActions();
		} catch(Exception e){
			e.printStackTrace();
		}
		state = RoboState.Disabled;
		Motors.compressor.setClosedLoopControl(false);
	
	}

	@Override
	public void disabledPeriodic() {
		state = RoboState.Disabled;
		if (colorState)
			Motors.blinkin2019.set(Settings.CONFETTI);
		else
			Motors.blinkin2019.set(Settings.PARTY);
		if (System.currentTimeMillis() - lastLightSwitch > 300) {
			lastLightSwitch = System.currentTimeMillis();
			colorState = !colorState;
		}
		// if(!avocadoUp){
		// isAvocadoTurning = (true);
		// startTurn = (true);

		// }
	}

	@Override
	public void autonomousInit() {
		teleopInit();
		state = RoboState.AutonomousInit;

		// PowerMonitor.init();

		gameData = DriverStation.getInstance().getGameSpecificMessage();
	}

	@Override
	public void autonomousPeriodic() {
		teleopPeriodic();
		//Motors.compressor.setClosedLoopControl(false);
		landingGearControl();
		state = RoboState.Autonomous;
		//moveToHatch();

	}

	@Override
	public void teleopInit() {
		
	}

	@Override
	public void teleopPeriodic() {
		Motors.compressor.setClosedLoopControl(true);
		try {
			if(controllers[DRIVER].getRawButton(ControllerMap.start)) pdp.clearStickyFaults(); 
		} catch(Exception e){
			e.printStackTrace();
		}
		// try {
		// 	LIDARDist.setDouble(Math.round(Sensors.lidar.getDistanceIn() - 6));
		// } catch(Exception e) {
		// 	e.printStackTrace();
		// }
		// PowerMonitor.evaluate();
		// degreeSync();
		// -------------------------- CONTROLLER GARBO --------------------------
		// if (singleControllerMode && Master.getRawButton(ControllerMap.leftClick)) {
		// 	controllerSelector++;
		// 	GenericHID c;
		// 	if (controllerSelector > controllers.length - 1) {

		// 		controllerSelector = DRIVER;
		// 		c = controllers[DRIVER];
		// 		controllers[DRIVER] = controllers[OPERATOR];
		// 		controllers[OPERATOR] = c;
		// 	}
		// 	if (controllerSelector == OPERATOR) {
		// 		c = controllers[OPERATOR];
		// 		controllers[OPERATOR] = controllers[DRIVER];
		// 		controllers[DRIVER] = c;
		// 	}
		// 	System.out.println(controllerSelector + " controllerSelector");
		// 	Master = controllers[controllerSelector];
		// }

		state = RoboState.Teleop;

		try {
			driveControl();
			climbingArm(); // operator RIGHT STICK
			landingGearControl();
			avocadoControl();
			hingeSwitch.buttonUpdate();// opperator Y button
			beltController.update(); // operator X - UP AND B - DOWN Button

			rightBumper(); // driver
			leftBumper(); // driver
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Motors.blinkin2019.set(setColors());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (controllers[DRIVER].getRawButton(ControllerMap.LB)) {
		}
	}

	public void landingGearControl() {

		//landingGearControl.update();
		if (landingGearSwitchRetract.buttonPressed()) {
			landingGearSwitchStop.setB(false);
			landingGearSwitchExtend.setB(false);
		}
		if (landingGearSwitchExtend.buttonPressed()) {
			landingGearSwitchRetract.setB(false);
		}
		landingGearSwitchExtend.buttonUpdate();
		landingGearSwitchRetract.buttonUpdate();
		landingGearSwitchStop.buttonUpdate();
	}

	public void leftBumper() {
		if (controllers[DRIVER].getRawButton(ControllerMap.LB) && !leftBumperEngaged) {
			leftBumperEngaged = (true);
			MovementController.addAction((new Turn((float)JeVois.getTargetDeg(), .8f)));
		}

		if (!controllers[DRIVER].getRawButton(ControllerMap.LB) && leftBumperEngaged) {
			leftBumperEngaged = false;
			MovementController.clearActions();
		}
	}

	void rightBumper(){
		if (controllers[DRIVER].getRawButton(ControllerMap.RB) && !rightBumperEngaged) {
			rightBumperEngaged = (true);
			MovementController.addAction((new Turn(180, 1f)));
		}

		if (!controllers[DRIVER].getRawButton(ControllerMap.RB) && rightBumperEngaged) {
			rightBumperEngaged = false;
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
		avocadoRotationSwitch.button();
		int set;
		if(avocadoRotationSwitch.getB()){
			set = 1;
		} else {
			set = -1;
		}
		//System.out.println(Motors.avocadoMotor.get());
		
		Motors.avocadoMotor.set(0);
		// if (invincibilityTimer > 0)
		// 	invincibilityTimer--;
		// if (Sensors.avocadoLimit.get() && controllers[OPERATOR].getRawButton(ControllerMap.Y)) {
		// 	isAvocadoTurning = (true);
		// 	startTurn = (true);
		// }
		// if (controllers[OPERATOR].getRawButton(ControllerMap.Y))
		// 	invincibilityTimer = 2;
		// if (startTurn && !Sensors.avocadoLimit.get())
		// 	startTurn = false;
		// if (Sensors.avocadoLimit.get() && !startTurn && isAvocadoTurning) {
		// 	avocadoUp = !avocadoUp;
		// 	isAvocadoTurning = false;
		// }
		// if (isAvocadoTurning || invincibilityTimer != 0)
		// 	Motors.avocadoMotor.set(-1);
		// else
		// 	Motors.avocadoMotor.set(0);

		
	}

	private void avocadoControl() {
		avocadoSlideSwitch.buttonUpdate(); // Operator A
		avocadoTurningControl(); // operator Y
	}

	private void avocadoSlideControl() {
		try {
			if (Sensors.lidar.getDistanceIn() < Settings.LIDAR_AVOCADO_DISTANCE
					&& Math.abs(controllers[OPERATOR].getRawAxis(ControllerMap.rightTrigger)) > .1) {
				// avocadoSlideSwitch.b = (true);
			} else {
				// avocadoSlideSwitch.b = false;
			}
			avocadoSlideSwitch.buttonUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void climbingArm() {
		double armPivot = controllers[OPERATOR].getRawAxis(ControllerMap.leftY);
		double armSpin = controllers[OPERATOR].getRawAxis(ControllerMap.rightY);
		//double currCurr = pdp.getCurrent(1);

		//if (currCurr / lastAmp < 0.3 || currCurr < 5)
			//armColl = false;
		if (Math.abs(armPivot) > .1) {
			//if (currCurr / lastAmp > 1.5)
				//armColl = (true);
			Motors.armMotor.set(armPivot * Settings.PIVOT_SPEED );
		} else {
			Motors.armMotor.set(0);
		}
		if (Math.abs(armSpin) > .15) {
			Motors.armEndMotor.set(armSpin);
		} else {
			Motors.armEndMotor.set(0);
		}
		//lastAmp = currCurr;
	}

	private void driveControl() {
		double dif;
		double leftY = controllers[DRIVER].getRawAxis(ControllerMap.leftTrigger)
				- controllers[DRIVER].getRawAxis(ControllerMap.rightTrigger);
		
		if (Math.abs(leftY) < Settings.BUMPER_DEADZONE)
			dif = 0.0;
		else {
			dif = (leftY/ Math.abs(leftY))*(.4 + (Math.abs(leftY) * .6));
		}
		double lx = controllers[DRIVER].getRawAxis(ControllerMap.leftX);
		double lNum;
		if (Math.abs(lx) > Settings.LSTICK_DEADZONE)
			lNum = controllers[DRIVER].getRawAxis(ControllerMap.leftX);
		else
			lNum = 0;
		if (lNum == 0 && dif == 0)
			Motors.drive.arcadeDrive(0, 0);
		else {
			if (controllers[DRIVER].getRawButton(ControllerMap.A))
				Motors.drive.arcadeDrive(-dif * Settings.SPEED_MULT, lNum);
			else if (controllers[DRIVER].getRawButton(ControllerMap.B)) { // Sicko mode button
				Motors.talonLeft.enableCurrentLimit(false);
				Motors.talonRight.enableCurrentLimit(false);
				Motors.drive.arcadeDrive(-dif * Settings.SPEED_MULT, lNum * Settings.TURN_MULT);
			} else {
				Motors.talonLeft.enableCurrentLimit((true));
				Motors.talonRight.enableCurrentLimit((true));
				Motors.drive.arcadeDrive(-dif * Settings.SPEED_MULT, lNum * Settings.TURN_MULT);
			}
		}
	}

	public void moveToHatch() {
		if (!hasDone) {
			hasDone = (true);
			MovementController.addAction((new Turn((float)JeVois.getTargetDeg(), .7f)));
		}
		// double distance = 2;// (double) Sensors.backDist.getRangeInches() *
		// INCHES_TO_METERS;
		// if (angle > 0) {
		// MovementController.addAction((new Turn(90 - angle, .8f)));
		// MovementController.addAction(new Straight((float) (Math.cos(Math.toRadians(90
		// - angle)) * distance), .8f));
		// MovementController.addAction((new Turn(-90, .8f)));
		// MovementController.addAction(new Straight((float) (Math.sin(Math.toRadians(90
		// - angle)) * distance), .8f));
		// } else {
		// MovementController.addAction((new Turn(-90 - angle, .8f)));
		// MovementController.addAction(new Straight((float)
		// (Math.cos(Math.toRadians(-90 - angle)) * distance), .8f));
		// MovementController.addAction((new Turn(90, .8f)));
		// MovementController.addAction(new Straight((float)
		// (Math.sin(Math.toRadians(-90 - angle)) * distance), .8f)); }
		// }
	}

	public void grabHatch() {
		MovementController.addAction(new SolenoidAction(Motors.avocadoSlide));
		MovementController.addAction(new AvocadoTurn(0, 0, this));
		MovementController.addAction(new SolenoidAction(Motors.avocadoSlide));
	}

	public float setColors() {
		float color = Settings.PARTY;

		try {
			if (avocadoSlideSwitch.getB())
				avoSlideState.setString("Up");
			else
				avoSlideState.setString("Down");

			if (hingeSwitch.getB())
				hippieState.setString("Up");
			else
				hippieState.setString("Down");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// if (avocadoUp && !isAvocadoTurning) {
		// 	avocadoState.setString("Up");
		// 	color = Settings.GREEN;
		// } else if (!avocadoUp && !isAvocadoTurning) {
		// 	avocadoState.setString("Down");
		// 	color = Settings.YELLOW;
		// }

		try{
			if(!avocadoRotationSwitch.getB()){
				avocadoState.setString("Up");
				color = Settings.GREEN;
			}else if(avocadoRotationSwitch.getB()){
				avocadoState.setString("Down");
				color = Settings.YELLOW;
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		if (beltMovingUp()) {
			color = Settings.BLUE;
		} else if (beltMovingDown()) {
			color = Settings.RED;
		}
		if (isAvocadoTurning) {
			avocadoState.setString("Turning");
			color = Settings.HOT_PINK;
		}
		if (controllers[DRIVER].getRawButton(ControllerMap.back)) {
			if (colorState)
				color = Settings.CONFETTI;
			else
				color = Settings.PARTY;
		}
		if (System.currentTimeMillis() - lastLightSwitch > 300) {
			lastLightSwitch = System.currentTimeMillis();
			colorState = !colorState;
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