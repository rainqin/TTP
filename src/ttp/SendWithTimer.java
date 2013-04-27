package ttp;

import java.io.IOException;
import services.DatagramService;
import datatypes.Datagram;

public class SendWithTimer extends Thread implements Runnable{
	
	private int time;
	private Datagram data;
	private DatagramService datagramService;
	private int sendCount;
	
	public SendWithTimer(int milliseconds, Datagram datagram, DatagramService dataService, 
				int count) {
		time = milliseconds;
		data = datagram;
		datagramService = dataService;
		sendCount = count;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(sendCount > 0 && !Thread.interrupted()) {
			
			try {
				TTP ttp = (TTP)data.getData();
				int category = (int)ttp.getCategory();
				System.out.println("Sending data Category: " + category);
				datagramService.sendDatagram(data);
				Thread.sleep(time);
			} catch (InterruptedException e) {
				break;
			} catch (IOException e) {
				System.out.println("sending Exception");
			}	
			
			sendCount--;
		}		
	}
}
