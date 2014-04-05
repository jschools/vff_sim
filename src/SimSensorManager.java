



import java.util.ArrayList;
import java.util.List;


public class SimSensorManager implements SensorManager {
	
	final static int SENSOR_UPDATE_INTERVAL = 1;
	final static int SONAR_INTERFERENCE_INTERVAL = 0;

	boolean running;
	
	List<SimDistanceSensor> distanceSensors;
	SimLocationSensor locationSensor;
	HeadingSensor headingSensor;
	Environment env;
	
	
	public SimSensorManager(Environment env, double[] sensorLocations, PhysicalRobot physicalBot) {
		
		this.env = env;
		distanceSensors = new ArrayList<SimDistanceSensor>();
		locationSensor = new SimLocationSensor(physicalBot);
		headingSensor = new HeadingSensor(physicalBot);
		
		for (double angle : sensorLocations) {
			distanceSensors.add(new SimDistanceSensor(env, angle));
		}

		running = true;
	}


	@Override
	public void run() {
		System.out.println("SimSensorManager up");
		
		while(running) {
			
			for (SimDistanceSensor s : distanceSensors) {
				s.takeReading();
				Constants.simDelay(SONAR_INTERFERENCE_INTERVAL);
			}
			locationSensor.takeReading();
			headingSensor.takeReading();			
			
			Constants.simDelay(SENSOR_UPDATE_INTERVAL);
		}		
	}


	@Override
	public Point getLastLocation() {
		return locationSensor.getLastLocation();
	}


	@Override
	public double getLastHeading() {
		return headingSensor.getLastHeading();
	}


	@Override
	public List<? extends DistanceSensor> getDistanceSensors() {
		return this.distanceSensors;
	}
	
}
