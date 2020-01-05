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
		mb.register(this);
		Diary diary = Diary.getInstance();
		subscribeBroadcast(TickBroadcast.class, (broadcast) -> {
			currentTime = broadcast.getCurrentTick();
		});

		subscribeEvent(MissionRecivedEvent.class, (event) ->{
			diary.incrementTotal();
			if(!event.isTerminated()){
			mInfo = new MissionInfo(event.getMissionName(), event.getSerialAgentsNumbers(), event.getGadget(), event.getTimeIssued(), event.getTimeExpired(), event.getDuration());
			List<String> SerialAgentsNumbers = event.getSerialAgentsNumbers();
			AgentsAvailableEvent availableEvent = new AgentsAvailableEvent(SerialAgentsNumbers);
			Future<Boolean> future1 = getSimplePublisher().sendEvent(availableEvent);

			if(future1 != null && future1.get() == true){
				GadgetAvailableEvent gadgetEvent = new GadgetAvailableEvent(event.getGadget());
				Future<Integer> future2 = getSimplePublisher().sendEvent(gadgetEvent);

				if(future2.get() != null && event.getTimeExpired() >= currentTime){ // there is a gadget and TimeExpired bigger then currentTime. continue.
						SendAgentsEvent sendAgentsEvent = new SendAgentsEvent(mInfo.getDuration(), SerialAgentsNumbers);
						Future<toReturn> futureSendAgents = getSimplePublisher().sendEvent(sendAgentsEvent);
						if(futureSendAgents.get()!=null){
							Report rep = new Report(mInfo.getMissionName(), id, futureSendAgents.get().getId(), mInfo.getSerialAgentsNumbers(), futureSendAgents.get().getLs(), mInfo.getGadget(), mInfo.getTimeIssued(), future2.get().intValue(), (int)currentTime);
							diary.addReport(rep);
						}

				}
				else{ // there is no gadget available
					ReleaseAgentsEvent ReleaseAgentsEvent = new ReleaseAgentsEvent(SerialAgentsNumbers);
					getSimplePublisher().sendEvent(ReleaseAgentsEvent);
				}

			}
			else
			{

			}

		}
			else{
				complete(event,null);
			}
		})  ;
		subscribeBroadcast(ExpirationBroadcastEvent.class, (broadcast) -> {
			terminate();
			getSimplePublisher().sendEvent(new MTerminateEvent());
			///send event Mterminated; to killer
		});

	}
}
