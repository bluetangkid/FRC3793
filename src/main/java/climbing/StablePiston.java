package climbing;

import edu.wpi.first.wpilibj.PIDOutput;
import movement.Action;

public class StablePiston extends Action implements PIDOutput{
    public boolean isComplete(){
        return false;
    }

    public void set(){

    }

    public void pidWrite(double d){
        
    }
}