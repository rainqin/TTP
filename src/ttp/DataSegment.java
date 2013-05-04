package ttp;

public class DataSegment {
	public Object data;
	public short dataLength;
	public int SYN;
	public int expectedSYN;
	public SendWithTimer timer;
	
	public DataSegment() {
		
	}
	
	public DataSegment(Object data, short len, int SYN) {
		this.data = data;
		this.dataLength = len;
		this.SYN = SYN;
		expectedSYN = SYN + len;
	}
}
