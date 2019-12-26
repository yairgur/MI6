package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.ThreadCounter;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.List;
import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeUnit;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {
	public static int ID = 1;
	private int id;
	private MessageBroker mb;
	private MissionInfo mInfo;
	private long currentTime;
	
	public M() {
		super("M" + ID);
		id = ID;
		ID++;
		mb = MessageBrokerImpl.getInstance();
		currentTime = 0;
	}

	@Override
	protected void initialize()
	{
		ThreadCounter.getInstance().increase();
		mb.register(this); // is it needed?
		System.out.println("M is init " + id);
		Diary diary = Diary.getInstance();
		subscribeBroadcast(TickBroadcast.class, (broadcast) -> {
			//System.out.println("M- " + id + " ,initialize method, before getting current tick " + currentTime);
			currentTime = broadcast.getCurrentTick();
		});

		subscribeEvent(MissionRecivedEvent.class, (event) ->{
			diary.incrementTotal();
			mInfo = new MissionInfo(event.getMissionName(), event.getSerialAgentsNumbers(), event.getGadget(), event.getTimeIssued(), event.getTimeExpired(), event.getDuration());
			System.out.println("M-" + id + " I got the mission " + mInfo.getMissionName() + " with agents " + mInfo.getSerialAgentsNumbers() + " issued in " + mInfo.getTimeIssued() + " in system time: " + currentTime);
			List<String> SerialAgentsNumbers = event.getSerialAgentsNumbers();

			AgentsAvailableEvent availableEvent = new AgentsAvailableEvent(SerialAgentsNumbers);
			Future<Boolean> future1 = getSimplePublisher().sendEvent(availableEvent);

			if(future1 != null && future1.get(event.getTimeExpired()-currentTime, TimeUnit.MILLISECONDS) == true){
				System.out.println("M-" + id + " There are available Agents, they are: " + availableEvent.getSerialAgentsNumbers() + "in the time " + currentTime);
				GadgetAvailableEvent gadgetEvent = new GadgetAvailableEvent(event.getGadget());
				System.out.println("M-" + id + " sent event for finding gadget: " + event.getGadget());
				Future<Integer> future2 = getSimplePublisher().sendEvent(gadgetEvent);

				if(future2.get() != null && event.getTimeExpired() >= currentTime){ // there is a gadget and TimeExpired bigger then currentTime. continue.

					System.out.println("M- " + id + " There is a gadget! yay!. gadget is: " + event.getGadget() +" mission name: "+ event.getMissionName() + "serial numbers= " + event.getSerialAgentsNumbers() + " time issued is " + mInfo.getTimeIssued() + " and current time is: " + currentTime);
					if(event.getTimeExpired() >= currentTime + event.getDuration()){
						SendAgentsEvent sendAgentsEvent = new SendAgentsEvent(mInfo.getDuration(), SerialAgentsNumbers);
						Future<toReturn> futureSendAgents = getSimplePublisher().sendEvent(sendAgentsEvent);
						if(futureSendAgents.get()!=null){
							System.out.println("M-" + id + " just sent Agents " + event.getSerialAgentsNumbers() + " and now they are not available");

							Report rep = new Report(mInfo.getMissionName(), id, futureSendAgents.get().getId(), mInfo.getSerialAgentsNumbers(), futureSendAgents.get().getLs(), mInfo.getGadget(), mInfo.getTimeIssued(), future2.get().intValue(), (int)currentTime);
							diary.addReport(rep);
							System.out.println(rep.toString());

						}

					}
				}
				else{ // there is no gadget available
					System.out.println("M-" + id + " There is no gadget. gadget: " + event.getGadget() + "is not in the list. Mission name: " + event.getMissionName() + " serial numbers= " + event.getSerialAgentsNumbers());
					ReleaseAgentsEvent ReleaseAgentsEvent = new ReleaseAgentsEvent(SerialAgentsNumbers);
					getSimplePublisher().sendEvent(ReleaseAgentsEvent);
				}
			}
			else
			{

			}

		})  ;
		subscribeBroadcast(ExpirationBroadcastEvent.class, (broadcast) -> {
			terminate();
			System.out.println("M-" + id + " has terminated");
		});

	}
}
