/**
 * @author Ilkan Esiyok
 * @date 2014-05-14
 * @purpose The operations on sections
 */


public class Section {

	protected volatile boolean occupied = false;
	
	protected int id = 0;//section id
	
	protected volatile Vessel vessel = null;
	
	public Section(int sectionNumber) {
		this.id = sectionNumber;
	}
	/**
	 * the synchronized method for moving vessels, acquires the access of the section,
	 * checks the status of it,if the section is free,
	 * allows to  Launch tug and Tug to transport vessel to inside of the section
	 */
	public void move(Vessel v) throws InterruptedException{
			try{
				if(!occupied && v != null){
					System.out.println(v.toString() + " enters section "+id);
					this.occupied = true;
					this.vessel = v;
					Thread.sleep(Param.TOWING_TIME);				
				}
			}finally{
			}
	}
	/**
	 * the synchronized method for leaving vessels, acquires the access of the section,
	 * checks the status of it,if the section is occupied,
	 * allows to  Return tug and Tug to remove vessel from the section
	 */
	public void leave(Section s, Vessel v) throws InterruptedException{
			try{
				if(s.occupied && v != null){
					System.out.println(v.toString() + " leaves section "+s.id);
					s.occupied = false;
					s.vessel = null;
					Thread.sleep(Param.TOWING_TIME);
					
				}
			}finally{
			}
	}
	
}
