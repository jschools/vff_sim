

public class PhysicalRobot implements Runnable {

	final static int TIMESTEP = 10;	// ms
	final static double MASS = 750f;	// grams
	final static double WIND_X = 0;
	final static double WIND_Y = 0;
	
	boolean running = true;

	Point position;			// units
	double heading;			// degrees from East
	double angularVelocity;	// degrees/second
	Vector velocity;		// units/second
	Vector netForce;		// newtons
	Vector windForce;		// newtons


	public PhysicalRobot(Point position, double heading) {
		this.position = position;
		this.heading = heading;
		this.velocity = new Vector(0, 0);
		this.netForce = new Vector(0, 0);
		this.windForce = new Vector(WIND_X, WIND_Y);

	}


	public void run() {
		while (running) {
			
			// move
			this.position.x += this.velocity.x;
			this.position.y += this.velocity.y;

			// rotate
			this.heading += this.angularVelocity;
			while (this.heading > 360) {
				this.heading -= 360;
			}
			while (this.heading < 0) {
				this.heading += 360;
			}

			// wait until next time step
			Constants.simDelay(TIMESTEP);
		}
	}



}
