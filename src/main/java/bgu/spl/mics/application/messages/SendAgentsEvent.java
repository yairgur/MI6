package bgu.spl.mics.application.messages;
import java.util.List;
import bgu.spl.mics.Event;

public class SendAgentsEvent implements Event<toReturn>{
    private List<String> serialAgentsNumbersToSend;
    private int duration;

    public SendAgentsEvent(int duration, List<String> serialAgentsNumbersToSend)
    {
        this.duration = duration;
        this.serialAgentsNumbersToSend = serialAgentsNumbersToSend;
    }

    public int getDuration()
    {
        return duration;
    }

    public List<String> getSerialAgentsNumbers()
    {
        return serialAgentsNumbersToSend;
    }

}
