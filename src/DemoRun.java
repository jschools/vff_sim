import java.util.LinkedList;
import java.util.List;


public class DemoRun {
	
	// start conditions
	final static double START_X = 100;
	final static double START_Y = 100;
	final static double START_HEADING = 90;
	
	public static void main(String[] args) {
		
		// create environment
		Environment env = new Environment();

		// create robot
		//Robot bot = new Robot(env, new Point(START_X, START_Y), START_HEADING, new double[]{});
		Robot bot = new Robot(new double[] {-55, -20, 20, 55});
		RobotController botController = bot.controller;
		SensorManager sensorManager = bot.sensorManager;


		List<Point> history = new LinkedList<Point>();

		// boot up the robot
		bot.startServices();		
		
	}

}
