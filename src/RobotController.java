import java.util.ArrayList;
import java.util.List;

public class RobotController implements Runnable {
	
	final static double TARGET_FORCE = 2;
	final static double TARGET_DIAMETER = .0002; // 0.0002 for GPS, 25 for pixels
	final static double FORCE_CONSTANT = 110; // 110
	
	final static double FORCE_THRESHOLD = 0.2; // 0.2
	final static double TRAP_THRESH = 1.8;	// 2.0
	
	final static double VIRTUAL_ANGLE = 35f;

	Robot robot;
	SensorManager sensorManager;
	MovementController movementController;
	UdpFeedbackSender feedbackSender;
	Point target;
	Boolean avoidLeft;
	boolean running;
	
	List<Vector> sensorData;
	public List<Vector> forces;
	Vector netForce;
	

	/**
	 * Constructor
	 * @param robot
	 */
	public RobotController(Robot robot) {
		this.robot = robot;
		this.sensorManager = robot.sensorManager;
		this.movementController = robot.movementController;
		sensorData = new ArrayList<Vector>();
		forces = new ArrayList<Vector>();
		netForce = new Vector(0, 0);
		feedbackSender = new UdpFeedbackSender();
		avoidLeft = null;
		running = true;
	}  

	
	//***********************************************
	// Main controller loop
	//***********************************************
	public void run() {
		System.out.println("RobotController up");
		
		while (running) {
			if (target != null) {
				turnTowardsAvg();				
				updateSensors();
				calculateNetForce();
				move();
				
				checkTarget();
			}

			Constants.simDelay(100);
		}    
	}
	//***********************************************
	
	public void turnTowardsNetForce() {
		turnTowards(netForce.getAngle() + sensorManager.getLastHeading());
	}
	
	public void turnTowardsTarget() {
		double heading = getHeadingTo(target);
		turnTowards(heading);
	}
	
	public void turnTowardsAvg() {
		double headingToTarget = getHeadingTo(target);
		double headingToNetForce = netForce.getAngle() + sensorManager.getLastHeading();
		double avgHeading = Vector.averageAngle(headingToTarget, headingToNetForce);
		turnTowards(avgHeading);
	}

	public void setTarget(Point target) {
		this.target = target;
	}

	public void updateSensors() {
		sensorData.clear();
		
		if (sensorManager.getDistanceSensors() != null) {
			for (DistanceSensor sensor : sensorManager.getDistanceSensors()) {
				double distance = sensor.getLastReading();
				double heading = sensor.getRelativeHeading();
				
				feedbackSender.addRange((int) distance);
	
				if (distance > 0.00000001f) {
					sensorData.add(Vector.vectorFromPolar(distance, heading));
				}
			}
			
			feedbackSender.sendRanges();
		}
	}
	
	public void checkTarget() {
		Point position = sensorManager.getLastLocation();
		if (position != null && Point.distBetween(position, target) < TARGET_DIAMETER) {
			//movementController.stop();
			//target = null;
		}
	}

	public void turnTowards(double targetHeading) {
		
		double perceivedHeading = sensorManager.getLastHeading();
		
		double headingChange = targetHeading - perceivedHeading;

		if (headingChange > 180)
			headingChange = 360 - headingChange;
		else if (headingChange < -180)
			headingChange = 360 + headingChange;

		if (headingChange > 10)
			movementController.turnLeft();
		else if (headingChange < -10)
			movementController.turnRight();
		else
			movementController.noTurn();
	}

	
	public void calculateNetForce() {
		forces.clear();
		
		// get current location
		Point position = sensorManager.getLastLocation();
		double heading = sensorManager.getLastHeading();
	
		
		// calculate heading to target
		if (position != null && target != null) {
			System.out.println("position: " + position.toString());
			System.out.println("target: " + target.toString());
			
			double worldTargetHeading = position.getHeadingTo(target);
			double relativeTargetHeading = worldTargetHeading - heading;
			relativeTargetHeading = Vector.mod360(relativeTargetHeading);
			
			System.out.println("position: " + position.toString());
			System.out.println("target: " + target.toString());
			feedbackSender.sendHeading((float) relativeTargetHeading);
			
			// sum component forces
			netForce = new Vector(0, 0);
	
			for (Vector v : sensorData) {
				
				double distance = v.getLength();
				double angle = v.getAngle();
				
				double forceMultiplier = distToForce(distance);
				
				Vector force = Vector.vectorFromPolar(forceMultiplier, angle);
				
				if (force.getLength() > FORCE_THRESHOLD) {
					forces.add(force);				
					netForce.add(force);
				}
			}


			// if we are in a corner, add virtual force
			double netAngle = Vector.headingFromZero(netForce.getAngle());			
		
			double targetAngle = relativeTargetHeading;
			Vector netSum = Vector.add(netForce, Vector.vectorFromPolar(TARGET_FORCE, targetAngle));
			
//			System.out.println("netSum: " + netSum.getLength());
			
			if (netSum.getLength() < TRAP_THRESH) {
				if (netAngle >= 90 || (avoidLeft != null && avoidLeft)) {
//					System.out.println("trapped left");
					targetAngle = netAngle - 145;
					avoidLeft = true;
				} else if (netAngle <= -90) {
//					System.out.println("trapped right");
					targetAngle = netAngle + 145;
					avoidLeft = false;
				}
			} else {
				avoidLeft = null;
			}
			
			Vector targetForce = Vector.vectorFromPolar(TARGET_FORCE, targetAngle);
			
			// add forces together
			netForce.add(targetForce);
			
		} else {
			// if we don't have a lock, stop immediately
			movementController.stop();
		}
		
	}

	public void move() {
		movementController.move(netForce);
		feedbackSender.sendVelocity(netForce);
	}

	public double getHeadingTo(Point p) {
		Point lastLocation = sensorManager.getLastLocation();
		return lastLocation == null ? 0.0 : lastLocation.getHeadingTo(p);
	}

	public double distToForce(double distance) {
		//double force = -FORCE_CONSTANT / (distance * distance);
		
		double force = 0;
		
		if (distance < 50) {
			force = -0.0739 * distance + 4.443;
		} else if (distance < 85) {
			force = -0.0143 * distance + 1.464;
		} else {
			force = -0.00147 * distance + 0.375;
		}
		
		force *= -1.0;
		
		//System.out.println("dist: " + distance + " --> force: " + force);
		return force;
	}


}