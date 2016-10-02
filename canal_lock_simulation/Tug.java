/**
 * @author Ilkan Esiyok
 * @date 2014-05-14
 * @purpose The operations of Tugs,
 * this file contains three type of Tug classes
 * Tug, Launch tug and Return tug
 */

import java.util.concurrent.locks.ReentrantLock;

public class Tug extends Thread {

	protected int tugId;

	protected java.util.concurrent.locks.Lock desLock = new ReentrantLock();

	protected java.util.concurrent.locks.Lock curLock = new ReentrantLock();
	
	protected java.util.concurrent.locks.Condition cond;//condition

	protected Section curSection;//current section

	protected Section desSection;// destination section

	public Tug(int tugId, Section curSection, Section desSection) {
		this.tugId = tugId;
		this.curSection = curSection;
		this.desSection = desSection;
		this.cond = desLock.newCondition();
	}

	/**
	 * the method for checking section status, acquires the access of the
	 * current and successor sections, checks the status of them,if the current
	 * section is occupied, and the destination is occupied as well, then the
	 * thread will wait for the condition of destination is free. if the current
	 * section is occupied, and the destination is free, then it will allow to
	 * the Tug to transport vessel
	 */
	public void checkStatus() throws InterruptedException {

		Param.getLocks(curLock, desLock);
		try {
			if (curSection.occupied) {
				while (desSection.occupied) {
					cond.await();
				}
				Vessel v = curSection.vessel;
				desSection.leave(curSection, v);
				desSection.move(v);
			}

		} finally {
			curLock.unlock();
			desLock.unlock();
		}
	}
	/**
	 * the method for checking section status,
	 * if the condition of checkStatus() method is provided,
	 * then it will send signal to waiting threads
	 */
	public void checkStatus2() throws InterruptedException {
		Param.getLocks(curLock, desLock);
		try {
			if (curSection.occupied && !desSection.occupied) {
				cond.signalAll();
			}
		} finally {
			curLock.unlock();
			desLock.unlock();
		}
	}
	 /**
	   * checks the condition of the sections infinitely
	   * 
	   */
	public void run() {
		while (true) {
			try {
				checkStatus();
				checkStatus2();
				//Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
/**
 * @purpose The operations of Launch Tugs
 */
class Launch_tug extends Thread {

	protected Lock lock;

	protected Section desSection;

	protected java.util.concurrent.locks.Lock lLock = new ReentrantLock();

	protected java.util.concurrent.locks.Lock secLock = new ReentrantLock();

	protected java.util.concurrent.locks.Condition cond;

	public Launch_tug(Lock lock, Section desSection) {
		this.lock = lock;
		this.desSection = desSection;
		this.cond = lLock.newCondition();
	}

	/**
	 * the method for checking lock and first section status, acquires the
	 * access of the lock and first section, checks the status of them, if the
	 * lock is occupied, and the first section is occupied as well, then the
	 * thread will wait for the condition of the destination is free. if the
	 * lock is occupied, and the destination is free, then it will allow to the
	 * Launch Tug to transport vessel
	 */
	public void checkStatus() throws InterruptedException {
		Param.getLocks(lLock, secLock);
		try {
			if (lock.occupied && lock.upStream) {
				while (desSection.occupied) {
					cond.await();
				}
				Vessel v = lock.vessel;
				lock.leaveLock(v, true);
				desSection.move(v);
			}

		} finally {
			lLock.unlock();
			secLock.unlock();
		}
	}
	/**
	 * the method for checking lock and first section status,
	 * if the condition of checkStatus() method is provided,
	 * then it will send signal to waiting threads
	 */
	public void checkStatus2() throws InterruptedException {
		Param.getLocks(lLock, secLock);
		try {
			if (lock.occupied && !desSection.occupied && lock.upStream) {
				cond.signalAll();
			}
		} finally {
			lLock.unlock();
			secLock.unlock();
		}
	}
	 /**
	   * checks the condition of the lock and the first section infinitely
	   * 
	   */
	public void run() {
		while (true) {
			try {
				checkStatus();
				checkStatus2();
				//Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
/**
 * @purpose The operations of Return Tugs
 */
class Return_tug extends Thread {

	protected Section curSection;

	protected Lock lock;

	protected java.util.concurrent.locks.Lock curLock = new ReentrantLock();

	protected java.util.concurrent.locks.Lock lLock = new ReentrantLock();

	protected java.util.concurrent.locks.Condition cond;

	public Return_tug(Section curSection, Lock lock) {
		this.curSection = curSection;
		this.lock = lock;
		this.cond = curLock.newCondition();
	}
	/**
	 * the method for checking lock and last section status, acquires the
	 * access of the lock and last section, checks the status of them, if the
	 * last section is occupied, and lock is occupied as well, then the
	 * thread will wait for the condition of the lock is free. if the
	 * last section is occupied, and the lock is unoccupied, then it will allow to the
	 * Return Tug to transport vessel
	 */
	public void checkStatus() throws InterruptedException {
		Param.getLock(curLock);
		try {
			if (curSection.occupied) {
				while (lock.occupied && lock.drained && !lock.upStream) {
					cond.await();
				}
				Vessel v = curSection.vessel;
				curSection.leave(curSection, v);
				lock.carryOutVessel(v, false);
			}

		} finally {
			curLock.unlock();
		}
	}
	/**
	 * the method for checking lock and last section status,
	 * if the condition of checkStatus() method is provided,
	 * then it will send signal to waiting threads
	 */
	public void checkStatus2() throws InterruptedException {
		Param.getLock(curLock);
		try {
			if (curSection.occupied && !lock.occupied && !lock.drained
					&& lock.upStream) {
				cond.signalAll();
			}
		} finally {
			curLock.unlock();
		}
	}
	 /**
	   * checks the condition of the lock and the last section infinitely
	   * 
	   */
	public void run() {
		while (true) {
			try {
				checkStatus();
				checkStatus2();
				//Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}