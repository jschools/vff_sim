import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SimMovementListener implements Runnable {
	
	public static final String VELOCITY_REGEX = "v:(.*):(.*):";
	//public static final String VELOCITY_REGEX = ".<(.*),(.*)>";
	
	public static final String HEADING_REGEX = "h:(.*):";
	//public static final String HEADING_REGEX = ".<(.*)>";

	private PhysicalRobot physicalBot;
	private DatagramSocket sock;
	
	private boolean running = false;
	
	public SimMovementListener(PhysicalRobot pBot) {
		physicalBot = pBot;
		
		try {
			sock = new DatagramSocket(Constants.MOVEMENT_PORT);
			sock.setSoTimeout(250);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		running = true;
	}
	
	@Override
	public void run() {
		while (running) {
			parsePacket(getPacket());
		}
	}
	
	private void move(Vector direction) {
		
		// calculate which direction to move robot in world coordinates
		double angle = direction.getAngle() + physicalBot.heading;
		double speed = direction.getLength();
		speed = Math.min(speed, MovementController.MAX_SPEED);
		Vector velocity = Vector.vectorFromPolar(speed, angle);
		physicalBot.velocity = velocity;
	}
	
	
	public void stop() {
		move(new Vector(0, 0));
	}
	
	private void parsePacket(String movement) {
		// return String.format("V<%+08.3f,%+08.3f>", this.x, this.y);
		if (movement == null || movement.length() == 0)
			return;
		
		
		switch (movement.charAt(0)) {
		case 'h':
			Pattern aPattern = Pattern.compile(HEADING_REGEX);
			Matcher m = aPattern.matcher(movement);
			m.find();
			
			String angleString = m.group(1);
			double angle = Double.parseDouble(angleString);
			
			// set the value
			physicalBot.angularVelocity = angle;
			
			break;
			
		case 'v':
			Pattern vPattern = Pattern.compile(VELOCITY_REGEX);
			Matcher v = vPattern.matcher(movement);
			v.find();
			
			String yString = v.group(1);
			String xString = v.group(2);
			
			double x = Double.parseDouble(xString) * 10;
			double y = Double.parseDouble(yString) * 10;
			
			// set the value
			move(new Vector(x, y));
			break;
			
		default:
			
			break;
		}
		
		
		
		
	}
	
	
	private String getPacket() {
		
		byte[] buff = new byte[1024];
		DatagramPacket pkt = new DatagramPacket(buff, buff.length);

		try {
			sock.receive(pkt);
		} catch (SocketTimeoutException ste) {
			move(new Vector(0, 0));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int len = pkt.getLength();
		String received = new String(pkt.getData()).substring(0, len);
				
		return received;
	}
	
}
