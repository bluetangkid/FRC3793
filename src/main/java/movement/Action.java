package movement;

public abstract class Action {
    protected long beginTime;

	public Action() {
		beginTime = System.currentTimeMillis();
	}
	
	public abstract void set();
	public abstract boolean isComplete();
	public void resetStartPos(){}
}
