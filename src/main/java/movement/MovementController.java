package movement;

import java.util.ArrayDeque;

import org.usfirst.frc.team3793.robot.ControllerMap;
import org.usfirst.frc.team3793.robot.Motors;
import org.usfirst.frc.team3793.robot.Robot;
import org.usfirst.frc.team3793.robot.Sensors;

import edu.wpi.first.wpilibj.RobotState;

import org.usfirst.frc.team3793.robot.RoboState;

/**
 * This class is a {@link Runnable} that controls movement during automated
 * portions of the match. This class is active during {@code State.Autonomous}
 * and when triggered during {@code State.Teleop}. An ArrayDeque of
 * {@link MovementActions} is used along with {@code ArrayDeque.removeFirst();}
 * to act as a FIFO list
 * 
 * @author Warren Funk and Ethan Durham
 */

public class MovementController extends Thread {
	public static ArrayDeque<Action> actions;
	Action action;
	boolean teleopEnabled = false;
	Robot r;
	int timer = 0;

	public MovementController(Robot r) {
		this.r = r;
	}

	public void run() {
		while (Sensors.navX.isCalibrating());
		action = null;
		actions = new ArrayDeque<Action>();
		// Make speed for everything 0.8f(reccomended)
		//actions.add(new Straight(3, 0.8f));
		//actions.add(new Turn(90,.8f));
		// Put actions here for autonomous like so: actions.add(new Turn(1, 90, 0.7));

		while (!Thread.interrupted()) {
			timer--;
			if (action != null && action.isComplete()) {
				action = null;
				Motors.drive.tankDrive(0, 0);
				timer = 30;
			}
			if (Robot.getState() == RoboState.Autonomous || Robot.getState() == RoboState.Teleop) {
				if (!actions.isEmpty()) {
					if (action == null && timer <= 0) {
						action = actions.removeFirst();
						action.resetStartPos();
					}
				}
				if (action != null && (Robot.controllers[Robot.DRIVER].getRawButton(ControllerMap.LB) || Robot.getState().equals(RoboState.Autonomous))) {
					action.set();
				}
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
		this.interrupt();
	}

	public static synchronized void addAction(Action a) {
		actions.add(a); // nice
	}

	public static synchronized void clearActions(){
		actions.clear();
	}
}