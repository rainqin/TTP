package ttp;

import java.util.Hashtable;
import datatypes.Datagram;

public class ServerTTPService extends TTPservice{
	private Hashtable<String, ConDescriptor> clientList;
	public ServerTTPService(String srcaddr, short srcPort) {
		super(srcaddr, srcPort);
		
		clientList = new Hashtable<String, ConDescriptor>();
	
	}
	
	public ConDescriptor serverListen(){
		
		Datagram datagram = receiveData();
		if (datagram == null) {
			return null;
		}
		
		TTP ttp = (TTP)datagram.getData();
		System.out.println("Server Receive from client, Category: " + (int)ttp.getCategory());
		
		if (ttp.getCategory() == (char)0) { 	//syn packet
			String dstaddr = datagram.getSrcaddr();
			short dstPort = datagram.getSrcport();
			
			ConDescriptor client = new ConDescriptor(srcaddr, dstaddr,
										srcPort, dstPort, ttp.getSYN());
			
			client.setACK(ttp.getSYN() + ttp.getLength());
			
			clientList.put(client.getKey(), client);
			
			serverSendData(client, null, (short)0, (char)1);
			
		} else if (ttp.getCategory() == (char)2) { // last hand shake
			if (clientList.containsKey(datagram.getSrcaddr() + datagram.getSrcport())) {
				System.out.println("Hand shake finishes");
				ConDescriptor client = clientList.get(datagram.getSrcaddr() 
														 + datagram.getSrcport());
				client.setConnected(true);
				client.killTimer();
				
				client.setACK(ttp.getSYN() + ttp.getLength());
			}
		} else if (ttp.getCategory() == (char)4) { //FIN
			System.out.println("Receive FIN");
			if (clientList.containsKey(datagram.getSrcaddr() + datagram.getSrcport())) {
				ConDescriptor client = clientList.get(datagram.getSrcaddr() 
														 + datagram.getSrcport());
				client.setConnected(false);
				client.killTimer();
				
				client.setACK(ttp.getSYN() + ttp.getLength());
				serverSendData(client, null, (short)1, (char)5, 1);
				
				client.runCloseTimer();
			}				
		} else if (ttp.getCategory() == (char)6) { //FIN + ACK
			System.out.println("Receive FIN + ACK");
			if (clientList.containsKey(datagram.getSrcaddr() + datagram.getSrcport())) {
				ConDescriptor client = clientList.get(datagram.getSrcaddr() 
														 + datagram.getSrcport());
				client.killTimer();
				
				if (client.isConnected()) {
					serverSendData(client, null, (short)1, (char)5, 1);
				} else {
					clientList.remove(client.getKey());
					client.stopCloseTimer();
					System.out.println("Server connection closed");
					System.out.println("remove success? " + !clientList.containsKey(client.getKey()));
				}
			}	
		} else if (ttp.getCategory() == (char)3) {									//requset
			if (clientList.containsKey(datagram.getSrcaddr() + datagram.getSrcport())) {
				System.out.println("Receive Data From Client");
				ConDescriptor client = clientList.get(datagram.getSrcaddr() 
														 + datagram.getSrcport());
				client.killTimer();
				
				client.setACK(ttp.getSYN() + ttp.getLength());
				
				if (client.isConnected()) {
					int ack = ttp.getACK();
					client.setTempACK(ack);
					if (ack == client.getExpectSYN()) { //right
						client.setServerSYN(ack);
						client.setACK(ttp.getSYN()+ttp.getLength()) ;
						
						if (ttp.getData() != null) {
							client.setData(datagram);
							return client;
						}
						
					} 
				} else {
					serverSendData(client, null, (short)0, (char)1);
				}
			}
		}
		
		return null;
	}
	
	private int serverSendData(ConDescriptor client, Object data, short dataLength, char category) {
		SendWithTimer timer = super.sendData(client.getACK(), client.getServerSYN(), client.getClientAddr(), 
					   client.getClientPort(), data, dataLength, category, 5);
		client.setTimer(timer);
		client.setExpectSYN(client.getServerSYN() + dataLength);
		
		return 0;
	}
	
	private int serverSendData(ConDescriptor client, Object data, short dataLength, char category, int count) {
		SendWithTimer timer = super.sendData(client.getACK(), client.getServerSYN(), client.getClientAddr(), 
					   client.getClientPort(), data, dataLength, category, count);
		client.setTimer(timer);
		client.setExpectSYN(client.getServerSYN() + dataLength);
		
		return 0;
	}
}
