package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Squad;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadCounter {
    private AtomicInteger counter;

    public ThreadCounter()
    {
        counter = new AtomicInteger(0);
    }

    public void increase()
    {
        counter.incrementAndGet();
    }

    public AtomicInteger getCounter()
    {
        return counter;
    }

    private static class SingletonHolder {
        private static ThreadCounter instance = new ThreadCounter();
    }

    public static ThreadCounter getInstance() {
        return ThreadCounter.SingletonHolder.instance;
    }
}
