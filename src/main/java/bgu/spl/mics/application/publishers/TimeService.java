package bgu.spl.mics.application.publishers;
import bgu.spl.mics.Publisher;
import bgu.spl.mics.SimplePublisher;
import bgu.spl.mics.application.messages.ExpirationBroadcastEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {
	private int duration;
	private Timer timer;
	private AtomicLong counter;

	public TimeService(int duration) { // new C'tor
		super("Time Service");
		counter = new AtomicLong(0);
		timer = new Timer();
		this.duration = duration;
	}

	@Override
	protected void initialize() {

	}

//	//@Override
//	public void run1() {
//		System.out.println("Time Service has been init wohoooooooo");
//		SimplePublisher simplePublisher = new SimplePublisher();
//		TimerTask taskToDo = new TimerTask()
//		{
//			@Override
//			public void run()
//			{
//				if(counter.longValue() <= duration)
//				{
//					simplePublisher.sendBroadcast(new TickBroadcast(counter.longValue()));
//					counter.incrementAndGet();
//				}
//				else {
//					ExpirationBroadcastEvent event = new ExpirationBroadcastEvent();
//					simplePublisher.sendBroadcast(event);
//				}
//			}
//		};
//		timer.schedule(taskToDo, 0, 100); //should be delay?
//
//	}

	@Override
	public void run(){
		boolean arrived = false;
		SimplePublisher simplePublisher = new SimplePublisher();
		while(counter.longValue() < duration){
			try {
				counter.incrementAndGet();
				Thread.sleep(100);
				simplePublisher.sendBroadcast(new TickBroadcast(counter.longValue()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(counter.longValue() == duration-1){
				arrived = true;
			}
			if(arrived)
			{
				ExpirationBroadcastEvent event = new ExpirationBroadcastEvent();
				simplePublisher.sendBroadcast(event);
				break;
			}
		}
	}
}
