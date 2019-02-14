package org.usfirst.frc.team3793.robot;

import java.net.DatagramSocket;
import java.net.InetAddress;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import movement.MovementController;
import edu.wpi.first.wpilibj.Timer;

//Equation for Drift on tile where y is drift in clicks and x is velocity in clicks/100 ms
// Y=7.029608995X - 592.3469424, where domain is defined on (90,1700)
/**
 * Main Robot class. Does networking and Teleop control by thinking very hard and very carefully.
 * @author Faris for teleop control, Warren for networking, FIRST provided an empty class template
 */
public class Robot extends TimedRobot {
	
	//Controller initialization
	static GenericHID driverController = new XboxController(0);
	static GenericHID operatorController = new XboxController(1);
	public GenericHID[] controllers = new GenericHID[2];
	private boolean singleControllerMode = true;
	public int controllerSelector = 0;
	public GenericHID Master = null;
	
	
	static RoboState state = RoboState.RobotInit;
	Thread t;
	
	public static float targetDegrees;
	public static float targetDistance;

	static int switchNum = 0;

	static String gameData;
	
	static DatagramSocket socket;
	static InetAddress pi;

	static final int DRIVER = 0;
	static final int OPERATOR = 1;

	//avocado initialization
	static int avocadoPos = 180;
	static boolean isAvocadoTurning = false;
	static boolean avocadoLimitReleased = false;
	static Timer avocadoTimer = new Timer();

	static final int TIMER_DELAY = 5;

	static int avocadoSlideTimer = 0;
	static boolean isAvocadoOut = false;

	static int hingeTimer = 0;
	static boolean isHingeUp = false;

	//beltstates
	public enum beltStates {STOPPED, MOVING_UP, LIMIT_HIT, EJECTING, MOVING_DOWN}
	beltStates beltState = beltStates.STOPPED;
	boolean xButtonEnabled = false;
	boolean bButtonEnabled = false;


	static SmartDashboard dashboard;
	//static PowerDistributionPanel pdp;
	static double minVoltage = 30;

	static int compressorTimer = 0;
	static boolean compressorOn = true;

	@Override
	public void robotInit() {
		Motors.initialize();
		Sensors.initialize();

		controllers[DRIVER] = driverController;
		controllers[OPERATOR] = operatorController;
		if(singleControllerMode){
			Master = controllers[DRIVER];
			System.out.println("controller 0");
		}
		// Motors.compressor.setClosedLoopControl(false);
		// Motors.landingGear.set(false);
		// Motors.avocadoSlide.set(false);

		state = RoboState.RobotInit;
		
//		try {
//			socket = new DatagramSocket(5808);
//			socket.setSoTimeout(3);
//		} catch (SocketException e) {
//			e.printStackTrace();
//		}
		
//		try {
//			pi = InetAddress.getByName("10.37.93.50");
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
		//pdp = new PowerDistributionPanel();
	}

	@Override
	public void disabledInit() {
		state = RoboState.Disabled;
		//Motors.blinkin.set(-0.59);
		try{
		t.interrupt();
		}catch(Exception e){
			e.printStackTrace();
		}
		// Motors.compressor.setClosedLoopControl(false);
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
		//Motors.blinkin.set(-0.43);
		
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		t = new MovementController();
		t.start();

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
		state = RoboState.TeleopInit;
		
	}

