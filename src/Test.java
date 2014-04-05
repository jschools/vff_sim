

public class Test {

	public static void main(String[] args) {
		
		for (int i = 0; i <= 255; i++) {
			distToForce(i);
		}
		
		
	}
	
	public static double distToForce(double distance) {
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
		
		System.out.println("dist: " + distance + " --> force: " + force);
		return force;
	}
	
	private static void parseImuData(String imuDataString) {
		String[] imuPieces = imuDataString.split(",");
		
		double lat = 0;
		double lon = 0;
		double yaw = 0;
		
		for (String piece : imuPieces) {
			String[] strings = piece.split(":");
			if (strings[0] != null) {
				if (strings[0].equals("LAT")) {
					lat = Double.parseDouble(strings[1]) / 10000000.0;
				} else if (strings[0].equals("LON")) {
					lon = Double.parseDouble(strings[1]) / 10000000.0;
				} else if (strings[0].equals("YAW")) {
					yaw = Double.parseDouble(strings[1]);
					if (yaw < -0.00000001) {
						yaw += 360.0;
					}
				}
			}
		}
		
//		this.lastLocation = new Point(lat, lon);
//		this.lastHeading = yaw;
/*		
		System.out.println("latitude: " + lat);
		System.out.println("longitude: " + lon);
		System.out.println("heading: " + yaw);
*/		
	}
	
}
