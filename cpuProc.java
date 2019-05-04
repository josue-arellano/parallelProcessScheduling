public class cpuProc implements Comparable<cpuProc>
{
	/*
	 * pid, arrival time, burst_time, priority
	 */

	private int pid, arrival_time, burst_time, priority;
	private char compType, tid;
	
	public cpuProc()
	{
		
	}
	
	public cpuProc(char cpType)
	{
		this.compType = cpType;

	}
	
	public cpuProc(int pid, int arrival_time, int burst_time, int priority)
	{
		this.pid = pid;
		this.arrival_time = arrival_time;
		this.burst_time = burst_time;
		this.priority = priority;
		this.tid = 'P';
	}

	public void setTid(char tid) {
		this.tid = tid;
	}

	public char getTid() {
		return tid;
	}

	public void setCompType(char compType)
	{
		this.compType = compType;
	}
	
	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getArrival_time() {
		return arrival_time;
	}

	public void setArrival_time(int arrival_time) {
		this.arrival_time = arrival_time;
	}

	public int getBurst_time() {
		return burst_time;
	}

	public void setBurst_time(int burst_time) {
		this.burst_time = burst_time;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	//@Override
	public int compareTo(cpuProc arg0) {
	if(compType == 's')
		return Integer.compare(this.burst_time, arg0.getBurst_time());	
	else if(compType == 'p')
		return Integer.compare(this.priority, arg0.getPriority());	
	else if(compType == 'f')	// if(compType == 'f')
		return Integer.compare(this.arrival_time, arg0.getArrival_time());	
	else if(compType == 'x')
		return Integer.compare(arg0.getBurst_time(),this.burst_time);
	else
		return 0;
	}	
	
	@Override
	public String toString() {
		return "pid: " + pid + ", tid: " + tid + ", burstTime: " + burst_time + ", arrivalTime: " + arrival_time;
	}
	
}