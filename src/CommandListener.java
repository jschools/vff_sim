import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommandListener implements Runnable {

	private RobotController controller;
	
	private DatagramSocket sock;
	private DatagramPacket pkt;
	
	private boolean running;
	
	public CommandListener(RobotController controller) {
		this.controller = controller;
		
		byte[] buf = new byte[1024];
		this.pkt = new DatagramPacket(buf, buf.length);
		
		try {
			sock = new DatagramSocket(Constants.COMMAND_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		running = true;	
	}
	
	
	@Override
	public void run() {
		System.out.println("CommandListener up");
		while (running) {
			try {
				sock.receive(pkt);
				String command = new String(pkt.getData()).substring(0, pkt.getLength());
				interpretCommand(command);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}
	}
	
	private void interpretCommand(String command) {
		String regex = "^[" + Constants.COMMAND_TYPES + "]<.*>$";
		if (!Pattern.matches(regex, command)) {
			System.err.println("\"" + command + "\" could not be interpreted");
			return;
		}
		
		Pattern p = Pattern.compile(".<(.*)>");
		Matcher m = p.matcher(command);
		m.find();
		
		char type = command.charAt(0);
		String params = m.group(1);
		
		switch (type) {
		case 'T':
			interpretTargetCommand(params);
			break;
		case 'S':
			interpretStopCommand(params);
			break;
			
		
		}
		
	}
	
	private void interpretTargetCommand(String params) {
		Point newTarget = Point.parsePoint(params);
		controller.setTarget(newTarget);
		
		if (newTarget != null)
			System.out.println("got target: " + newTarget);

	}
	
	private void interpretStopCommand(String params) {
		controller.setTarget(null);
		System.out.println("got stop command");
	}
	
}
