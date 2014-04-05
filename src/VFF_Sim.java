import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

import processing.core.PApplet;

/*

VFF Sim

Jonathan Schooler
4/17/2011

This simulator is based on the "Virtual Force Field" idea described by
Johann Borenstein and Yorem Koren in the paper below.

Borenstein, J.; Koren, Y.; "Real-time obstacle avoidance for fast mobile robots"
Systems, Man and Cybernetics, IEEE Transactions on , vol.19, no.5, pp.1179-1187,
Sep/Oct 1989 doi: 10.1109/21.44033

 */

/*
 * This Revision works. Don't mess it up.
 * 
 */

@SuppressWarnings("serial")
public class VFF_Sim extends PApplet {
	Environment env;
	Robot bot;
	RobotController botController;
	SensorManager sensorManager;

	Point startLine;
	Point tempTarget;
	List<Point> history;
	int moveCounter = 0;

	// window dimensions
	final int WIDTH = 1200;
	final int HEIGHT = 800;

	// start conditions
	final double START_X = 600;
	final double START_Y = 200;
	final double START_HEADING = 90;

	// visibility options
	boolean SHOW_TARGET_LINE = true;
	boolean SHOW_SENSOR_DIST = true;
	boolean SHOW_INDIV_FORCES = true;
	boolean SHOW_NET_FORCE = true;
	boolean SHOW_90_LINES = true;
	boolean SHOW_HISTORY = true;

	// relative directions of sonar sensors
	//double[] SENSOR_LOCATIONS = {-75, -20, 20, 75};	// good pattern so far
	double[] SENSOR_LOCATIONS = { -55, -20, 20, 55 }; // better

	/**
	 * Main init method
	 */
	@Override
	public void setup() {

		// create environment
		env = new Environment();
		addObstacles(env);

		// create robot
		bot = new Robot(env, new Point(START_X, START_Y), START_HEADING, SENSOR_LOCATIONS);
		botController = bot.controller;
		sensorManager = bot.sensorManager;

		// set up window and anti-alias everything
		size(WIDTH, HEIGHT);
		smooth();

		history = new LinkedList<Point>();

		// boot up the robot
		bot.startServices();
	}

	private void addObstacles(Environment env) {
		env.addObstacle(new Point(600, 575), new Point(300, 300));
		env.addObstacle(new Point(300, 300), new Point(225, 355));
		env.addObstacle(new Point(225, 355), new Point(600, 700));
		env.addObstacle(new Point(600, 700), new Point(975, 355));
		env.addObstacle(new Point(975, 355), new Point(900, 300));
		env.addObstacle(new Point(900, 300), new Point(600, 575));
	}

	// main draw loop
	@Override
	public void draw() {

		// draw background
		background(0xffffffff);

		// draw path history
		drawHistory();

		// draw temporary new obstacle
		drawNewLine();

		// draw sensor ranges and forces
		drawLines();

		// draw the robot
		drawRobot();

		// draw the obstacles
		drawObstacles();

		// draw the target
		drawTarget();

	}


	@Override
	public void mousePressed() {
		if (mouseButton == LEFT) {
			startLine = getScreenMousePoint();
		}
	}

	@Override
	public void mouseReleased() {
		if (mouseButton == LEFT) {
			env.addObstacle(getRealPoint(getScreenMousePoint()), getRealPoint(startLine));
			startLine = null;
		} else {
			botController.setTarget(tempTarget);
		}
	}

	// draws a gray line showing where the robot has been
	public void drawHistory() {
		noFill();
		stroke(0xffcccccc);
		strokeWeight(2);

		// draw history path
		beginShape();
		for (Point p : history) {
			vertex((float) getScreenX(p), (float) getScreenY(p));
		}
		endShape();

		// add new history point every 10 frames
		moveCounter++;
		if (moveCounter % 10 == 0) {
			history.add(new Point(bot.physicalRobot.position));
		}

	}


	public void drawNewLine() {
		if (startLine == null) {
			return;
		}

		noFill();
		stroke(0xff000000);
		strokeWeight(1);
		line((float) startLine.x, (float) startLine.y, mouseX, mouseY);
	}

	public void drawRobot() {

		double circleDiam = 60;
		double dotDiam = 10;
		double lineLength = 40;
		double sensorLength = 20;

		Point position = bot.physicalRobot.position;
		double heading = bot.physicalRobot.heading;

		pushMatrix();
		translate((float) getScreenX(position), (float) getScreenY(position));

		rotate((float) Math.toRadians(-heading));

		// draw line
		noFill();
		stroke(0xff0000cc);
		strokeWeight(3);
		line(0f, 0f, (float) lineLength, 0);

		// draw circle
		ellipseMode(CENTER);
		noFill();
		stroke(0xffff0000);
		strokeWeight(4);
		ellipse(0f, 0f, (float) circleDiam, (float) circleDiam);

		// draw dot
		ellipseMode(CENTER);
		fill(0xff000000);
		noStroke();
		ellipse(0f, 0f, (float) dotDiam, (float) dotDiam);

		// draw sensors
		noFill();
		stroke(0xff000000);
		strokeWeight(3);
		if (bot.sensorManager.getDistanceSensors() != null) {
			for (DistanceSensor sensor : bot.sensorManager.getDistanceSensors()) {
				pushMatrix();
				rotate((float) Math.toRadians(-sensor.getRelativeHeading()));
				line(0f, 0f, (float) sensorLength, 0);
				popMatrix();
			}
		}
		popMatrix();
	}


