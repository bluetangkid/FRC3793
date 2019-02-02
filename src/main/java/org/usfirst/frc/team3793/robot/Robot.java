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

//Equation for Drift on tile where y is drift in clicks and x is velocity in clicks/100 ms
// Y=7.029608995X - 592.3469424, where domain is defined on (90,1700)
/**
 * Main Robot class. Does networking and Teleop control by thinking very hard and very carefully.
 * @author Alex for teleop control, Warren for networking, FIRST provided an empty class template
 */
public class Robot extends TimedRobot {
	
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

	static int vaccumTimer;
	static int cubeIntakeTimer;
	static int cubeEjectTimer;

	static int switchNum = 0;

	static double scissorSpeed;
	static double speedOfVacuumPivot;

	static boolean vacuumOn;

	static boolean timerStarted;

	static boolean scissorTimerStarted;

	static String gameData;
	
	static DatagramSocket socket;
	static InetAddress pi;
	
	static SmartDashboard dashboard;
	//static PowerDistributionPanel pdp;
	static double minVoltage = 30;

	@Override
	public void robotInit() {
		Motors.initialize();
		Sensors.initialize();

		controllers[0] = driverController;
		controllers[1] = operatorController;
		if(singleControllerMode){
			Master = controllers[0];
			System.out.println("controller 0");
		}
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
		Motors.blinkin.set(-0.59);
		try{
		t.interrupt();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void disabledPeriodic() {
		state = RoboState.Disabled;
		Scheduler.getInstance().run();
	}

	@Override
	public void autonomousInit() {
		Sensors.navX.reset();
		state = RoboState.AutonomousInit;
		Motors.blinkin.set(-0.43);
		timerStarted = false;
		scissorTimerStarted = false;
		//Motors.vacuumMotor.set(1);
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		//System.out.println("gameData is " + gameData);
		
		//Sensors.navX.reset();
		System.out.println(Sensors.navX.getYaw()+" AutoInit");
		t = new MovementController();
		t.start();
		//System.out.println(switchNum);
	}
	
	@Override
	public void autonomousPeriodic() {
		
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
		

		if(singleControllerMode && Master.getRawButton(8)){
			controllerSelector++;
			GenericHID c;
			if(controllerSelector > controllers.length -1){
				
				controllerSelector = 0;
				c = controllers[0]; 
				controllers[0] = controllers[1];
				controllers[1] = c;
			}
			if(controllerSelector == 1){
				c = controllers[1];
				controllers[1] = controllers[0];
				controllers[0] = c; 
			}
			System.out.println(controllerSelector + " controllerSelector");
			Master = controllers[controllerSelector];
		}
		
		state = RoboState.Teleop;

		vaccumTimer--;
		cubeIntakeTimer--;
		cubeEjectTimer--;

		scissorSpeed = controllers[1].getRawAxis(1);
		speedOfVacuumPivot = controllers[1].getRawAxis(5);

		Scheduler.getInstance().run();
		
		//if(pdp.getVoltage() < minVoltage) minVoltage = pdp.getVoltage();
		//SmartDashboard.putNumber("Min Voltage", minVoltage);
		
		/*
		if(controllers[0].getBumper(Hand.kRight)) {
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

		drive();

		// ----------------------------------------------------------------------

		// --------------------------- VACUUM CONTROL ---------------------------

		turnVacuumOn();

		// ----------------------------------------------------------------------

		// ----------------------- CUBE INTAKE AND EJECT ------------------------

		cubeDispenser();

		// ----------------------------------------------------------------------

		// -------------------------- SCISSOR CONTROL ---------------------------

		scissorSpeed(scissorSpeed);

		// ----------------------------------------------------------------------

		// ----------------------- VACUUM PIVOT CONTROL -------------------------

		vacuumPivot(speedOfVacuumPivot);

		// ----------------------------------------------------------------------
		Motors.blinkin.set(-0.01);

	}
	public boolean masterIsDriver(){
		return Master == controllers[0];
			} 
	public boolean masterIsOperator(){
		return Master == controllers[1];
			} 

	private void drive() {
		driveControl();
	}

	private void turnVacuumOn() {
		if (controllers[1].getRawButton(1) && vaccumTimer < 0) { //A button
			vaccumTimer = 50;

			if (!vacuumOn) {
				vacuumOn = true;
			} else {
				vacuumOn = false;
			}
		}

		if (vacuumOn) {
			Motors.vacuumMotor.set(1);
		} else {
			Motors.vacuumMotor.set(0);
		}
	}

	private void cubeDispenser() {
		if (controllers[1].getRawButton(3)) { // X Button
			// Intake cube
			Motors.cubeMotorLeft.set(-0.7); // -0.7
			Motors.cubeMotorRight.set(0.7); // 0.7
			// t = new MovementController();
		    // t.start();
		} else if (controllers[1].getRawButton(4)) {// Eject cube - Y Button
			Motors.cubeMotorLeft.set(0.6); // 0.5
			Motors.cubeMotorRight.set(-0.6); // -0.5
		} else {
			Motors.cubeMotorLeft.set(0);
			Motors.cubeMotorRight.set(0);
		}
	}

	private void scissorSpeed(double scissorSpeed) {
		if ((Math.abs(scissorSpeed) > 0.2)) {
			Motors.scissorMotorOne.set(scissorSpeed);
			Motors.scissorMotorTwo.set(-scissorSpeed);
		} else {
			Motors.scissorMotorOne.set(0.0);
			Motors.scissorMotorTwo.set(0.0);
		}
	}

	private boolean vacuumPivot(double speedOfVacuumPivot) {
		//System.out.println(Sensors.vacuumPivotSwitch.get());
		// FALSE means the switch is being PRESSED
		// TRUE means the switch is NOT being PRESSED
		if (Math.abs(speedOfVacuumPivot) >= 0.2) { // Checking if speed of vacuum is above the dead zone
			if (!Sensors.vacuumPivotSwitch.get() && speedOfVacuumPivot > 0) { // Checking if switch is pressed AND speed is negative
				Motors.vacuumPivotMotor.set(0); // Setting speed to 0 so the arm won't move down anymore
			} else Motors.vacuumPivotMotor.set(speedOfVacuumPivot); // If we are going the other way or switch isnt pressed, run as usual
			return Sensors.vacuumPivotSwitch.get();
		}
		Motors.vacuumPivotMotor.set(0); // If speed is not above the dead zone, set the speed to 0
		return true;
	}

	private void driveControl() {
		double dif = Math.signum(controllers[0].getRawAxis(2) - controllers[0].getRawAxis(3))*((controllers[0].getRawAxis(2) - controllers[0].getRawAxis(3)) * (controllers[0].getRawAxis(2) - controllers[0].getRawAxis(3)));
		if (Math.abs(dif) < 0.1) dif = 0.0;

		double turn = Math.signum(controllers[0].getRawAxis(0))*(controllers[0].getRawAxis(0)*controllers[0].getRawAxis(0));
		if (Math.abs(turn) < 0.1) turn = 0.0;
		
		Motors.drive.arcadeDrive(dif, turn);
	}

	@Override
	public void testPeriodic() {}
	
	public static synchronized RoboState getState() {
		return state;
	}
}
