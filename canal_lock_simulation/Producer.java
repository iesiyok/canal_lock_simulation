/**
 * @author Ilkan Esiyok
 * @date 2014-05-14
 * @purpose The producer of vessels 
 */

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Producer extends Thread {

	/**
	 * vessel list
	 * @purpose  thread-safe object and
	 * easy to put and get objects
	 */
	public static BlockingQueue<Vessel> vslQueue = new ArrayBlockingQueue<Vessel>(
			Param.SECTIONS);//vessel list

	protected Lock lock;//the lock object

	protected Thread thread2;//seperate thread for sending ready vessels to the lock 

	protected Section[] secList;//section list

	protected java.util.concurrent.locks.Lock pLock = new ReentrantLock();

	public Producer(Lock lock, Section[] sec) {
		this.lock = lock;
		this.secList = sec;

		thread2 = new VesselSender(lock, secList);
		thread2.start();
	}
	/**
	 * runs infinitely to produce new vessels
	 */
	public void run() {
		while (true) {
			try {
				if (Producer.vslQueue.remainingCapacity() > 1) {
					try {
						Vessel vessel = Vessel.getNewVessel();
						vslQueue.add(vessel);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

class VesselSender extends Thread {

	protected volatile Lock lock;
	
	protected java.util.concurrent.locks.Lock sndrLock = new ReentrantLock();

	protected java.util.concurrent.locks.Condition cond;//condition

	protected Section[] secList;//section list

	public VesselSender(Lock lock, Section[] sec) {
		this.lock = lock;
		this.cond = sndrLock.newCondition();
		this.secList = sec;
	}

	public int numOfAvailableSections(Section[] secList) {
		int count = 0;
		for (Section section : secList) {
			if (section.occupied) {
				count++;
			}
		}
		return Param.SECTIONS - count;
	}


	/**
	 * runs infinitely to send new vessels to the lock
	 */
	public void run() {
		while (true) {
			try {
				Param.getLock(sndrLock);
				
				Vessel vessel = Producer.vslQueue.take();
				while (!lock.arrive(vessel, true,secList));
				sndrLock.unlock();
				Thread.sleep(Param.arrivalLapse());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
