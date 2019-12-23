package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Squad;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
//import sun.jvm.hotspot.tools.SysPropsDumper;
//import sun.jvm.hotspot.tools.SysPropsDumper;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) throws FileNotFoundException {
        Vector<Thread> threads = new Vector<>();
        Gson gson = new Gson();
        Inventory inventory = Inventory.getInstance();
        Squad squad = Squad.getInstance(); //the getInstance of squad and inventory is different.


        try
        {
            JsonReader reader = new JsonReader(new FileReader(args[0]));
            JsonParser jReader = gson.fromJson(reader, JsonParser.class);
            inventory.load(jReader.inventory);
            for(int i=0; i<jReader.services.M; i++)
            {
                Thread t = new Thread(new M());
                threads.add(t);
                t.start();
            }
            for(int i=0; i<jReader.services.Moneypenny; i++)
            {
                Thread t = new Thread(new Moneypenny());
                threads.add(t);
                t.start();
            }

            for(int i=0; i<jReader.services.intelligence.length; i++)
            {
                String [] o;
                Intelligence instance = new Intelligence();
                for(int j=0; j<jReader.services.intelligence[i].missions.length; j++)
                {
                    o = jReader.services.intelligence[i].missions[j].serialAgentsNumbers;
                    List<String> ls = new LinkedList<>();
                    for(int k=0; k<o.length; k++)
                    {
                        ls.add(o[k]);
                    }
//                    Intelligence instance = new Intelligence();
                    MissionInfo mission = new MissionInfo(jReader.services.intelligence[i].missions[j].missionName, ls, jReader.services.intelligence[i].missions[j].gadget, jReader.services.intelligence[i].missions[j].timeIssued, jReader.services.intelligence[i].missions[j].timeExpired, jReader.services.intelligence[i].missions[j].duration);

                    instance.addMission(mission);
                    Thread t = new Thread(instance);
                    threads.add(t);
                    t.start();
                }
            }
            TimeService time = new TimeService(jReader.services.time);
            Thread t = new Thread(time);
            threads.add(t);
            t.start();


            squad.load(jReader.squad);
            Q q = Q.getInstance();
            Thread t1 = new Thread(q);
            threads.add(t1);
            t1.start();

            try{
                for(Thread thread: threads){
                    thread.join();
                }
            }catch (InterruptedException e){
                Thread.currentThread().isInterrupted();
            }
        }
        catch (Exception e){}


        //List<String> ls = inventory.getGadgets();
        System.out.println("Yair is the king");

//        for(int i = 0; i<inventory.getGadgets().size(); i++)
//        {
//            System.out.println(inventory.getGadgets().get(i));
//        }
//        for(int i = 0; i<squad.getAgents().size(); i++)
//        {
//           // System.out.println(squad.getAgents().keySet().toArray()[i] + " " + squad);
//        }

    }
}
