package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import java.util.List;

public class MissionRecivedEvent implements Event {
    private String missionName;
    private List<String> serialAgentsNumbers;
    private String gadget;
    private int timeIssued;
    private int timeExpired;
    private int duration;
    private boolean terminated;

    public MissionRecivedEvent(String missionName, List<String> serialAgentsNumbers, String gadget, int timeIssued, int timeExpired, int duration){
        this.missionName = missionName;
        this.serialAgentsNumbers = serialAgentsNumbers;
        this.gadget = gadget;
        this.timeIssued = timeIssued;
        this.timeExpired = timeExpired;
        this.duration = duration;
        this.terminated = false;
    }

    public String getMissionName(){
        return this.missionName;
    }

    public int getTimeIssued() {return timeIssued;}

    public int getDuration() {return duration;}

    public int getTimeExpired(){
        return this.timeExpired;
    }

    public List<String> getSerialAgentsNumbers()
    {
        return serialAgentsNumbers;
    }

    public boolean isTerminated() { return terminated;}

    public void setTerminated(boolean b)
    {
        terminated = b;
    }

    public String getGadget(){
        return gadget;
    }
}