package ttp;

import java.util.Hashtable;



public class ConDescriptor {
	private String serveraddr;
	String clientaddr;
	private short serverport;
	short clientport;
	private int serverSYN;
	private int expectSYN;
	private int ACK;
	private EndConnectionTimer closeTimer;
	private boolean connected;
	
	private SendWithTimer timer;
	
	public ConDescriptor(String serveraddr, String clientaddr, short serverport, 
						 short clientport, int clientSYN, Hashtable<String, ConDescriptor> clientList){
		this.serveraddr = serveraddr;
		this.clientaddr = clientaddr;
		this.serverport = serverport;
		this.clientport = clientport;
		this.closeTimer = new EndConnectionTimer(30, this, clientList);
		
		serverSYN = 0;
		
		connected = false;
		
	}
	
	public boolean equals(ConDescriptor b){
		if (serveraddr.equals(b.serveraddr) && clientaddr.equals(b.clientaddr) 
				&& serverport == b.serverport && clientport ==b.clientport)
			return true;
		else 
			return false;
	}
	
	public void runCloseTimer() {
		closeTimer.start();
	}
	
	public void stopCloseTimer() {
		if (closeTimer.isAlive()) {
			closeTimer.interrupt();
		}
	}
	
	public String getKey() {
		return clientaddr + clientport;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void setConnected(boolean status) {
		connected = status;
	}
	
	public String getServerAddr() {
		return serveraddr;
	}
	
	public String getClientAddr() {
		return clientaddr;
	}
	
	public short getServerPort() {
		return serverport;
	}
	
	public short getClientPort() {
		return clientport;
	}


	public void setTimer(SendWithTimer timer) {
		this.timer = timer;
	}
	
	public void killTimer() {
		if (timer != null && timer.isAlive()) {
			timer.interrupt();
		}
	}

	public int getACK() {
		return ACK;
	}

	public void setACK(int ACK) {
		this.ACK = ACK;
	}

	public int getServerSYN() {
		return serverSYN;
	}

	public void setServerSYN(int serverSYN) {
		this.serverSYN = serverSYN;
	}
	
	public int getExpectSYN() {
		return expectSYN;
	}
	
	public void setExpectSYN(int syn) {
		expectSYN = syn;
	}
}
