
public class cpuCore {

	private int id;
	private int curr_Endtime;
	//used to specify how much work the core has left.
	
	public cpuCore(int theID)
	{
		this.id = theID;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCurr_Endtime() {
		return curr_Endtime;
	}
	public void setCurr_Endtime(int curr_endtime) {
		this.curr_Endtime = curr_endtime;
	}
	
	public int compareTo(cpuCore arg0) {
			return Integer.compare(arg0.getCurr_Endtime(), this.curr_Endtime);	
	}
	

}
