package bgu.spl.mics.application.messages;

import java.util.List;

public class toReturn{
    private int id;
    private List<String> ls;
    public toReturn(int id, List<String> ls)
    {
        this.id= id;
        this.ls = ls;
    }
    public List<String> getLs()
    {
        return ls;
    }
    public int getId()
    {
        return id;
    }

}
