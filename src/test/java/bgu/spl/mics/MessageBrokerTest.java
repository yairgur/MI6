package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.*;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.fail;


public class MessageBrokerTest {


    private class BroadcastTestMessage implements Broadcast {
        public int number;
        public BroadcastTestMessage(int number) {
            this.number = number;
        }
    }
    private class EventTestMessage implements Event<Integer> {
        public int number;
        public EventTestMessage(int number) {
            this.number = number;
        }
    }

    Subscriber subFirst;
    Subscriber subSecond;
    MessageBroker broker;

    Integer lastReceivedNumber;


    @BeforeEach
    public void setUp(){
        broker = MessageBrokerImpl.getInstance();
        subFirst = new Subscriber("SUB1") {
            @Override
            protected void initialize() {
                subscribeBroadcast(BroadcastTestMessage.class, br -> {
                    lastReceivedNumber = br.number;
                    terminate();
                });
            }
        };
        subSecond = new Subscriber("SUB2") {
            @Override
            protected void initialize() {
                subscribeEvent(EventTestMessage.class, m2 -> {
                    complete(m2, m2.number * 2);
                    terminate();
                });
            }
        };

        lastReceivedNumber = null;
    }

    @Test
    public void singletonExistenceTest(){

        MessageBroker mb = MessageBrokerImpl.getInstance();

        assert mb != null;
        mb.sendBroadcast(new BroadcastTestMessage(3));

        assertEquals(3, lastReceivedNumber.intValue());
    }

    @Test
    public void simpleBroadcastTest(){

        MessageBroker broker = MessageBrokerImpl.getInstance();

        assert broker != null;
        broker.sendBroadcast(new BroadcastTestMessage(3));

        assertEquals(3, lastReceivedNumber.intValue());
    }

    @Test
    public void simpleEventTest(){

        MessageBroker broker = MessageBrokerImpl.getInstance();

        assert broker != null;
        Future<Integer> newNum = broker.sendEvent(new EventTestMessage(3));

        assertEquals(6, newNum.get().intValue());
    }

    @AfterEach
    public void unregisterAll() {
        MessageBroker mb = MessageBrokerImpl.getInstance();
        if (mb != null) {
            mb.unregister(subFirst);
        }
    }
}
