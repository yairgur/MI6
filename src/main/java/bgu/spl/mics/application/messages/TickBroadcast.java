package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private long tick;

    public TickBroadcast(long currTick)
    {
        this.tick = currTick;
    }

    public long getCurrentTick(){
        return tick;
    }
}