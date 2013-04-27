package ttp;

import java.util.Hashtable;

public class EndConnectionTimer extends Thread implements Runnable{
	private long sleeptime;
	private ConDescriptor client;
	private Hashtable<String, ConDescriptor> clientList;
	
	public EndConnectionTimer(int time, ConDescriptor client, 
							  Hashtable<String, ConDescriptor> clientList) {
		this.sleeptime = time * 1000;
		this.client = client;
		this.clientList = clientList;
	}
	
	public void run() {
		try {
			Thread.sleep(sleeptime);
		} catch (InterruptedException e) {
			return;
		}
		clientList.remove(client.getKey());
		System.out.println("Server connection closed");
		System.out.println("remove success? " + !clientList.containsKey(client.getKey()));
	}
}
