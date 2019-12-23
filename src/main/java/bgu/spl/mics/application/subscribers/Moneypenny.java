package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
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

	public Moneypenny() {
		super("Moneypenny" + ID);
		squadInstance = Squad.getInstance();
		id = ID;
		ID++;
		currentTime = 0;
	}

	public int getId() // yair added now
	{
		return id;
	}

	@Override
	protected void initialize() {
//		System.out.println("Moneypenny start initialize" + id);

		subscribeBroadcast(TickBroadcast.class, (broadcast) -> {
			//System.out.println("M- ,initialize method, before getting current tick " + currentTime);
			currentTime = broadcast.getCurrentTick();
			//System.out.println("M- ,initialize method, after getting current tick");
		});

		if(id%2 ==0){
		subscribeEvent(AgentsAvailableEvent.class, (event) ->{
			System.out.println("Moneypenny subscribeEvent AgentsAvailableEvent " + id);
			List<String> serialAgentsNumber = event.getSerialAgentsNumbers();
			//boolean result;
			try {
				result = squadInstance.getAgents(serialAgentsNumber);
//				System.out.println("Moneypenny AgentsAvailableEvent lambda in Moneypenny, before complete");
				if(result){
					complete(event, true);
				}
				else {
					complete(event, false);
				}
				System.out.println("Moneypenny AgentsAvailableEvent lambda in Moneypenny, after complete res = " + result);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		});
		}
		else{
		subscribeEvent(ReleaseAgentsEvent.class, (event) -> {
			List<String> serialAgentsNumber = event.getSerialAgentsNumbers();
			squadInstance.releaseAgents(serialAgentsNumber);
			System.out.println("Moneypenny ReleaseAgentsEvent lambda in Moneypenny, after releaseAgents in squad, before complete");
			complete(event, true);
			System.out.println("Moneypenny ReleaseAgentsEvent lambda in Moneypenny, after complete .res= true");
		});

		subscribeEvent(SendAgentsEvent.class, (event)-> {
			List<String> serialAgentsNumber = event.getSerialAgentsNumbers();
//			System.out.println("Moneypenny serialAgentsNumber lambada, before sendAgents in squad " + serialAgentsNumber);
			squadInstance.sendAgents(serialAgentsNumber, event.getDuration()*100);
			System.out.println("Moneypenny serialAgentsNumber lambada, after sendAgents in squad " + serialAgentsNumber + " " + event.getDuration()*100);
			List<String> names = squadInstance.getAgentsNames(serialAgentsNumber);

			toReturn toReturn = new toReturn(id, names);
//			System.out.println("Moneypenny serialAgentsNumber,  SendAgentsEvent lambda ,before complete, return value is " + toReturn );
			complete(event, toReturn);
			System.out.println("Moneypenny serialAgentsNumber,  SendAgentsEvent lambda ,after complete returned value: " + toReturn.getLs());
		});
		}

		subscribeBroadcast(ExpirationBroadcastEvent.class, (broadcast) -> {
			terminate();
		});

//		subscribeBroadcast(ExpirationBroadcastEvent.class, (broadcast) -> {
//			//System.out.println("Moneypenny, before terminate");
//			terminate();
//			//System.out.println("Moneypenny, after terminate");
//		});

		//System.out.println("Moneypenny finished initialize" + id);
	}
}


