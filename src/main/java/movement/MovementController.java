package movement;

import java.util.ArrayDeque;
import org.usfirst.frc.team3793.robot.Motors;
import org.usfirst.frc.team3793.robot.Robot;
import org.usfirst.frc.team3793.robot.Sensors;

import org.usfirst.frc.team3793.robot.RoboState;

/**
 * This class is a {@link Runnable} that controls movement during automated
 * portions of the match. This class is active during {@code State.Autonomous}
 * and when triggered during {@code State.Teleop}. An ArrayDeque of
 * {@link MovementActions} is used along with {@code ArrayDeque.removeFirst();}
 * to act as a FIFO list
 * 
 * @author Warren Funk
 */

public class MovementController extends Thread {
	ArrayDeque<MovementAction> actions;
	MovementAction action;
	boolean teleopEnabled = false;

	public MovementController() {
		action = null;
		actions = new ArrayDeque<MovementAction>();
		while (Sensors.navX.isCalibrating());
		// Make speed for turns 0.8f
		actions.add(new Turn(90, 0.7f));
		// Put actions here for autonomous like so: actions.add(new Turn(1, 90, 0.7));
		// actions.add(new Turn (45,.8f));
		action = actions.removeFirst();
	}

	public void run() {
		long loopStart;
		Sensors.navX.resetDisplacement();
		// System.out.println(Sensors.navX.getYaw());
		while (!Thread.interrupted()) {
			// System.out.println(System.currentTimeMillis());
			// SmartDashboard.putString("State", Robot.getState().name());
			// SmartDashboard.putNumber("Angle", Sensors.navX.getYaw());
			loopStart = System.currentTimeMillis();
			if (action != null && action.isComplete()) {
				action = null;
				Motors.drive.tankDrive(0, 0);
			}
			if (Robot.getState() == RoboState.Autonomous) {
				if (!actions.isEmpty()) {
					if (action == null)
						action = actions.removeFirst();
				}
				if (action != null) {
					Speed speed = action.getSpeed();
					Motors.drive.tankDrive(speed.getL(), speed.getR());
				}
			} else if (Robot.getState() == RoboState.Teleop) {
				if (teleopEnabled) {
					if (action == null)
						action = actions.removeFirst();
					Speed speed = action.getSpeed();
					Motors.drive.tankDrive(speed.getL(), speed.getR());
				}
			} else if (Robot.getState() == RoboState.TeleopInit) {
				action = null;
				actions.clear();
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
		this.interrupt();
	}

	public synchronized void addAction(MovementAction a) {
		actions.add(a);
	}

	public synchronized void setStatus(boolean status) {
		teleopEnabled = status;
	}
}