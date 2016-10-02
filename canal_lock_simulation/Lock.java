/**
 * @author Ilkan Esiyok
 * @date 2014-05-14
 * @purpose The operations on the lock 
 */

import java.util.concurrent.locks.ReentrantLock;

public class Lock {

	protected volatile boolean occupied = false;// lock_status

	protected volatile boolean drained = true;// chamber water level

	protected volatile Vessel vessel = null;

	protected volatile boolean upStream = true;//the direction of vessel

	private volatile java.util.concurrent.locks.Lock arLock = new ReentrantLock();
	private volatile java.util.concurrent.locks.Lock lvLock = new ReentrantLock();
	private volatile java.util.concurrent.locks.Lock dwnLock = new ReentrantLock();
	private volatile java.util.concurrent.locks.Lock dprtLock = new ReentrantLock();


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
	 * the method for arriving vessels, acquires the access of the lock, checks
	 * the status of it, if the lock is free and drained, permits to the
	 * Launch_tug to transport vessel to inside of the lock
	 */
	public boolean arrive(Vessel vessel, boolean upStream, Section[] secList)
			throws InterruptedException {
		if(!((secList[Param.SECTIONS-1].occupied)&&(secList[0].occupied)&&numOfAvailableSections(secList) <= 1)){
			if (!this.occupied && this.drained && upStream && vessel != null) {
				Param.getLock(arLock);
				System.out.println(vessel.toString() + " enters lock to go up");
				this.occupied = true;
				this.drained = false;
				this.vessel = vessel;
				this.upStream = true;
				Thread.sleep(Param.TOWING_TIME);
				arLock.unlock();
				return true;
			} else {
				return false;
			}
		}else{
			return false;
		}
	}

	/**
	 * the method for vessels going upstream, acquires the access of the lock,
	 * checks the status of it, if the lock is occupied, chamber is filled, and
	 * the direction is upstream permits to the Launch_tug to transport vessel
	 * to outside of the lock
	 */
	public void leaveLock(Vessel vessel, boolean upStream)
			throws InterruptedException {
		try {
			if (this.occupied && !this.drained && vessel != null && upStream) {
				Param.getLock(lvLock);
				System.out.println(vessel.toString() + " leaves the lock");
				this.occupied = false;
				this.drained = false;
				this.vessel = null;
				this.upStream = true;
				lvLock.unlock();
				Thread.sleep(Param.TOWING_TIME);
			}
		} finally {
		}
	}

	/**
	 * the method for vessels going downstream, acquires the access of the lock,
	 * checks the status of it, if the lock is not occupied, chamber is filled,
	 * and the direction is downstream permits to the Return_tug to transport
	 * vessel from the last section to inside of the lock
	 */
	public void carryOutVessel(Vessel v, boolean upStream)
			throws InterruptedException {
		try {
			Param.getLock(dwnLock);
			if (!this.occupied && !this.drained && v != null && !upStream) {

				System.out.println(v.toString() + " enters lock to go down");
				this.occupied = true;
				this.drained = false;
				this.vessel = v;
				this.upStream = false;
				Thread.sleep(Param.TOWING_TIME);
				dwnLock.unlock();
			}
		} finally {
		}
	}

	/**
	 * the method for vessels going downstream, acquires the access of the lock,
	 * checks the status of it, if the lock is occupied, chamber is filled, and
	 * the direction is downstream permits to the Consumer to transport vessel
	 * from the lock
	 */
	public void depart(Vessel v, boolean upStream) throws InterruptedException {
		try {
			if (this.occupied && !this.drained && v != null && !upStream) {
				Param.getLock(dprtLock);
				System.out.println(v.toString() + " departs");
				this.occupied = false;
				this.drained = true;
				this.vessel = null;
				this.upStream = true;
				Thread.sleep(Param.TOWING_TIME);
				dprtLock.unlock();
			}
		} finally {
		}
	}

	/**IT WORKS IF NECESSARY
	 * the method for chamber draining, acquires the access of the lock, checks the
	 * status of it, if the lock is free and chamber is filled permits to the
	 * Operator to drain the chamber
	 */
	public synchronized void drain() throws InterruptedException {
		try {
			if (!this.occupied && !this.drained) {
				System.out.println("Chamber drains");
				this.drained = true;
				Thread.sleep(Param.OPERATE_TIME);
			}
		} finally {
		}

	}

	/**IT WORKS IF NECESSARY
	 * the method for chamber filling, acquires the access of the lock, checks the
	 * status of it, if the lock is free and chamber is drained permits to the
	 * Operator to fill the chamber
	 */
	public synchronized void fill() throws InterruptedException {
		try {

			if (!this.occupied && this.drained) {
				System.out.println("Chamber fills");
				this.drained = false;
				Thread.sleep(Param.OPERATE_TIME);
			}
		} finally {
		}
	}
}
