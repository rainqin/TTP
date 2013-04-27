package ttp;

public class Timer extends Thread implements Runnable{
	
	private long time;
	private Thread thread;
	
	public Timer(int sleepSecond, Thread t) {
		time = sleepSecond * 1000;
		thread = t;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(time);
			thread.interrupt();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
