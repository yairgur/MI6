package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.ExpirationBroadcastEvent;
import bgu.spl.mics.application.messages.MissionRecivedEvent;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.messages.TickBroadcast;


import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
	Queue<MissionInfo> missionQueue;
	String name;
	int id;
	public static int ID = 1;

	public Intelligence() {
		super("Intelligence" + ID);
		id = ID;
		ID++;
		missionQueue = new LinkedBlockingQueue<>();
	}

	public void addMission(MissionInfo event){
		missionQueue.add(event);
	}

	public void sendMission(MissionInfo m){
		MissionRecivedEvent missEvent = new MissionRecivedEvent(m.getMissionName(), m.getSerialAgentsNumbers(), m.getGadget(), m.getTimeIssued(), m.getTimeExpired(), m.getDuration());
		getSimplePublisher().sendEvent(missEvent);
	}


	@Override
	protected void initialize() {
		//System.out.println("intelligence start initialize" + id);
		subscribeBroadcast(TickBroadcast.class, (tick)-> {
			while(!missionQueue.isEmpty() && missionQueue.peek().getTimeIssued() <= tick.getCurrentTick()){
				sendMission(missionQueue.poll());
			}
		});
		//System.out.println("intelligence initialize" + id);

		subscribeBroadcast(ExpirationBroadcastEvent.class, (broadcast) -> {
			terminate();
		});
	}
}

