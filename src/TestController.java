import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class TestController {

	
	public static void main(String[] args) {
		DatagramSocket sock = init();
		
		while (true) {
			sendTarget(47.653315, -122.307653, sock);
			delay(2000);
//			sendTarget(500, 100, sock);
//			delay(2000);
		}
		
	}
	
	private static DatagramSocket init() {
		DatagramSocket sock = null;
		try {
			sock = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		return sock;
	}
	
	private static void sendTarget(double x, double y, DatagramSocket sock) {
		String command = "T" + new Point(x, y).toString();
		
		try {
			sendPacket(command, sock);
			System.out.println("sent");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void sendPacket(String string, DatagramSocket sock) {
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(Constants.COMMAND_ADDR);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		DatagramPacket pkt = new DatagramPacket(string.getBytes(), string.length(), addr, Constants.COMMAND_PORT);
		try {
			sock.send(pkt);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void delay(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
}
