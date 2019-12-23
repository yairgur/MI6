package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Agent;

import javax.sound.midi.Soundbank;
import java.sql.SQLOutput;

public class JsonParser {
    public String[] inventory;
    public jServices services;
    //public jSquad squad;
    public Agent[] squad;
//    public class jSquad
//    {
//        private String name;
//        private String serialNumber;
//    }


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
                //public String [] missions;
                public int duration;
                public String gadget;
                public String missionName;
                public int timeExpired;
                public int timeIssued;
            }
        }

    }


}
