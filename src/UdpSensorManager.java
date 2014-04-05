import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;


public class UdpSensorManager implements SensorManager {

	private List<DistanceSensor> distanceSensors;
	private Point lastLocation;
	private double lastHeading;

	private DatagramSocket sonarSock = null;
	private DatagramSocket imuSock = null;

	private byte[] buff = new byte[1024];

	private boolean running;

	public static final String SONAR_PACKET_REGEX = "^([0-9]R.{3}:?)*";
	public static final String GPS_REGEX = ".*"; // "LAT:-?[0-9]*,LON:-?[0-9]*,ALT:[0-9]*";

	/**
	 * Constructs a SensorManager that receives data over UDP from the
	 * ports defined above.
	 * @param sensorLocations The relative headings of all of the sensors
	 * that will be streaming data to the UdpSensorManager, <b>in the order that
	 * the data will arrive.</b>
	 */
	public UdpSensorManager(double[] sensorLocations) {
		running = true;

		distanceSensors = new ArrayList<DistanceSensor>();

		for (double heading : sensorLocations) {
			distanceSensors.add(new DistanceSensor(heading));
		}


		try {
			// create sockets and set timeouts appropriately
			sonarSock = new DatagramSocket(Constants.SONAR_PORT);
			sonarSock.setSoTimeout(Constants.SOCKET_TIMEOUT);

			imuSock = new DatagramSocket(Constants.IMU_PORT);
			imuSock.setSoTimeout(Constants.SOCKET_TIMEOUT);
		} catch (SocketException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Main thread loop
	 */
	@Override
	public void run() {
		System.out.println("UdpSensorManager up");

		while (running) {

			updateSonarData();
			updateImuData();

		}


	}


	private void updateSonarData() {
		String sonarDataString = receivePacket(sonarSock);

		if (sonarDataString == null)
			return;

		// check that packet is formatted correctly
		if (!sonarDataString.matches(UdpSensorManager.SONAR_PACKET_REGEX)) {
			System.out.println("sonar packet dropped: " + sonarDataString);
			return;
		}

		// 0Rxxx:1Rxxx:2Rxxx:3Rxxx:4Rxxx:\n
		String[] pieces = sonarDataString.split(":");

		for (int i = 0; i < distanceSensors.size(); i++) {
			String value = pieces[i].substring(2);
			distanceSensors.get(i).setDistance(Double.parseDouble(value));
		}

	}


	private void updateImuData() {
		String imuDataString = receivePacket(imuSock);

		if (imuDataString == null)
			return;

		if (!imuDataString.matches(GPS_REGEX)) {
			System.out.println("imu packet dropped: " + imuDataString);
			return;
		}

		parseImuData(imuDataString);

	}


	private void parseImuData(String imuDataString) {
		String[] imuPieces = imuDataString.split(",");

		double lat = 0;
		double lon = 0;
		double yaw = 0;

		System.out.println(imuDataString);
		for (String piece : imuPieces) {
			String[] strings = piece.split(":");
			if (strings[0] != null) {
				if (strings[0].equals("FIX")) {
					System.out.println("fix: " + strings[1]);
					if (strings[1].equals("0")) {
						this.lastLocation = null;
					}
				} else if (strings[0].equals("LAT")) {
					lat = Double.parseDouble(strings[1]) / 10000000.0;
					System.out.println("lat: " + lat);
				} else if (strings[0].equals("LON")) {
					lon = Double.parseDouble(strings[1]) / 10000000.0;
					System.out.println("lon: " + lon);
				} else if (strings[0].equals("YAW")) {
					yaw = Double.parseDouble(strings[1]);
					System.out.println("yaw: " + yaw);
					if (yaw < -0.00000001) {
						yaw += 360.0;
					}
				}
			}
		}

		if (lat != 0 && lon != 0) {
			System.out.println("setting last location: " + lat + " " + lon);
			this.lastLocation = new Point(lat, lon);
		}
		this.lastHeading = yaw;


	}


	private String receivePacket(DatagramSocket sock) {
		DatagramPacket pkt = new DatagramPacket(buff, buff.length);

		try {
			sock.receive(pkt);
		} catch (SocketTimeoutException ste) {
			// nothing to receive
			return null;
		} catch (IOException se) {
			se.printStackTrace();
			return null;
		}

		int len = pkt.getLength();
		String result = new String(pkt.getData()).substring(0, len);

		return result;
	}

	@Override
	public Point getLastLocation() {
		//return new Point(47.653884, -122.307358);
		return this.lastLocation;
	}

	@Override
	public double getLastHeading() {
		return this.lastHeading;
	}

	@Override
	public List<? extends DistanceSensor> getDistanceSensors() {
		return this.distanceSensors;
	}

}
