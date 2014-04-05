import java.util.List;


public interface SensorManager extends Runnable {

	public Point getLastLocation();
	public double getLastHeading();
	public List<? extends DistanceSensor> getDistanceSensors();	

}
