package movement;

import edu.wpi.first.wpilibj.Solenoid;
/**
 * abstract class that {@link Straight} and {@link Turn} implement
 * @author Warren Funk
 */
public class SolenoidAction extends Action{
	private Solenoid s;
	private boolean b;
	
	public SolenoidAction(Solenoid s) {
		super();
		this.s = s;
		b = true;
	}
	
	/*
	 * returns a speed object containing the speeds from -1 to 1 for each motor
	 */
	public void set() {
		s.set(!s.get());
	}
	public boolean isComplete() {
		b = !b;
		return b;
	}
}
