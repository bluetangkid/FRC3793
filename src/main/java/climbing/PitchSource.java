package climbing;

import org.usfirst.frc.team3793.robot.Sensors;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class PitchSource implements PIDSource{
    PIDSourceType t = PIDSourceType.kDisplacement;
    public PIDSourceType getPIDSourceType(){
        return t;
    }
    public double pidGet(){
        if (t == PIDSourceType.kRate) {
    		return Sensors.navX.getRawGyroX();
    	}
    	return Sensors.navX.getPitch();
    }
    public void setPIDSourceType(PIDSourceType t){
        this.t = t;
    }
}