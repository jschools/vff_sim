
public class DistanceSensor {

	protected double lastReading;
	protected double relativeHeading;
	
	public DistanceSensor(double heading) {
		this.relativeHeading = heading;
		this.lastReading = Double.POSITIVE_INFINITY;
	}
	
	public void setDistance(double distance) {
		this.lastReading = distance;
	}
	
	public double getLastReading() {
		return this.lastReading;
	}
	public double getRelativeHeading() {
		return this.relativeHeading;
	}
	
}
