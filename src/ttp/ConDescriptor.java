package ttp;

import datatypes.Datagram;

public class ConDescriptor extends TTPservice{
	private String serveraddr;
	private String clientaddr;
	private short serverport;
	private short clientport;
	private int serverSYN;
	private int expectSYN;
	private int ACK;
	private EndConnectionTimer closeTimer;
	private boolean connected;
	private Object data;
	private DataSegment[] sendData;
	private int dataLength;
	private int tempACK;
	
	private SendWithTimer timer;
	
	private int windowBegin;
	private int windowEnd;
	private int windowSend;
	private int WINDOWSIZE;
	
	public ConDescriptor(String serveraddr, String clientaddr, short serverport, short clientport,
			 			 int clientSYN){
		super(serveraddr, serverport);
		this.serveraddr = serveraddr;
		this.clientaddr = clientaddr;
		this.serverport = serverport;
		this.clientport = clientport;
		ACK = 0;
		serverSYN = 0;
		WINDOWSIZE = 5;
		windowBegin = 0;
		windowSend = 0;
		connected = false;
		
	}
	
	private void constructDataSegment(Object[] d, short[] dLength) {
		dataLength = d.length;
		windowEnd = min(windowBegin + WINDOWSIZE, d.length);
		int tempSYN = serverSYN;
		sendData = new DataSegment[d.length];
		for(int i = 0; i < d.length; i++) {
			sendData[i].data = d[i];
			sendData[i].dataLength = dLength[i];
			sendData[i].SYN = tempSYN;
			tempSYN += dLength[i];
			sendData[i].expectedSYN = tempSYN;
		}
	}
	
	public void send(Object[] d, short[] dLength) {
		constructDataSegment(d, dLength);
		
		while (windowBegin < windowEnd) {
			for (;windowSend < windowEnd; windowSend++) {
				sendData[windowSend].timer = sendData(ACK, sendData[windowSend].SYN, 
						clientaddr, clientport, sendData[windowSend].data, 
						sendData[windowSend].dataLength, (char)3, 5);
			}
			moveWindow();
		}		
	}
	
	public void moveWindow() {
		boolean moveFlag = false;
		for (int i = windowBegin; i < windowEnd; i++) {
			if (sendData[i].expectedSYN == tempACK) {
				windowBegin = i + 1;
				windowEnd = min(windowBegin + WINDOWSIZE, dataLength) - 1;
				moveFlag = true;
			}
		}
		
		if (!moveFlag) {
			windowSend = windowBegin;
		}
	}
	
	public void setTempACK(int val) {
		tempACK = val;
	}
	
	public Object[] getSendData() {
		return sendData;
	}
	
	public void setData(Datagram data) {
		this.data = data;
	}
	
	public Object readData() {
		return data;
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
	
	private int min(int a, int b) {
		return a > b ? b : a;
	}
}
