package climbing;

import org.usfirst.frc.team3793.robot.Settings;
import org.usfirst.frc.team3793.robot.Robot;
import org.usfirst.frc.team3793.robot.Motors;
import org.usfirst.frc.team3793.robot.Sensors;
import movement.*;

public class L3Climb extends Action {
    int phase;
    Straight straight;
    ArmPivot armP;
    boolean s;
    long time;

    public L3Climb() {
        super();
        phase = 0;
    }

    public boolean isComplete() {
        return phase >= 5;
    }

    public void set() {
        if(phase == 0) {
            if(s) straight = new Straight((float)((Sensors.lidar.getDistanceIn() - 8) * 0.0254), 0.6f);
            if(straight.isComplete()) {
                phase = 1;
                s = false;
            }
        } else if(phase == 1){
            if(s) {
                Robot.landingGearSwitch2.b = true;
                Robot.landingGearSwitch3.b = false;
                armP = new ArmPivot();
                time = System.currentTimeMillis();
            }
            if() phase = 2;
        }
    }
}