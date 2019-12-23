package bgu.spl.mics.application.messages;

import java.util.List;
import bgu.spl.mics.Event;

public class ReleaseAgentsEvent implements Event<Boolean> {

    private List<String> serialAgentsNumbers;

    public ReleaseAgentsEvent(List<String> serialAgentsNumber)
    {
        this.serialAgentsNumbers = serialAgentsNumber;
    }

    public List<String> getSerialAgentsNumbers()
    {
        return serialAgentsNumbers;
    }

}
