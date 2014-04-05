


public class Robot {
	
	public static final boolean USE_SIM = true;
	
	public Environment env;
	public RobotController controller;
	public SensorManager sensorManager;
	public MovementController movementController;
	public PhysicalRobot physicalRobot;
	public CommandListener commandListener;
	public SimMovementListener movementListener;
	

	public Robot(double[] sensorLocations) {
		this.sensorManager = new UdpSensorManager(sensorLocations);
		this.movementController = new MovementController();
		this.controller = new RobotController(this);
		this.commandListener = new CommandListener(this.controller);
	}
	
	
	public Robot(Environment env, Point location, double heading, double[] sensorLocations) {
		this.physicalRobot = new PhysicalRobot(location, heading);
		
		this.env = env;
		env.setRobot(this, physicalRobot);
		
		this.sensorManager = new SimSensorManager(env, sensorLocations, physicalRobot);
		this.movementController = new MovementController(physicalRobot);
		this.controller = Constants.USE_DEMO_CONTROLLER ? new DemoController(this) : new RobotController(this);
		this.commandListener = new CommandListener(this.controller);
		this.movementListener = new SimMovementListener(physicalRobot);
		
	}
	
	public void startServices() {
		new Thread(sensorManager).start();
		new Thread(physicalRobot).start();
		new Thread(controller).start();
		new Thread(commandListener).start();
		new Thread(movementListener).start();
	}

}