	public void drawObstacles() {
		List<Obstacle> obstacles = env.getObstacles();

		noFill();
		stroke(0xff4c4c4c);
		strokeWeight(2);

		for (Obstacle ob : obstacles) {
			line((float) getScreenX(ob.a), (float) getScreenY(ob.a), (float) getScreenX(ob.b), (float) getScreenY(ob.b));
		}

	}

	public void drawTarget() {
		if (mousePressed && mouseButton != LEFT) {
			tempTarget = getRealMousePoint();
			drawTarget(tempTarget);
		}

		if (botController.target != null) {
			drawTarget(botController.target);
		}
	}

	public void drawTarget(Point realTarget) {
		pushMatrix();
		translate((float) getScreenX(realTarget), (float) getScreenY(realTarget));

		// draw crosshairs
		noFill();
		stroke(0xff000000);
		strokeWeight(1);
		line(-10, 0, 10, 0);
		line(0, -10, 0, 10);

		// draw circle
		stroke(0xff0000ff);
		ellipseMode(CENTER);
		ellipse(0, 0, 15, 15);
		popMatrix();
	}

	public void drawLines() {

		Point position = bot.physicalRobot.position;
		double heading = bot.physicalRobot.heading;

		if (SHOW_TARGET_LINE && botController.target != null) {

			// draw target line
			noFill();
			stroke(0xff00ff00);
			strokeWeight(1);
			beginShape();
			pushMatrix();
			vertex((float) getScreenX(position), (float) getScreenY(position));
			vertex((float) getScreenX(botController.target), (float) getScreenY(botController.target));
			popMatrix();
			endShape();
		}

		if (SHOW_90_LINES) {
			// draw 90 degree lines from forward
			noFill();
			stroke(0xff666666);
			strokeWeight(1);
			pushMatrix();
			translate((float) getScreenX(position), (float) getScreenY(position));
			rotate((float) Math.toRadians(-heading));

			beginShape();
			vertex(0, -60);
			vertex(0, 60);
			endShape();
			popMatrix();
		}

		if (SHOW_SENSOR_DIST) {
			// draw a line from each sensor to its intersection point with each obstacle
			noFill();
			stroke(0xffff4444);
			strokeWeight(1);
			if (bot.sensorManager.getDistanceSensors() != null) {
				for (DistanceSensor sensor : bot.sensorManager.getDistanceSensors()) {
					Point lastIntersection = position.distancePoint(sensor.getLastReading(), sensor.getRelativeHeading() + bot.physicalRobot.heading);
					line((float) getScreenX(position), (float) getScreenY(position), (float) getScreenX(lastIntersection), (float) getScreenY(lastIntersection));
				}
			}
		}

		if (SHOW_INDIV_FORCES) {
			noFill();
			stroke(0xff0000ff);
			strokeWeight(1);
			try {
				for (Vector force : botController.forces) {

					pushMatrix();
					translate((float) getScreenX(position), (float) getScreenY(position));
					rotate((float) Math.toRadians(-bot.physicalRobot.heading));
					line(0f, 0f, (float) (100 * force.x), (float) (-100 * force.y));
					popMatrix();
				}
			} catch (ConcurrentModificationException cme) {
				// chill and retry
			}
		}

		if (SHOW_NET_FORCE && botController.netForce != null) {
			noFill();
			stroke(0xff00aa00);
			strokeWeight(2);
			Point net = new Point(botController.netForce.x, botController.netForce.y);

			pushMatrix();
			translate((float) getScreenX(position), (float) getScreenY(position));
			rotate((float) Math.toRadians(-bot.physicalRobot.heading));
			line(0, 0, (float) (100 * net.x), (float) (-100 * net.y));
			popMatrix();
		}
	}

	// convert real x coordinate to screen coordinate
	public double getScreenX(Point p) {
		return p.x;
	}

	// convert real y coordinate to screen coordinate
	public double getScreenY(Point p) {
		return height - p.y;
	}

	// convert screen point to real coordinate
	public Point getRealPoint(Point screenPoint) {
		return new Point(screenPoint.x, height - screenPoint.y);
	}

	public Point getScreenPoint(Point realPoint) {
		return new Point(getScreenX(realPoint), getScreenY(realPoint));
	}

	public Point getScreenMousePoint() {
		return new Point(mouseX, mouseY);
	}

	public Point getRealMousePoint() {
		return getRealPoint(getScreenMousePoint());
	}

	public static void main(String args[]) {
		PApplet.main(new String[] {"--bgcolor=#666666", "--stop-color=#cccccc", "VFF_Sim" });
	}
}
