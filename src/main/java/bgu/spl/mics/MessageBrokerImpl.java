package bgu.spl.mics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {

	private static MessageBrokerImpl instance;

	private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<Subscriber>> topicsOfSubscribers;
	private ConcurrentHashMap<Subscriber, BlockingQueue<Message>> messagesPerSubscriber;
	private ConcurrentHashMap<Event, Future> futureMap;

	/**
	 * Retrieves the single instance of this class.
	 */



	private static class SingletonHolder {
		private static MessageBrokerImpl instance = new MessageBrokerImpl();
	}

	public static MessageBroker getInstance() {
		return SingletonHolder.instance;
	}

	public MessageBrokerImpl(){
		this.topicsOfSubscribers = new ConcurrentHashMap<>();
		this.messagesPerSubscriber = new ConcurrentHashMap<>();
		this.futureMap = new ConcurrentHashMap();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
		// use Map 1,
		if(topicsOfSubscribers.containsKey(type)){
			topicsOfSubscribers.get(type).add(m);
		}
		else {
			ConcurrentLinkedQueue<Subscriber> queue = new ConcurrentLinkedQueue<Subscriber>();
			queue.add(m);
			topicsOfSubscribers.put(type, queue);
		}
		synchronized(messagesPerSubscriber)
		{
			messagesPerSubscriber.putIfAbsent(m, new LinkedBlockingQueue<Message>());
		}

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		synchronized (this) {
			if (topicsOfSubscribers.containsKey(type)) {
				topicsOfSubscribers.get(type).add(m);
			} else {
				ConcurrentLinkedQueue<Subscriber> queue = new ConcurrentLinkedQueue<Subscriber>();
				queue.add(m);
				topicsOfSubscribers.put(type, queue);
			}
		}
			messagesPerSubscriber.putIfAbsent(m, new LinkedBlockingQueue<Message>());

		}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// todo: generics

		System.out.println("MSGI complete called , before null check");
		Future<T> future = futureMap.get(e);
		if(future != null)
			System.out.println("MSGI complete called, after null");
			future.resolve(result);

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		Class broadClassType = b.getClass();
		if(topicsOfSubscribers.containsKey(broadClassType)) {
			ConcurrentLinkedQueue<Subscriber> queue = topicsOfSubscribers.get(broadClassType); // take first subscriber
			ConcurrentLinkedQueue<Subscriber> newQueue = new ConcurrentLinkedQueue<Subscriber>();
			synchronized (queue) {

				for (Subscriber s : queue) {
					synchronized (newQueue){newQueue.add(s);}
//				ConcurrentLinkedQueue<Message> queueOfMess = new ConcurrentLinkedQueue<>();
					BlockingQueue<Message> queueOfMess = messagesPerSubscriber.get(s);
					if (queueOfMess == null) {
						queueOfMess = new LinkedBlockingQueue<>();
					}
					queueOfMess.add(b);
					messagesPerSubscriber.put(s, queueOfMess);
				}
				topicsOfSubscribers.put(broadClassType, newQueue);
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Class eventClassType = e.getClass();

		Future<T> returnFuture = new Future<T>(); //we just create future, without modifying it
		// we should return null if there is no suitable subscriber
		synchronized (futureMap)
		{
			futureMap.put(e, returnFuture);
		}

		synchronized (topicsOfSubscribers) {
			synchronized(messagesPerSubscriber){
			if (topicsOfSubscribers.containsKey(eventClassType)) {
				ConcurrentLinkedQueue<Subscriber> queueOfSubscribers = topicsOfSubscribers.get(eventClassType);
				BlockingQueue<Message> queueOfMess = new LinkedBlockingQueue<>();
				if (!queueOfSubscribers.isEmpty()) {
					Subscriber s1 = queueOfSubscribers.poll();
					queueOfSubscribers.add(s1);

					if (messagesPerSubscriber.get(s1) != null) {
						queueOfMess = messagesPerSubscriber.get(s1);
					}
					queueOfMess.add(e);
					messagesPerSubscriber.put(s1, queueOfMess);
				}
			} else {
				return null;
			}
			return returnFuture;
		}
		}
	}

	@Override
	public void register(Subscriber m) {
		// Map 2
		messagesPerSubscriber.putIfAbsent(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	public void unregister(Subscriber m) {
		synchronized (m){
			for(Class<? extends Message> cl :topicsOfSubscribers.keySet()){
				topicsOfSubscribers.get(cl).remove(m);
			}

			while(!messagesPerSubscriber.get(m).isEmpty())
			{

			}
			messagesPerSubscriber.remove(m);

		}
	}

	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		synchronized (m){
			return messagesPerSubscriber.get(m).take();
		}
	}

}