package movement;

import org.usfirst.frc.team3793.robot.Settings;
import org.usfirst.frc.team3793.robot.Robot;
import org.usfirst.frc.team3793.robot.Motors;
import org.usfirst.frc.team3793.robot.Sensors;

public class AvocadoSlide extends MovementAction {
    Robot robot;

    public AvocadoSlide(int direction, float maxSpeed, Robot robot) {
        super(0, 0);
        this.robot = robot;
        System.out.println("I want to die");
    }

    public Speed getSpeed() {
        return new Speed(0, 0);
    }

    public boolean isComplete(){
        System.out.println("calling is complete");
        robot.avocadoSlideSwitch.reflect();
        return true;
    }
}