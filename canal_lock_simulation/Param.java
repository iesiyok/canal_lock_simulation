/**
 * present class for parameters
 */

import java.util.Random;

/**
 * Parameters that influence the behaviour of the system.
 */

public class Param {

  //the number of canal sections
  public final static int SECTIONS = 16;

  //the time interval at which Main checks threads are alive
  public final static int MAIN_INTERVAL = 50;

  //the time it takes to operate the lock
  public final static int OPERATE_TIME = 800;

  //the time it takes to tow
  public final static int TOWING_TIME = 1200;

  //the maximum amount of time between vessel arrivals
  public final static int MAX_ARRIVE_INTERVAL = 2400;

  //the maximum amount of time between vessel departures
  public final static int MAX_DEPART_INTERVAL = 800;

  //the maximum amount of time between operating the lock
  public final static int MAX_OPERATE_INTERVAL = 6400;

/**
 * For simplicity, we assume uniformly distributed time lapses.
 * An exponential distribution might be a fairer assumption.
 */

  public static int arrivalLapse() {
    Random random = new Random();
    return random.nextInt(MAX_ARRIVE_INTERVAL);
  }

  public static int departureLapse() {
    Random random = new Random();
    return random.nextInt(MAX_DEPART_INTERVAL);
  }

  public static int operateLapse() {
    Random random = new Random();
    return random.nextInt(MAX_OPERATE_INTERVAL);
  }
	/**
	 * acquires lock for individual object and avoids deadlocks 
	 */
	public static void getLock(java.util.concurrent.locks.Lock firstLock)
			throws InterruptedException {
		while (true) {

			boolean suc_FirstLock = false;
			try {
				suc_FirstLock = firstLock.tryLock();
			} finally {
				if (suc_FirstLock) {
					return;
				} 

			}
			Thread.sleep(1);
		}
	}
	/**
	 * acquires lock for two objects and avoids deadlocks 
	 */
	 public static void getLocks(java.util.concurrent.locks.Lock firstLock,
				java.util.concurrent.locks.Lock secondLock)
				throws InterruptedException {
			while (true) {

				boolean suc_FirstLock = false;
				boolean suc_SecondLock = false;
				try {
					suc_FirstLock = firstLock.tryLock();
					suc_SecondLock = secondLock.tryLock();
				} finally {
					if (suc_FirstLock && suc_SecondLock) {
						return;
					} else {
						if (suc_FirstLock)
							firstLock.unlock();
						if (suc_SecondLock)
							secondLock.unlock();
					}

				}
				Thread.sleep(1);
			}
		}
  
}
