package bgu.spl.mics;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 *
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	private T result;
	private boolean isResolved;
	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		isResolved = false;
		result = null;
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved.
	 * This is a blocking method! It waits for the computation in case it has
	 * not been completed.
	 * <p>
	 * @return return the result of type T if it is available, if not wait until it is available.
	 *
	 */
	public T get() {
		synchronized (this){
			while(!isDone()){
				try{
					//System.out.println("Future- ,get method, before wait " + Thread.currentThread().getId());
					this.wait();
					System.out.println("Future- ,get method,  after wait " + Thread.currentThread().getId());
				} catch(InterruptedException e) {
					//Thread.currentThread().interrupt();
				}
			}
			System.out.println("Future- ,get method, before while " + Thread.currentThread().getId());
		}
		System.out.println("result in get  " + result);
		return result;
	}

	/**
	 * Resolves the result of this Future object.
	 */
	public synchronized void resolve (T result) {
			this.result = result;
			this.isResolved = true;
			System.out.println("Future , resolve, before notify all");
			notifyAll();
			System.out.println("Future , resolve, after notify all");
	}

	/**
	 * @return true if this object has been resolved, false otherwise
	 */
	public boolean isDone() {
		return isResolved;
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved,
	 * This method is non-blocking, it has a limited amount of time determined
	 * by {@code timeout}
	 * <p>
	 * @param timeout 	the maximal amount of time units to wait for the result.
	 * @param unit		the {@link TimeUnit} time units to wait.
	 * @return return the result of type T if it is available, if not,
	 * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
	 *         elapsed, return null.
	 */
	public T get(long timeout, TimeUnit unit) {
		synchronized (this) {
			while (!isDone()){
				try {
					this.wait(timeout);
				} catch (InterruptedException e) {}
			}
			return result;
		}
	}
}