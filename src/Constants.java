
public class Constants {

	public static final boolean USE_DEMO_CONTROLLER = false;

	public static final int SONAR_PORT = 6000;
	public static final int IMU_PORT = 6001;
	public static final int MOVEMENT_PORT = 6006;
	public static final int COMMAND_PORT = 6003;
	public static final int FEEDBACK_PORT = 6008;


	final static String MOVEMENT_ADDR = "127.0.0.1";
	final static String COMMAND_ADDR = MOVEMENT_ADDR;
	final static String FEEDBACK_ADDR = MOVEMENT_ADDR;
	/*
	final static String MOVEMENT_ADDR = "128.208.7.255";
	final static String COMMAND_ADDR = "128.208.7.255";
	final static String FEEDBACK_ADDR = "128.208.7.255";
	 */

	public static final int SOCKET_TIMEOUT = 100;

	final static String COMMAND_TYPES = "TS";


	public static void simDelay(int ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {

		}
	}

}
