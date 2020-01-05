package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.ExpirationToOthers;
import bgu.spl.mics.application.messages.MTerminateEvent;


public class Killer extends Subscriber {
    private int numOfM;
    private int numOfMterminated;

    public Killer(int numOfM){
        super("Killer");
        this.numOfM = numOfM;
        this.numOfMterminated=0;
    }

    @Override
    protected void initialize() {
        subscribeEvent(MTerminateEvent.class, (event) ->{
            numOfMterminated = numOfMterminated + 1;
            if(numOfM == numOfMterminated){
                getSimplePublisher().sendBroadcast(new ExpirationToOthers());
                terminate();
                }
        });


    }

}
