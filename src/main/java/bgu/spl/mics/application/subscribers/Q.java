package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.ExpirationBroadcastEvent;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {
	private Inventory inventoryInstance;
	public static int ID = 1;
	private int id;
	private int currentTime;

	private static class SingletonHolder {
		private static Q instance = new Q();
	}

	public static Q getInstance()
	{
		return Q.SingletonHolder.instance;

	}

	public Q() {
		super("Q" + ID);
		this.inventoryInstance = Inventory.getInstance();
		id = ID;
		ID++;
		currentTime = 0;
	}

	@Override
	protected void initialize() {
		//System.out.println("Q start initialize" + id);

		subscribeBroadcast(TickBroadcast.class, (broadcast) -> {
			currentTime = (int)broadcast.getCurrentTick();
		});

		subscribeEvent(GadgetAvailableEvent.class, (event)-> {
			String name = event.getName();
			boolean result = inventoryInstance.getItem(name);
			if(result){
				complete(event, currentTime);
			}
			else{
				complete(event, null);
			}
		});
		//System.out.println("Q finish initialize" + id);

		subscribeBroadcast(ExpirationBroadcastEvent.class, (broadcast) -> {
			terminate();
		});
	}
}