package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class GadgetAvailableEvent implements Event<Integer> {
    private String gadget;

    public GadgetAvailableEvent(String name){
        this.gadget = name;
    }

    public String getGadget(){
        return this.gadget;
    }
}