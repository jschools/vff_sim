




public class SimLocationSensor extends LocationSensor {

	Point lastReading;
	PhysicalRobot physicalBot;
	
	public SimLocationSensor(PhysicalRobot physicalBot) {
		this.physicalBot = physicalBot;
		lastReading = null;
	}
	
	public void takeReading() {
		Point pos = new Point(physicalBot.position);
		
		// add error
		//pos.x += 100f * (Math.random() - 0.5f);
		//pos.y += 100f * (Math.random() - 0.5f);
		
		//System.out.println(pos);
		
		lastReading = pos;
	}
	
	public Point getLastLocation() {
		return new Point(lastReading);
	}
	
	
}
