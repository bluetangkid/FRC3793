package movement;
public class Autonomi {
    public static MovementAction[][] autonomi;
    
    public static void init(){
        autonomi = new MovementAction[10][];
        autonomi[0] = new MovementAction[]{new Straight(1, 0.5f)};
    }
    public static void addActions(int i) {
        for(MovementAction m : autonomi[i]) {
            MovementController.addAction(m);
        }
    }
}