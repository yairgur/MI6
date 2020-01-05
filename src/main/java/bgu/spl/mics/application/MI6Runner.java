package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.*;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) throws FileNotFoundException {
        Vector<Thread> threads = new Vector<>();
        Gson gson = new Gson();
        Inventory inventory = Inventory.getInstance();
        Squad squad = Squad.getInstance();
        AtomicInteger threadCounter = new AtomicInteger(0);
        AtomicInteger numOfM = new AtomicInteger(0);

        try
        {
            JsonReader reader = new JsonReader(new FileReader(args[0]));
            JsonParser jReader = gson.fromJson(reader, JsonParser.class);
            inventory.load(jReader.inventory);
            for(int i=0; i<jReader.services.M; i++)
            {
                threadCounter.incrementAndGet();
                Thread t = new Thread(new M());
                numOfM.incrementAndGet();
                t.setName("M_" + (i+1) );
                threads.add(t);
                t.start();
            }
            for(int i=0; i<jReader.services.Moneypenny; i++)
            {
                threadCounter.incrementAndGet();
                Thread t = new Thread(new Moneypenny());
                t.setName("Monetpenny_" + (i+1) );
                threads.add(t);
                t.start();
            }

            for(int i=0; i<jReader.services.intelligence.length; i++)
            {
                threadCounter.incrementAndGet();
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
                    MissionInfo mission = new MissionInfo(jReader.services.intelligence[i].missions[j].name, ls, jReader.services.intelligence[i].missions[j].gadget, jReader.services.intelligence[i].missions[j].timeIssued, jReader.services.intelligence[i].missions[j].timeExpired, jReader.services.intelligence[i].missions[j].duration);
                    instance.addMission(mission);

                }
                Thread t = new Thread(instance);
                t.setName("intelligence" + (i+1) );
                threads.add(t);
                t.start();
            }


            squad.load(jReader.squad);

            Q q = Q.getInstance();
            threadCounter.incrementAndGet();
            Thread t1 = new Thread(q);
            t1.setName("Q_1");
            threads.add(t1);
            t1.start();

            Thread t2 = new Thread(new Killer(numOfM.intValue()));
            t2.setName("Killer_1");
            threads.add(t2);
            t2.start();

            while(ThreadCounter.getInstance().getCounter().intValue() < threadCounter.intValue())
            {}



            TimeService time = new TimeService(jReader.services.time);
            Thread t = new Thread(time);
            t.setName("TimeService");
            threads.add(t);
            t.start();

            try{
                for(Thread thread: threads){
                    thread.join();
                }
            }catch (InterruptedException e){
                Thread.currentThread().isInterrupted();
            }
        }
        catch (Exception e){}

        Diary diary = Diary.getInstance();
        diary.printToFile(args[2]);
        Inventory inventory2 = Inventory.getInstance();
        inventory2.printToFile(args[1]);
    }
}
