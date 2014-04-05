import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;



public class UdpFeedbackSender {
	
	DatagramSocket sock;
	DatagramPacket pkt;
	List<Integer> ranges;

	public UdpFeedbackSender() {
		
		try {
			sock = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		ranges = new ArrayList<Integer>();
		
	}
	
	public void addRange(int range) {
		ranges.add(range);
	}
	
	public void sendRanges() {
		String rangeString = "R";
		for (int r : ranges) {
			rangeString += r + ":";
		}
		ranges.clear();
		sendMessage(rangeString);
	}
	
	public void sendVelocity(Vector vel) {
		String velString = "V" + vel.x + ":" + vel.y;
		sendMessage(velString);
	}
	
	public void sendHeading(float heading) {
		String hString = "H" + heading;
		sendMessage(hString);
	}
	
	private void sendMessage(String message) {

		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(Constants.FEEDBACK_ADDR);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		DatagramPacket pkt = new DatagramPacket(message.getBytes(), message.length(), addr, Constants.FEEDBACK_PORT);
		try {
			sock.send(pkt);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
