package movement;

import org.usfirst.frc.team3793.robot.Settings;
import org.usfirst.frc.team3793.robot.Robot;
import org.usfirst.frc.team3793.robot.Motors;
import org.usfirst.frc.team3793.robot.Sensors;

public class AvocadoTurn extends MovementAction {
    Robot robot;
    boolean isAvocadoTurning = true;
    int delayTimer = 0;

    public AvocadoTurn(int direction, float maxSpeed, Robot robot) {
        super();
        this.robot = robot;
        isAvocadoTurning = true;
    }

    public boolean isComplete() {
        return !isAvocadoTurning;
    }

    public void spin() {
        // 1330 miliseconds, doesn't work
        delayTimer++;
        if (Sensors.avocadoLimit.get() && delayTimer > Settings.TIMER_DELAY)
            isAvocadoTurning = false;

        if (isAvocadoTurning) Motors.avocadoMotor.set(-1);
        else Motors.avocadoMotor.set(0);
    }

    public Speed getSpeed() {
        spin();
        return new Speed(0, 0);
    }
}