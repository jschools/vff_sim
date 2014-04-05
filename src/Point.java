import java.util.regex.Pattern;


public class Point {

	public double x;
	public double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point() {
		this(0, 0);
	}

	public Point(Point p) {
		this((p != null ? p.x : 0), (p != null ? p.y : 0));
	}

	public double getHeadingTo(Point destination) {

		double dx = destination.x - this.x;
		double dy = destination.y - this.y;

		double theta = Math.atan2(dy, dx);

		return (double) Math.toDegrees(theta);
	}
	
	/**
	 * Calculates the position of a new point exactly 1 unit in the
	 * direction of heading
	 * @param heading in degrees
	 * @return new point
	 */
	public Point distancePoint(double distance, double heading) {
		Point result = new Point();

		double dx = (double) Math.cos(Math.toRadians(heading)) * distance;
		double dy = (double) Math.sin(Math.toRadians(heading)) * distance;
		result.x = this.x + dx;
		result.y = this.y + dy;

		return result;
	}

	public String toString() {
		return String.format("<%012.8f,%012.8f>", x, y);
	}
	
	public static double distBetween(Point a, Point b) {
		return (double) Math.hypot(b.x - a.x, b.y - a.y);
	}
	
	/**
	 * Parses a point string in the format of /^-?[0-9]{3}\.[0-9]{8},-?[0-9]{3}\.[0-9]{8}$/
	 * @param string
	 * @return
	 */
	public static Point parsePoint(String string) {
		if (!Pattern.matches("^-?\\d{3}\\.\\d{8},-?\\d{3}\\.\\d{8}$", string)) {
			System.err.println("\"" + string + "\" cannot be parsed as a Point");
			return null;
		}
		
		String[] pieces = string.split(",");
		
		double x = Double.parseDouble(pieces[0]);
		double y = Double.parseDouble(pieces[1]);		
		
		return new Point(x, y);
	}

}