import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class MovementController {

	final static boolean ENABLE_UDP_CONTROL = true;
	final static boolean ENABLE_DIRECT_CONTROL = false;

	final static double MAX_SPEED = 24;

	PhysicalRobot physicalBot;
	DatagramSocket sock;
	DatagramPacket outPkt;


	public MovementController() {
		// set up transmitter socket
		try {
			sock = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	public MovementController(PhysicalRobot robot) {
		this();

		// make sure some form of control is enabled
		if (!(ENABLE_UDP_CONTROL || ENABLE_DIRECT_CONTROL)) {
			System.err.println("No movement control specified.");
		}

		this.physicalBot = robot;
	}

	/**
	 * Sets the velocity of the robot in the direction of 'movement'
	 * @param movement RELATIVE direction to move
	 */
	public void move(Vector movement) {
		// save the direction vector
		Vector direction = new Vector(movement);

		// send desired velocity over UDP
		if (ENABLE_UDP_CONTROL) {
			// rescale velocity so as to be within +- 1.0
			Vector velocity = direction.scale(direction.getLength() / MAX_SPEED);

			// convert velocity to a byte string
			byte[] commandString = velocityString(velocity).getBytes();

			// send velocity string
			try {
				outPkt = new DatagramPacket(commandString, commandString.length, InetAddress.getByName(Constants.MOVEMENT_ADDR), Constants.MOVEMENT_PORT);
				sock.send(outPkt);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void turn(double angle) {

		// send desired turn speed over udp
		if (ENABLE_UDP_CONTROL) {
			byte[] commandString = angleString(angle).getBytes();

			try {
				outPkt = new DatagramPacket(commandString, commandString.length, InetAddress.getByName(Constants.MOVEMENT_ADDR), Constants.MOVEMENT_PORT);
				sock.send(outPkt);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void turnLeft() {
		turn(1);
	}

	public void turnRight() {
		turn(-1);
	}

	public void noTurn() {
		turn(0);
	}

	public void stop() {
		move(new Vector(0, 0));
	}

	public void takeOff() {
		System.out.println("sending takeoff command");

		byte[] commandString = "s:1:".getBytes();

		try {
			outPkt = new DatagramPacket(commandString, commandString.length, InetAddress.getByName(Constants.MOVEMENT_ADDR), Constants.MOVEMENT_PORT);
			sock.send(outPkt);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void land() {
		System.out.println("sending land command");

		byte[] commandString = "s:0:".getBytes();

		try {
			outPkt = new DatagramPacket(commandString, commandString.length, InetAddress.getByName(Constants.MOVEMENT_ADDR), Constants.MOVEMENT_PORT);
			sock.send(outPkt);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String velocityString(Vector velocity) {
		return velocity.toString();
	}

	public static String angleString(double angle) {
		return String.format("h:%f:", angle);
		//return String.format("A<%+08.3f>", angle);
	}

}
