



import java.util.ConcurrentModificationException;


public class SimDistanceSensor extends DistanceSensor {
	
	final static double SENSOR_LIMIT = 255;
	final static double ERROR_VALUE = 0;

	public Environment env;
	public Point lastIntersection;

	public SimDistanceSensor(Environment env, double relativeHeading) {
		super(relativeHeading);

		this.env = env;
		this.lastReading = SENSOR_LIMIT;
		this.lastIntersection = null;
	}

	/**
	 * Causes a distance measurement to take place. Distance vector is left in lastReading.
	 */
	public void takeReading() {
		
		Point minIntersection = env.physicalRobot.position.distancePoint(SENSOR_LIMIT, env.physicalRobot.heading + this.relativeHeading);
		double minDist = SENSOR_LIMIT;
				
		Point physicalLocation = env.physicalRobot.position;
		double physicalHeading = env.physicalRobot.heading;

		try {

			for (Obstacle ob : env.obstacles) {
				Point a = physicalLocation;
				Point b = ob.a;
				Point c = ob.b;
				Point d = a.distancePoint(1.0f, physicalHeading + this.relativeHeading);

				double uNum = ((c.x - b.x) * (a.y - b.y)) - ((c.y - b.y) * (a.x - b.x));
				double uDen = ((c.y - b.y) * (d.x - a.x)) - ((c.x - b.x) * (d.y - a.y));
				double u = uNum / uDen;

				Point intersection = new Point();
				intersection.x = a.x + u * (d.x - a.x);
				intersection.y = a.y + u * (d.y - a.y);

				Vector v = new Vector(d.x - a.x, d.y - a.y);
				Vector ad = new Vector(intersection.x - a.x, intersection.y - a.y);

				boolean isInFront = Math.abs(Math.atan2(v.y, v.x) - Math.atan2(ad.y, ad.x)) < 0.00001f;

				Vector ib = new Vector(b.x - intersection.x, b.y - intersection.y);
				Vector ic = new Vector(c.x - intersection.x, c.y - intersection.y);

				boolean isOnLine = Math.abs(Math.atan2(ib.y, ib.x) - Math.atan2(ic.y, ic.x)) > 0.0001f;

				double distance = Point.distBetween(a, intersection);

				if ((distance < minDist || minIntersection == null) && isInFront && isOnLine) {
					minDist = distance;
					minIntersection = intersection;
				}
			}

			this.lastIntersection = minIntersection;

		} catch (ConcurrentModificationException cme) {
			System.out.println("whoops I'm bad at multithreaded programming");
		}

		// set the distance
		if (lastIntersection != null) {
			lastReading = Point.distBetween(physicalLocation, lastIntersection);
		}
		
	}
	
	/**
	 * Gets the last reading this sensor took
	 * @return last distance measured
	 */
	@Override
	public double getLastReading() {
		return lastReading +  2 * ERROR_VALUE * (Math.random() - 0.5);
	}
	
	/**
	 * Accessor for relativeHeading
	 * @return the sensor's heading relative to straight forward
	 */
	@Override
	public double getRelativeHeading() {
		return relativeHeading;
	}

	
	/*
	private double getAbsoluteHeading() {
		double result;

		result = env.robot.physicalRobot.heading + relativeHeading;

		if (result > 359.9999999f) {
			result = result - 360;
		} else if (result < -0.0000001f) {
			result = result + 360;
		}

		return result; 
	}
	*/

	public void printPoint(Point p) {
		System.out.println("x: " + p.x + " y: " + p.y);
	}

	public Vector normalize(Vector v) {
		double len = Math.hypot(v.x, v.y);
		return new Vector(v.x / len, v.y / len);
	}


}