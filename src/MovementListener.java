import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class MovementListener {

	
	public static void main(String[] args) {
		
		DatagramSocket sock = null;
		
		byte[] buff = new byte[1024];
		DatagramPacket pkt = new DatagramPacket(buff, buff.length);
		
		try {
			sock = new DatagramSocket(Constants.MOVEMENT_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		for (;;) {
			try {
				sock.receive(pkt);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			int len = pkt.getLength();
			String received = new String(pkt.getData()).substring(0, len);
			
			System.out.println(received);
		}
		
		
	}
	
}
