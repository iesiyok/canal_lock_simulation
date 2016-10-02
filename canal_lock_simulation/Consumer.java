/**
 * @author Ilkan Esiyok
 * @date 2014-05-14
 * @purpose The consumer of the vessels which are ready to depart
 */ 


import java.util.concurrent.locks.ReentrantLock;


public class Consumer extends Thread{

	protected Lock lock;
	
	protected java.util.concurrent.locks.Lock consLock = new ReentrantLock();
	protected java.util.concurrent.locks.Condition cond;
	
	public Consumer(Lock lock) {
		this.lock = lock;
	}
	 /**
	   * checks the condition of the lock infinitely at random intervals,
	   * if it is occupied by a vessel and chamber water level is filled and the direction is downstream
	   * the vessel will be departed
	   * 
	   */
	public void run(){
		while(true){
			
			try {
				Param.getLock(consLock);
				if(lock.occupied && !lock.drained && !lock.upStream ){
					lock.depart(lock.vessel, false);					
				}
				consLock.unlock();
				Thread.sleep(Param.departureLapse());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
