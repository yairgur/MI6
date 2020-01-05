package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.ThreadCounter;
import bgu.spl.mics.application.messages.ExpirationBroadcastEvent;
import bgu.spl.mics.application.messages.MissionRecivedEvent;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.messages.TickBroadcast;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
	Map<MissionInfo, Integer> missionMap;
	int id;
	public static int ID = 1;
	private MessageBroker mb;

	public Intelligence() {
		super("Intelligence" + ID);
		id = ID;
		ID++;
		missionMap = new LinkedHashMap<>();
		mb = MessageBrokerImpl.getInstance();
	}

	public void addMission(MissionInfo mission){
		missionMap.put(mission, mission.getTimeIssued());
	}

	private void sendMission(MissionInfo m){
		MissionRecivedEvent missEvent = new MissionRecivedEvent(m.getMissionName(), m.getSerialAgentsNumbers(), m.getGadget(), m.getTimeIssued(), m.getTimeExpired(), m.getDuration());
		Future futureMissionEvent =  getSimplePublisher().sendEvent(missEvent);
	}


	@Override
	protected void initialize() {
		ThreadCounter.getInstance().increase();
		mb.register(this);
		subscribeBroadcast(TickBroadcast.class, (tick)-> {
			for(MissionInfo mission:missionMap.keySet())
			{
				if(mission.getTimeIssued() == tick.getCurrentTick())
				{
					sendMission(mission);
					missionMap.put(mission, 0);
				}
			}


		});

		subscribeBroadcast(ExpirationBroadcastEvent.class, (broadcast) -> {
			terminate();
		});
	}
}

