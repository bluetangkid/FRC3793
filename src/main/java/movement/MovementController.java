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
 * @author Warren Funk and Ethan Durham
 */

public class MovementController extends Thread {
	public static ArrayDeque<MovementAction> actions;
	MovementAction action;
	boolean teleopEnabled = false;
	Robot r;
	int timer = 0;

	public MovementController(Robot r) {
		this.r = r;
	}

	public void run() {
		while (Sensors.navX.isCalibrating());
		action = null;
		actions = new ArrayDeque<MovementAction>();
		// actions.add(new Turn(45,.8f));
		// Make speed for everything 0.8f(reccomended)
		//actions.add(new Straight(3, 0.8f));
		// addAction(new AvocadoSlide(0,0,r));
		// addAction(new AvocadoTurn(0,0,r));
		// addAction(new AvocadoSlide(0,0,r));
		//addAction(new Turn(90,.8f));
		// Put actions here for autonomous like so: actions.add(new Turn(1, 90, 0.7));

		while (!Thread.interrupted()) {
			System.out.println("run is running");

			timer--;
			if (action != null && action.isComplete()) {
				System.out.println("OH NO GMAER");
				action = null;
				Motors.drive.tankDrive(0, 0);
				timer = 30;
			}
			if (Robot.getState() == RoboState.Autonomous || Robot.getState() == RoboState.Teleop) {
				if (!actions.isEmpty()) {
					System.out.println("Death is a blessing");
					if (action == null && timer <= 0) {
						action = actions.removeFirst();
						action.resetStartPos();
					}
				}
				if (action != null) {
					Speed speed = action.getSpeed();
					Motors.drive.tankDrive(speed.getL(), speed.getR());
				}
			} else if (Robot.getState() == RoboState.TeleopInit) {
				//action = null;
				//actions.clear();
			}
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
		this.interrupt(); // nice
	}

	public static synchronized void addAction(MovementAction a) {
		actions.add(a);
	}

	public static synchronized void clearActions(){
		actions.clear();
	}
}