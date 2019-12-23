package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import bgu.spl.mics.application.passiveObjects.Squad;
import static org.junit.jupiter.api.Assertions.*;
import bgu.spl.mics.application.passiveObjects.Agent;
import static org.junit.jupiter.api.Assertions.fail;

public class SquadTest {
    Squad Squad1;
    @BeforeEach
    public void setUp(){
        Squad1 = new Squad();
    }

    @Test
    public void testGetAgents(){
        Agent a1 = new Agent("001" ,"ido");
        Agent a2 = new Agent("011" ,"Yair");
        Agent a3 = new Agent("010" ,"dani");
        List<String> ls = Arrays.asList(a1.getName(), a2.getName(), a3.getName());
        Agent[] arr = {a1, a2, a3};
        Squad1.load(arr);
        //assertTrue(Squad1.getAgents(ls));
    }

    @Test
    public void testReleaseAgents(){
        Agent a1 = new Agent("001" ,"ido");
        Agent a2 = new Agent("011" ,"Yair");
        Agent a3 = new Agent("010" ,"dani");
        List<String> ls = Arrays.asList(a1.getName(), a2.getName());
        Agent[] arr = {a1, a2, a3};
        Squad1.load(arr);
        Squad1.releaseAgents(ls);
        //assertFalse(Squad1.getAgents(Arrays.asList(a1.getName())));
        //assertTrue(Squad1.getAgents(Arrays.asList(a3.getName())));
    }


    @Test
    public void testGetAgentsNames(){
        Agent a1 = new Agent("001" ,"ido");
        Agent a2 = new Agent("011" ,"Yair");
        Agent a3 = new Agent("010" ,"dani");
        List<String> ls = Arrays.asList(a1.getName(), a2.getName());
        Agent[] arr = {a1, a2, a3};
        Squad1.load(arr);
        assertEquals(Squad1.getAgentsNames(Arrays.asList(a1.getSerialNumber())), Arrays.asList(a1.getName()));
    }

    @Test
    public void testSendAgents(){
        Agent a1 = new Agent("001" ,"ido");
        Agent a2 = new Agent("011" ,"Yair");
        Agent a3 = new Agent("010" ,"dani");
        List<String> ls = Arrays.asList(a1.getName(), a2.getName());
        Agent[] arr = {a1, a2, a3};
//
    }

}