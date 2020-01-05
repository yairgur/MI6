package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Agent {
	private String name;
	private String serialNumber;
	private boolean available;
	private Semaphore s=new Semaphore(1,true);

	public Agent(String serialNumber, String name){
		this.serialNumber = serialNumber;
		this.name = name;
		available = true;
	}

	/**
	 * Sets the serial number of an agent.
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * Retrieves the serial number of an agent.
	 * <p>
	 * @return The serial number of an agent.
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * Sets the name of the agent.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves the name of the agent.
	 * <p>
	 * @return the name of the agent.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retrieves if the agent is available.
	 * <p>
	 * @return if the agent is available.
	 */
	public boolean isAvailable() {
		return available;
	}

	/**
	 * Acquires an agent.
	 */
	public void acquire(){
		try {
			s.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(available == true){
			available = false;
		}
		else{

		}
	}

	/**
	 * Releases an agent.
	 */
	public void release(){
		s.release();
		available = true;
	}
}
