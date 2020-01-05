package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Agent;

public class JsonParser {
    public String[] inventory;
    public jServices services;
    public Agent[] squad;


    public class jServices
    {
        public int M;
        public int Moneypenny;
        jMissions[] intelligence;
        public int time;



        public class jMissions{
            jMission[] missions;

            public class jMission{
                public String [] serialAgentsNumbers;
                public int duration;
                public String gadget;
                public String name;
                public int timeExpired;
                public int timeIssued;
            }
        }

    }


}
