/*
 * A sample client that uses DatagramService
 */

package applications;

import java.io.IOException;
import java.net.SocketException;

import services.DatagramService;
import datatypes.Datagram;

public class client {

	private static DatagramService ds;
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		if(args.length != 2) {
			printUsage();
		}
		
		System.out.println("Starting client ...");
		
		int port = Integer.parseInt(args[0]);
		ds = new DatagramService(10);
		
		Datagram datagram = new Datagram();
		datagram.setData("Hello World!");
		datagram.setSrcaddr("128.237.123.88");
		datagram.setDstaddr("localhost");
		datagram.setDstport((short)Integer.parseInt(args[1]));
		datagram.setSrcport((short)port);
		
		ds.sendDatagram(datagram);
		System.out.println("Sent datagram");
		
		datagram = ds.receiveDatagram(0,false);
		System.out.println("Received " + datagram.getData());
	}
	
	private static void printUsage() {
		System.out.println("Usage: server <localport> <serverport>\n");
		System.exit(-1);
	}
}
