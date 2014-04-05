import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpListener {

	public static void main(String[] args) {
		
		int port = 0;
		
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.err.println("usage: java UdpListener <port>");
			System.exit(-1);
		}		

		System.out.println("listening on port " + port);
		
		DatagramSocket sock = null;

		byte[] buff = new byte[1024];
		DatagramPacket pkt = new DatagramPacket(buff, buff.length);

		try {
			sock = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		int packetCounter = 0;
		
		for (;;) {
			try {
				sock.receive(pkt);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

			int len = pkt.getLength();
			String received = new String(pkt.getData()).substring(0, len);

			System.out.println("-------------------------------");
			System.out.println("Packet " + packetCounter++ + ":\n" + received);
		}


	}

}
