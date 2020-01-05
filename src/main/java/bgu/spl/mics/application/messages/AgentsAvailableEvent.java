package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import java.util.List;

public class AgentsAvailableEvent implements Event<Boolean> {
    private List<String> serialAgentsNumbers;

    public AgentsAvailableEvent(List<String> serialAgentsNumber)
    {
        this.serialAgentsNumbers = serialAgentsNumber;
    }

    public List<String> getSerialAgentsNumbers(){
        return serialAgentsNumbers;
    }



}
