public class Vector {

	public double x;
	public double y;

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Vector v) {
		this(v.x, v.y);
	}

	public Vector() {
		this(0, 0);
	}
	
	public double getAngle() {
		return (double) Math.toDegrees(Math.atan2(this.y, this.x));
	}
	
	public double getLength() {
		return (double) Math.hypot(this.x, this.y);
	}
	
	/**
	 * Modifies this vector, adding a vector
	 * @param v vector to add to this
	 */
	public Vector add(Vector v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}
	
	public Vector scale(double factor) {
		return new Vector(this.x * factor, this.y * factor);
	}
	
	public static Vector add(Vector a, Vector b) {
		return new Vector(a.x + b.x, a.y + b.y);
	}
	
	/**
	 * Returns a new vector of opposite direction
	 * @return new vector
	 */
	public Vector opposite() {
		return new Vector(-1f * this.x, -1f * this.y);		
	}
	
	public String toString() {
		return String.format("v:%f:%f:", this.y, this.x);
		//return String.format("V<%+08.3f,%+08.3f>", this.x, this.y);
	}

	
	/**
	 * Creates a vector from the Point operation B - A
	 * @param a Point A
	 * @param b Point B
	 * @return a new vector representing B - A
	 */
	public static Vector pointSubtraction(Point a, Point b) {
		
		return new Vector(b.x - a.x, b.y - a.y);
	}
	
	/**
	 * Creates a rectangular vector from polar coordinates
	 * @param r distance
	 * @param theta angle (degrees)
	 * @return a new Vector in rectangular coordinates
	 */
	public static Vector vectorFromPolar(double r, double theta) {
		
		double x = (double) (r * Math.cos(Math.toRadians(theta)));
		double y = (double) (r * Math.sin(Math.toRadians(theta)));
		
		Vector result = new Vector(x, y);
		
		return result;
	}
	
	
	public static double averageAngle(double a, double b) {
		return Vector.add(Vector.vectorFromPolar(1, a), Vector.vectorFromPolar(1, b)).getAngle();
	}
	
	public static double headingFromZero(double angle) {
		angle = Vector.mod360(angle);
		if (angle > 180) {
			angle = angle - 360;
		}
		
		return angle;
	}
	
	public static double mod360(double angle) {
		while (angle >= 360f)
			angle -= 360f;
		while (angle < 0f)
			angle += 360f;
		
		return angle;
	}

}