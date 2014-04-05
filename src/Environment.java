import java.util.ArrayList;
import java.util.List;


public class Environment {

	public Robot robot;
	public PhysicalRobot physicalRobot;

	public List<Obstacle> obstacles;

	public List<Vector> forces;

	public Environment() {
		this.obstacles = new ArrayList<Obstacle>();    
	}

	public void setRobot(Robot robot, PhysicalRobot physicalBot) {
		this.robot = robot;
		this.physicalRobot = physicalBot;
	}

	public void addObstacle(Point a, Point b) {
		obstacles.add(new Obstacle(a, b));
	}

	public List<Obstacle> getObstacles() {
		return obstacles;
	}


}