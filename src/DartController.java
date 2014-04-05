
public class DartController {

	public static final double[] SENSOR_LOCATIONS = {-55, -20, 20, 55};
	
	public static void main(String[] args) {
		
		Robot bot = new Robot(SENSOR_LOCATIONS);
		bot.startServices();
		
	}
	
}
