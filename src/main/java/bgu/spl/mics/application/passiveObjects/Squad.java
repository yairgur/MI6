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
			Agent agent = new Agent(agents[i].getSerialNumber(), agents[i].getName());
			this.agents.put(agents[i].getSerialNumber(), agent);
		}
	}

	/**
	 * Releases agents.
	 */
	public void releaseAgents(List<String> serials){
		for(String s: serials)
		{
			agents.get(s).release();
		}
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   time ticks to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		// first - sleep for the squad when they go to the mission
		try {
			Thread.currentThread().sleep(time*100);
		}
		catch(InterruptedException e) {}
		for(String serial:serials) {
			agents.get(serial).release();
		}
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials) throws InterruptedException {
		serials.sort(String::compareTo);
			for (String serial : serials) {
				Agent agent = getInstance().agents.get(serial);
				if (agent == null) {
					return false;
				}
			}
		for(String serial: serials)
			agents.get(serial).acquire();
			return true;
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
				retList.add(agents.get(s).getName());
		}
		return retList;
	}
}

