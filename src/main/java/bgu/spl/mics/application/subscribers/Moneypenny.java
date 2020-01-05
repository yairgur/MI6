package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.ThreadCounter;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Squad;

import java.util.List;

/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {

	Squad squadInstance;
	public static int ID = 1;
	private int id;
	private long currentTime;
	private boolean result = false;
	private MessageBroker mb;

	public Moneypenny() {
		super("Moneypenny" + ID);
		squadInstance = Squad.getInstance();
		id = ID;
		ID++;
		currentTime = 0;
		mb = MessageBrokerImpl.getInstance();
	}

	public int getId()
	{
		return id;
	}

	@Override
	protected void initialize() {
		ThreadCounter.getInstance().increase();
		mb.register(this);
		subscribeBroadcast(TickBroadcast.class, (broadcast) -> {
			currentTime = broadcast.getCurrentTick();
		});

		if(id%2 != 0){
		subscribeEvent(AgentsAvailableEvent.class, (event) ->{
			List<String> serialAgentsNumber = event.getSerialAgentsNumbers();
			try {
				if(squadInstance.getAgents(serialAgentsNumber)){
					complete(event, true);
				}
				else {
					complete(event, false);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		});
		}
		else{
		subscribeEvent(SendAgentsEvent.class, (event)-> {
			List<String> serialAgentsNumber = event.getSerialAgentsNumbers();
			List<String> names = squadInstance.getAgentsNames(serialAgentsNumber);
			toReturn toReturn = new toReturn(id, names);
			squadInstance.sendAgents(serialAgentsNumber, event.getDuration());
			complete(event, toReturn);
		});
		subscribeEvent(ReleaseAgentsEvent.class, (event) -> {
			List<String> serialAgentsNumber = event.getSerialAgentsNumbers();
			squadInstance.releaseAgents(serialAgentsNumber);
			complete(event, true);
		});
		}

		subscribeBroadcast(ExpirationToOthers.class, (broadcast) -> {
			terminate();
		});

	}
}


