
public class DemoController extends RobotController {

	public static final long MS_TO_RUN = 2000;
	
	private Vector direction;
	private long startTime;
	
	public DemoController(Robot robot) {
		super(robot);
		
		direction = new Vector(1, 0);
	}
	
	@Override
	public void run() {
		startTime = System.currentTimeMillis();
		super.target = new Point(-500, -500);
		super.run();
	}
	
	public void move() {
		if (System.currentTimeMillis() - startTime > MS_TO_RUN) {
			this.direction.x = 0;
			this.direction.y = 0;
		}
		movementController.move(this.direction);
	}
	
	public void turnTowardsAvg() {
		
	}

}
