/**
 * @author Ilkan Esiyok
 * @date 2014-05-14
 * @purpose The operator of the lock system
 */ 


import java.util.concurrent.locks.ReentrantLock;

public class Operator extends Thread{
	
	protected Lock lock;
	
	protected Section[] secList;
	
	private java.util.concurrent.locks.Lock opLock = new ReentrantLock();
	private java.util.concurrent.locks.Lock vesLock = new ReentrantLock();
	
	protected java.util.concurrent.locks.Condition cond;
	
	public Operator(Lock lock, Section[] secList) {
		this.lock = lock;
		this.secList = secList;
		this.cond = opLock.newCondition();
	}
	  /**
	   * acquires the lock,
	   * Counts vessels in the canal system, excluding the lock
	   */
	private synchronized boolean vesselCountInCanal(Section[] secList) throws InterruptedException{
		
		try{
			Param.getLock(vesLock);
			int count = 0; 
			for (Section section : secList) {
				if(section.occupied){
					count++;
				}
			}
			if(secList[Param.SECTIONS-1].occupied){
				return true;
			}else{
				if(Param.SECTIONS - count <= 2){
					return true;
				}else{
					return false;
				}
			}
		}finally{
			vesLock.unlock();
		}

	}
	 /**
	   * checks the condition of the lock infinitely at random intervals
	   * 
	   */
	public void run(){
		
			while(true){
				try {
					Param.getLock(opLock);
					if(!lock.occupied){
						if((secList[Param.SECTIONS-1].occupied && lock.drained)){
							lock.fill();
						}else{
							boolean control = vesselCountInCanal(secList);
							if(control){
								lock.fill();
							}else{
								lock.drain();
							}
						}Thread.sleep(Param.operateLapse());
					}
						
					opLock.unlock();
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	}

}
