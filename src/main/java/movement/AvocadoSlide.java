package movement;

import org.usfirst.frc.team3793.robot.Robot;

public class AvocadoSlide extends MovementAction {
    Robot robot;

    public AvocadoSlide(int direction, float maxSpeed, Robot robot) {
        super();
        this.robot = robot;
    }

    public Speed getSpeed() {
        return new Speed(0, 0);
    }

    public boolean isComplete(){
        robot.avocadoSlideSwitch.reflect(); // This is fine, checked by Warren
        return true;
    }
}