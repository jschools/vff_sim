
public class HeadingSensor {
	
	private double lastHeading;
	PhysicalRobot physicalBot;

	public HeadingSensor(PhysicalRobot physicalBot) {
		this.physicalBot = physicalBot;
		lastHeading = 0;
	}
	
	public void takeReading() {
		double heading = physicalBot.heading;
		
		// add error
		//heading += 180 * (Math.random() - 0.5f);
		
		this.lastHeading = heading;
	}
	
	public double getLastHeading() {
		return lastHeading;
	}
	
}