	@Override																																																																					
	public void teleopPeriodic() {
		if(singleControllerMode && Master.getRawButton(ControllerMap.leftClick)){
			controllerSelector++;
			GenericHID c;
			if(controllerSelector > controllers.length -1){
				
				controllerSelector = DRIVER;
				c = controllers[DRIVER]; 
				controllers[DRIVER] = controllers[OPERATOR];
				controllers[OPERATOR] = c;
			}
			if(controllerSelector == OPERATOR){
				c = controllers[OPERATOR];
				controllers[OPERATOR] = controllers[DRIVER];
				controllers[DRIVER] = c; 
			}
			System.out.println(controllerSelector + " controllerSelector");
			Master = controllers[controllerSelector];
		}
		
		state = RoboState.Teleop;


		Scheduler.getInstance().run();
		
		//if(pdp.getVoltage() < minVoltage) minVoltage = pdp.getVoltage();
		//SmartDashboard.putNumber("Min Voltage", minVoltage);
		
		/*
		if(controllers[DRIVER].getBumper(Hand.kRight)) {
			byte[] data = new byte[1];
			data[0] = 1;
			DatagramPacket p = new DatagramPacket(data, 1, pi, 5808);
			try {socket.send(p);} catch (IOException e) {e.printStackTrace();}
		}
		byte[] data = new byte[8];
		DatagramPacket p = new DatagramPacket(data, data.length, pi, 5808);
		try {
			socket.receive(p);
			
			if(data[0] != 0) {
				byte[] a = new byte[4];
				byte[] b = new byte[4];
				
				a[0] = data[0];
				a[1] = data[1];
				a[2] = data[2];
				a[3] = data[3];
				
				b[0] = data[4];
				b[1] = data[5];
				b[2] = data[6];
				b[3] = data[7];
				
				float floatA = ByteBuffer.wrap(a).order(ByteOrder.LITTLE_ENDIAN).getFloat();
				float floatB = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getFloat();
				if(floatA - Math.floor(floatA) == 0.151) {
					targetDegrees = (float)Math.floor(floatA);
					targetDistance = floatB;
				} else {
					targetDegrees = (float)Math.floor(floatB);
					targetDistance = floatA;
				}
				MovementController.addAction(new Turn(targetDegrees, 0.5f));
				MovementController.addAction(new Straight(targetDistance*0.8f, 0.5f));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/

		// ---------------------------- ARCADE DRIVE ----------------------------

		driveControl(); // work Driver
		avocadoControl();
		//landingGear();
		climbingArm(); // work Driver
		cargoIntake(); // work Driver
		hingeControl();// work driver
		compressorControl();

		// ----------------------------------------------------------------------



		// ----------------------------------------------------------------------
		//Motors.blinkin.set(-0.01);

	}
	public boolean masterIsDriver(){
		return Master == controllers[DRIVER];
			} 
	public boolean masterIsOperator(){
		return Master == controllers[OPERATOR];
			} 

	public void compressorControl(){
		compressorTimer++;
		if(compressorTimer >= TIMER_DELAY){
			compressorTimer = TIMER_DELAY;
		}

		if(compressorTimer >= TIMER_DELAY && controllers[DRIVER].getRawButton(ControllerMap.rightClick)){
			compressorTimer = 0;
			if(compressorOn){
				compressorOn = false;
			}else{
				compressorOn = true;
			}
		}
		
		Motors.compressor.setClosedLoopControl(compressorOn);
	}

	public void avocadoSlideControl(){
		avocadoSlideTimer++;
		if(avocadoSlideTimer >= TIMER_DELAY){
			avocadoSlideTimer = TIMER_DELAY;
		}

		if(avocadoSlideTimer == TIMER_DELAY && controllers[DRIVER].getRawButton(ControllerMap.A) ){
			avocadoSlideTimer = 0;
			if(isAvocadoOut){
				isAvocadoOut = false;
			}else{
				isAvocadoOut = true;
			}
		}

		Motors.avocadoSlide.set(isAvocadoOut);
	}

	public void avocadoTurningControl(){
		//1.33 seconds
		int povPos = controllers[OPERATOR].getPOV(0);

		double left = controllers[OPERATOR].getRawAxis(ControllerMap.rightX);

		if(left<-.1){
		Motors.avocadoMotor.set(left*.6);
		}else{
			Motors.avocadoMotor.set(0);
		}


		if(povPos >= 135 && povPos <225 && avocadoPos >= 180 && !isAvocadoTurning){ // Up D-pad
			Motors.avocadoMotor.set(-1.0); // at full speed
			avocadoTimer.reset();  // we won't test for ...
			avocadoTimer.start();  // ... stopping for 1 second 
			isAvocadoTurning = true;
			System.out.println("In avocado turning motor ON"); 
			avocadoLimitReleased = false;
		}

		if(povPos >= 315 && povPos < 45 && avocadoPos <= 0 && !isAvocadoTurning){ // Down D-pad
			Motors.avocadoMotor.set(-1.0); // at full speed
			avocadoTimer.reset();  // we won't test for ...
			avocadoTimer.start();  // ... stopping for 1 second
			isAvocadoTurning = true;
			System.out.println("In avocado turning motor ON");
			avocadoLimitReleased = false;
		}

		if (!Sensors.avocadoLimit.get() && !avocadoLimitReleased) {
			avocadoLimitReleased = true;
			System.out.println("In avocado -- avocadoLimitReleased");
		}

		if (isAvocadoTurning) {
			// is it time to stop yet? 
			// Wait a second before testing if the limit switch is closed
			// if (avocadoTimer.get() >= 1.0) {
				if (Sensors.avocadoLimit.get() && avocadoLimitReleased) {  //active when true
					Motors.avocadoMotor.set(0.0);
					System.out.println("In avocado turning motor OFF");
					// set new position
					avocadoPos = 180 - avocadoPos;
					avocadoTimer.reset();
					isAvocadoTurning = false;
				}
			}
	
	}

	private void avocadoControl(){
		avocadoSlideControl();
		avocadoTurningControl();
	}

	private void landingGear(){
		if(!Motors.landingGear.get() && controllers[OPERATOR].getRawButton(ControllerMap.start)){ // supposed to be start button
			Motors.landingGear.set(true);// extend
		}

		if(Motors.landingGear.get() && controllers[OPERATOR].getRawButton(ControllerMap.back)){ // supposed to be back button
			Motors.landingGear.set(false);// retract
		}
	}

	private void climbingArm(){
		double armMovement = controllers[DRIVER].getRawAxis(ControllerMap.rightY); // supposed to be right stick Y axis
		
		if(Math.abs(armMovement) > .1){
			Motors.armMotor.set(armMovement *.6);
		}else{
			Motors.armMotor.set(0);
		}
	}

	

	private void cargoIntake(){
		final double GOING_UP = -1.0;
		final double GOING_DOWN = 1.0;

		if (beltState == beltStates.MOVING_UP) {
			if (controllers[DRIVER].getRawButton(ControllerMap.X) && xButtonEnabled) {
				// X button hit, stop the motor and change state
				Motors.beltMotor.set(0.0);
				beltState = beltStates.STOPPED;
				System.out.println("beltState is " + beltState);
				// prevent from going immediately backwards
				xButtonEnabled = false; 
			}
			// change the line below when we hook up a real limit switch.
			// Right now we get a true on the get, because no switch is there.
			if (!Sensors.beltLimit.get()) {
				// cargo is at the limit switch, stop the motor and change state
				Motors.beltMotor.set(0.0);
				beltState = beltStates.LIMIT_HIT;
				System.out.println("beltState is " + beltState);
			}
		}
		
		if (beltState == beltStates.STOPPED) {	
			if (controllers[DRIVER].getRawButton(ControllerMap.B) && bButtonEnabled) { 
				// operator wants to run intake until X button or 
				// the limit switch is active
				Motors.beltMotor.set(GOING_UP);
				beltState = beltStates.MOVING_UP;
				System.out.println("beltState is " + beltState);
				// prevent from going immediately backwards
				bButtonEnabled = false;
			}
			else if (controllers[DRIVER].getRawButton(ControllerMap.X) && xButtonEnabled) {
				// operator wants to run intake in reverse until the B is hit
				Motors.beltMotor.set(GOING_DOWN);
				beltState = beltStates.MOVING_DOWN;
				System.out.println("beltState is " + beltState);
			}
		}
		
		if (beltState == beltStates.LIMIT_HIT) { 
			// only X button can override the limit switch
			if (controllers[DRIVER].getRawButton(ControllerMap.X)) {
				Motors.beltMotor.set(GOING_UP);
				beltState = beltStates.EJECTING;
				System.out.println("beltState is " + beltState);
			}
		}
		
		if (beltState == beltStates.EJECTING  || beltState == beltStates.MOVING_DOWN) {
			// only B button can stop the belt in these states
			if (controllers[OPERATOR].getRawButton(ControllerMap.B) && bButtonEnabled) {
				Motors.beltMotor.set(0.0);
				beltState = beltStates.STOPPED;
				System.out.println("beltState is " + beltState);
				// prevent from going immediately upwards
				bButtonEnabled = false;
			}
		}
		// re-enable button once it is released
		if (! controllers[OPERATOR].getRawButton(ControllerMap.X) ) xButtonEnabled = true;
		if (! controllers[OPERATOR].getRawButton(ControllerMap.B) ) bButtonEnabled = true;
	
	}

	private void hingeControl(){
		
		hingeTimer++;
		if(hingeTimer >= TIMER_DELAY){
			hingeTimer = TIMER_DELAY;
		}

		if(hingeTimer >= TIMER_DELAY && controllers[DRIVER].getRawButton(ControllerMap.Y)){
			hingeTimer = 0;
			if(isHingeUp){
				isHingeUp = false;
			}else{
				isHingeUp = true;
			}
		}

		Motors.hinge.set(isHingeUp);
	}

	private void driveControl() {
		double dif = Math.signum(Math.pow(controllers[DRIVER].getRawAxis(ControllerMap.leftY),3));
		if (Math.abs(dif) < 0.1) dif = 0.0;

		
		double turn =Math.signum(Math.pow(controllers[DRIVER].getRawAxis(ControllerMap.leftX),3));
		if (Math.abs(turn) < 0.1) turn = 0.0;
		
		
		Motors.drive.arcadeDrive(turn * .6, -dif * .6);
	}

	@Override
	public void testPeriodic() {}
	
	public static synchronized RoboState getState() {
		return state;
	}
}
