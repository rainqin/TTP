package ttp;

import java.io.IOException;
import java.net.SocketException;
import datatypes.Datagram;

import services.DatagramService;


public abstract class TTPservice {
	 protected String srcaddr;
	 protected short srcPort;
	 
	 protected int windowSize;
	 
	 private short TTPHeaderLength;
	 
	 private DatagramService datagramService;
	 
	 public TTPservice(String srcaddr, short srcPort) {
		 this.srcaddr = srcaddr;
		 this.srcPort = srcPort;

		 TTPHeaderLength = 0;
		 
		 windowSize = 5;
		 
		 try {
			 datagramService = new DatagramService(srcPort, 0);
		 } catch (SocketException e) {
			 System.out.println("TTP Service construction exception");
		 }
	 }
	 
	 public SendWithTimer sendData (int ACK, int SYN, String dstaddr, short dstPort, 
			 			  Object data, short dataLength, char category, int count) {
		TTP ttp = new TTP(ACK, SYN, data, dataLength, category);
		Datagram datagram = new Datagram(srcaddr, dstaddr, srcPort, dstPort, 
							(short)(dataLength + TTPHeaderLength), ttp.getCheckSum(), ttp);
		
		System.out.println("Sending data to " + datagram.getDstaddr() + ":" 
							+ datagram.getDstport());
		
		SendWithTimer sendWithTimer = new SendWithTimer(10000, datagram, datagramService, count);	
		sendWithTimer.start();
		return sendWithTimer;
	}
		
	public Datagram receiveData () {
		try {
			return datagramService.receiveDatagram();
		} catch (ClassNotFoundException e) {
			System.out.println("TTP Service receiveData exception");
		} catch (IOException e) {
			System.out.println("TTP Service receiveData exception");
		} 
		return null;
	}
}
