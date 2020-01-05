package bgu.spl.mics;

import bgu.spl.mics.application.messages.ExpirationBroadcastEvent;
import bgu.spl.mics.application.messages.MissionRecivedEvent;
import bgu.spl.mics.application.subscribers.M;

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
//		synchronized (topicsOfSubscribers) {
		if(topicsOfSubscribers.containsKey(type)) {

				topicsOfSubscribers.get(type).add(m);
			}
		else{
				ConcurrentLinkedQueue<Subscriber> queue = new ConcurrentLinkedQueue<Subscriber>();
				queue.add(m);
				topicsOfSubscribers.put(type, queue);
			}
//		}
		//synchronized(messagesPerSubscriber)
		//{
			messagesPerSubscriber.putIfAbsent(m, new LinkedBlockingQueue<Message>());
		//}

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		synchronized (topicsOfSubscribers) {
			if (topicsOfSubscribers.containsKey(type)) {
				topicsOfSubscribers.get(type).add(m);
			} else {
				ConcurrentLinkedQueue<Subscriber> queue = new ConcurrentLinkedQueue<Subscriber>();
				queue.add(m);
				topicsOfSubscribers.put(type, queue);
			}
		}
		//synchronized(messagesPerSubscriber) {
			messagesPerSubscriber.putIfAbsent(m, new LinkedBlockingQueue<Message>());
		//}
		}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = futureMap.get(e);
		if(future != null) {
			future.resolve(result);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(b instanceof ExpirationBroadcastEvent){
			for(Subscriber s: messagesPerSubscriber.keySet()){
				if(s instanceof M){
					BlockingQueue<Message> queue = messagesPerSubscriber.get(s);
					for(Message e:queue)
					{
						if(e instanceof MissionRecivedEvent)
						{
							((MissionRecivedEvent) e).setTerminated(true);
						}
					}
				}
			}
		}
		Class broadClassType = b.getClass();
		if(topicsOfSubscribers.containsKey(broadClassType)) {
			ConcurrentLinkedQueue<Subscriber> queue = topicsOfSubscribers.get(broadClassType);
			ConcurrentLinkedQueue<Subscriber> newQueue = new ConcurrentLinkedQueue<Subscriber>();
//			synchronized (queue) {

				for (Subscriber s : queue) {
					//synchronized (newQueue){
						newQueue.add(s);
					//}
					BlockingQueue<Message> queueOfMess = messagesPerSubscriber.get(s);
					if (queueOfMess == null) {
						queueOfMess = new LinkedBlockingQueue<>();
					}
					//synchronized (queueOfMess){
					queueOfMess.add(b);
					//}
					messagesPerSubscriber.put(s, queueOfMess);
				}
				topicsOfSubscribers.put(broadClassType, newQueue);
//			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Class eventClassType = e.getClass();
		Future<T> returnFuture = new Future<T>();
		synchronized (futureMap)
		{
			futureMap.put(e, returnFuture);
		}
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

	@Override
	public void register(Subscriber m) {
		messagesPerSubscriber.putIfAbsent(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	public void unregister(Subscriber m) {
		synchronized (m){
			for(Class<? extends Message> cl :topicsOfSubscribers.keySet()){
				synchronized (topicsOfSubscribers) {
					topicsOfSubscribers.get(cl).remove(m);
				}
			}
			messagesPerSubscriber.remove(m);
		}
	}

	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		synchronized (m){
			if(messagesPerSubscriber.get(m)!=null)
				return messagesPerSubscriber.get(m).take();
			return null;
		}
	}

}