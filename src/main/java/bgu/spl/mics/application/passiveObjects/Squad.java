package bgu.spl.mics.application.passiveObjects;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	private Map<String, Agent> agents = new HashMap<>();

//	public Map<String, Agent> getAgents()
//	{
//		return agents;
//	}

	/**
	 * Retrieves the single instance of this class.
	 */

	private static class SingletonHolder {
		private static Squad instance = new Squad();
	}

	public static Squad getInstance() {
		return Squad.SingletonHolder.instance;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] agents) {
		for(int i=0; i<agents.length; i++)
		{
			this.agents.put(agents[i].getSerialNumber(), agents[i]);
		}
	}

	/**
	 * Releases agents.
	 */
	public void releaseAgents(List<String> serials){
		synchronized (getInstance().agents) {
			for (String s : serials) {
				agents.get(s).release(); // release agent with the serial s;
			}
			System.out.println("Squad, releaseAgents, before notifyall");
			/*this*/
			getInstance().agents.notifyAll();
			System.out.println("Squad, releaseAgents, after notifyall");
		}

	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   time ticks to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		// first - sleep for the squad when they go to the mission
		try {
			System.out.println("Squad, sendAgents, before sleep " + time);
			Thread.sleep(time);
			System.out.println("Squad, sendAgents, after sleep ");
		}
		catch(InterruptedException e) {}

		for(String serial:serials) {
			synchronized (getInstance().agents) {
				agents.get(serial).release();
				System.out.println("Squad, sendAgents, before notifyall");
				/*this*/
				getInstance().agents.notifyAll();
				System.out.println("Squad, sendAgents, after notifyall");
			}
		}
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */

	public boolean getAgents(List<String> serials) throws InterruptedException {

		serials.sort(String::compareTo);
		synchronized (getInstance().agents) { // check if here or later
			for (String serial : serials) {
				System.out.println("Squad - int getAgents method, serial number is: " + serial);
				Agent agent = getInstance().agents.get(serial);
				System.out.println("Squad - getAgents - agent: " + agent.getName() + "is available? " + agent.isAvailable());
				if (/*!agents.containsKey(serial)*/agent == null) {
					System.out.println("we do not contain serial: " + serial);
					return false;
				}
			}

			for (String serial : serials) {
				Agent agent = getInstance().agents.get(serial);
					while (agent.isAvailable()) {
					try {
						System.out.println("We are waiting in squad getAgents");
//						Thread.currentThread().sleep(100);
						 getInstance().agents.wait();
					} catch (InterruptedException e) {}
				}
					agent.acquire();
				//agents.get(serial).acquire();
				System.out.println("Agent " + agents.get(serial) + " is now acquired");
			}
			return true;
		}
	}

	/**
	 * gets the agents names
	 * @param serials the serial numbers of the agents
	 * @return a list of the names of the agents with the specified serials.
	 */
	public List<String> getAgentsNames(List<String> serials){
		List<String> retList = new LinkedList<>();
		for(String s:serials)
		{
			if(agents.get(s) != null)
				retList.add(agents.get(s).getName()); // adds the name of all agents to the list we will return
		}
		return retList;
	}

}
