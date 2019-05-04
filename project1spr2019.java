import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javax.swing.JFileChooser; //native java's file selector
import javax.swing.filechooser.FileNameExtensionFilter; //needed to restrict file selection to .txt only 

public class project1spr2019 {

	public static void adaptiveFCFS(cpuCore coreList[], cpuProc procList[], int size, int sleepVal) throws InterruptedException {

		char threadID = 'A';
		List<cpuProc> newProcList = new ArrayList<>();
		// loop to split up a processes
		for(int i = 0; i < size; i++) {
			// getting the number of threads created from a process
			int numberOfThreads = (int) ((100 * Math.random()) % coreList.length + 1);
			// ensuring that the burst time is not greater than the number of threads
			numberOfThreads = numberOfThreads > procList[i].getBurst_time() ? procList[i].getBurst_time(): numberOfThreads;
			int remainingBurstTime = procList[i].getBurst_time();
			int sizeOfBursts = remainingBurstTime / numberOfThreads;
			for(int j = 0; j < numberOfThreads; j++) {
				int newBurstTime = j == numberOfThreads - 1 ? remainingBurstTime: sizeOfBursts;
				cpuProc newThread = new cpuProc(j,
												procList[i].getArrival_time(),
												newBurstTime,
												procList[i].getPriority());
				newThread.setTid(threadID);
				newThread.setCompType('f');
				newProcList.add(newThread);
				remainingBurstTime -= sizeOfBursts;
			}
			threadID++;
		}

		Collections.sort(newProcList);
		Queue<cpuProc> processQueue = new PriorityQueue<>();

		int numOfCores = coreList.length; 
		int procNum = newProcList.size();
		int trnArnd = 0;
		int execTime = 0;
		for(int h=0;h<size;h++) {
			execTime = execTime+procList[h].getBurst_time();
		}
		
		int cpu_burst = 0; 
		int idle_time = 0;
		int i = 0;
	
		cpuProc curr_Proc[] = new cpuProc[numOfCores]; //initialize
		
		StringBuilder header = new StringBuilder("TIME\t");
		StringBuilder line = new StringBuilder("--------");
		for(int j = 0; j < coreList.length; j++) {
			int cpuNum = j + 1;
			header.append("CPU" + cpuNum + "\t");
			line.append("--------");
		}
		header.append("\n" + line.toString());
		System.out.println(header);
		String colorReset = (char)27 + "[00m";
		while(procNum > 0) {
			TimeUnit.MILLISECONDS.sleep(sleepVal);
			StringBuilder processOutput = new StringBuilder();
			processOutput.append((cpu_burst)+"\t");
			StringBuilder processQueueOutput = new StringBuilder("");
			int now = i;
			boolean processArrived = false;
			while(i < newProcList.size() && newProcList.get(i).getArrival_time() == cpu_burst) { //if Processes arrived, add them to the queue.
				int colorVal = (newProcList.get(i).getTid() % 7) + 31 ;
				String colorNum = (char)27 + "[" + colorVal + "m";
				if(i == now) {
					processQueueOutput.append("[ Arrived: " + colorNum + newProcList.get(i).getTid() + newProcList.get(i).getPid() + colorReset);
					processArrived = true;
				}
				else processQueueOutput.append(", " + colorNum + newProcList.get(i).getTid() + newProcList.get(i).getPid() + " - " + newProcList.get(i).getBurst_time() + colorReset);
				processQueue.add(newProcList.get(i));
				i++;
			}
			if(processArrived) processQueueOutput.append(" ]");

			char currentProcessTid = !processQueue.isEmpty()? processQueue.peek().getTid(): 'Z';
			Object[] processesInQueue = processQueue.toArray();
			int needed = 0;
			for(int j = 0; j < processesInQueue.length; j++) {
				cpuProc nextProcess = (cpuProc) processesInQueue[j];
				if(nextProcess.getTid() == currentProcessTid) needed++;
			}
			int available = 0;
			for(int j = 0; j < coreList.length; j++) {
				if(curr_Proc[j] == null) available++;
			}
			for(int x = 0; x<numOfCores; x++) {
				int burst;
				if(curr_Proc[x] == null) {
					if(needed <= available && !processQueue.isEmpty() && processQueue.peek().getTid() == currentProcessTid) {
						curr_Proc[x] = processQueue.poll();
					}
					if(curr_Proc[x] == null) {
						processOutput.append("IDLE\t");	
					} else {
						burst = curr_Proc[x].getBurst_time();
						int colorVal = (curr_Proc[x].getTid() % 7) + 31 ;
						String colorNum = (char)27 + "[" + colorVal + "m";
						processOutput.append(colorNum + curr_Proc[x].getTid());
						processOutput.append(curr_Proc[x].getPid() + colorReset + ": "+burst+"\t");
						curr_Proc[x].setBurst_time(burst-1);
					}
				}
				else {
					burst = curr_Proc[x].getBurst_time(); //check its progress
					int colorVal = (curr_Proc[x].getTid() % 7) + 31 ;
					String colorNum = (char)27 + "[" + colorVal + "m";
					if(burst == 0) {
						curr_Proc[x] = null;
						procNum--;
						//System.out.println("The process "+pid+" has finished execution on core "+x+" at burst: "+cpu_burst);	
						trnArnd=cpu_burst+trnArnd;
						if(available >= needed) {
							curr_Proc[x] = processQueue.poll(); //pull a new one.
						}
						if(curr_Proc[x] != null) {
							curr_Proc[x].setBurst_time(curr_Proc[x].getBurst_time()-1);
							colorVal = (curr_Proc[x].getTid() % 7) + 31 ;
							colorNum = (char)27 + "[" + colorVal + "m";
							processOutput.append(colorNum + curr_Proc[x].getTid() + curr_Proc[x].getPid() + colorReset + ": " + curr_Proc[x].getBurst_time() + "\t");
						} else {
							processOutput.append(colorNum + "Done\t" + colorReset);
						}
					}
					else if(burst >0) {
						processOutput.append(colorNum + curr_Proc[x].getTid());
						processOutput.append(curr_Proc[x].getPid() + colorReset + ": "+burst+"\t");
						burst--;
						curr_Proc[x].setBurst_time(burst);
					}			
				}
			
				//akszuli moze poprostu zrob zero i na koncu if jezeli jakis process jest zero to go wypierdol i daj wiadomosc.
				// lepiej tak niz pare razy ta sama wiadomosc pisac tbh.
			}
			if(size == 0) System.out.println(processOutput);
			else System.out.println(processOutput + " " + processQueueOutput);
			cpu_burst++; //go to next burst
		} // end of while(procNum>0)
	}

	public static void multiCoreLPT(cpuCore coreList[], cpuProc procList[], int size, int sleepVal) throws InterruptedException
	{
		for(int i = 0; i<size;i++){
			procList[i].setCompType('f'); }
		//selecting the comp type for all object will set their comparator to compare their arrival time		
		Arrays.sort(procList);//thanks to choosing appropriate comparator the array will be sorted by arrival time
		for(int i = 0; i<size;i++) {
			procList[i].setCompType('x'); }
		//selecting the comp type for all object will set their comparator to compare their burst time 
		
		Queue<cpuProc> PriorityQueue = new PriorityQueue<cpuProc>(); 
		// Last in First Out. New elements go to the end. Removes elements from the front
		
		//check how many Cores are available
		int numOfCores = coreList.length; 
		
		
		
		int procNum = size; //number of processes
		int trnArnd = 0; // used for calculating the turnaround time
		int execTime = 0; // used for calculating process time
		for(int h=0;h<size;h++) {
			execTime = execTime+procList[h].getBurst_time();
		}
		
		int cpu_burst = 0; 
		int idle_time = 0;
		int i = 0;
	
		cpuProc curr_Proc[] = new cpuProc[numOfCores]; //initialize
		
		StringBuilder header = new StringBuilder("TIME\t");
		StringBuilder line = new StringBuilder("--------");
		for(int j = 0; j < coreList.length; j++) {
			int cpuNum = j + 1;
			header.append("CPU" + cpuNum + "\t");
			line.append("--------");
		}
		header.append("\n" + line.toString());
		System.out.println(header);
		while(procNum >0) {
			TimeUnit.MILLISECONDS.sleep(sleepVal);
			StringBuilder processOutput = new StringBuilder();
			processOutput.append((cpu_burst)+"\t");
			StringBuilder processQueueOutput = new StringBuilder("");
			int now = i;
			boolean processArrived = false;
			while(i<size && procList[i].getArrival_time() == cpu_burst) {
				if(i == now) {
					processQueueOutput.append("[ Arrived: P"+procList[i].getPid());
					processArrived = true;
				}
				else processOutput.append(", P" + procList[i].getPid());
				PriorityQueue.add(procList[i]);
				i++;
			}
			
			if(processArrived) processQueueOutput.append(" ]");
			
			//you have processes in your queue.
			//sorted by size (biggest first).
			// now pull them out and assign them to eachs core current process holder curr_Proc
			
			//if PriorityQueue.isEmpty() != false wtedy dodawaj
			
			for(int x = 0; x<numOfCores; x++)
			{
				int burst;
				
				
				if(curr_Proc[x] == null)
				{	//if no process is currently executed by a core
					//poll a new one from a queue and execute it
					//if still null that means cpu can go to idle state
					
					curr_Proc[x] = PriorityQueue.poll();
					if(curr_Proc[x] == null) //polling from an empty queue gives null
					{//getting null means no processes are currently available for execution.
					// inform a core is not doing anything.
						processOutput.append("IDLE\t");	
					}
					else
					{
						burst = curr_Proc[x].getBurst_time();
						processOutput.append(curr_Proc[x].getTid());
						processOutput.append(curr_Proc[x].getPid()+": "+burst+"\t");
						curr_Proc[x].setBurst_time(burst-1);
					}
				}
				else // A current process is already assigned to a core
				{
					burst = curr_Proc[x].getBurst_time(); //check its progress
					
					if(burst == 0)
					{
						curr_Proc[x] = null;
						procNum--;
						processOutput.append("Done\t");
						//System.out.println("The process "+pid+" has finished execution on core "+x+" at burst: "+cpu_burst);	
						trnArnd=cpu_burst+trnArnd;
						curr_Proc[x] = PriorityQueue.poll(); //pull a new one.
							if(curr_Proc[x] != null) 
								curr_Proc[x].setBurst_time(curr_Proc[x].getBurst_time()-1);
					}
					else if(burst >0)
					{
						processOutput.append(curr_Proc[x].getTid());
						processOutput.append(curr_Proc[x].getPid()+": "+burst+"\t");
						burst--;
						curr_Proc[x].setBurst_time(burst);
					}			
				}
				
				 //this will fill all the current process holders for each core.
				
				//make it display in a singe line?? 
				// ex.
				// cpu0		|cpu1		|cpu2		|cpu3
				// p1: 10	p2:5		p3:5		IDLE
				// p1:	9	p2:4		p3:4		p4: 2
				
				//etc etc
			
				//akszuli moze poprostu zrob zero i na koncu if jezeli jakis process jest zero to go wypierdol i daj wiadomosc.
				// lepiej tak niz pare razy ta sama wiadomosc pisac tbh.
			}
			if(size == 0) System.out.println(processOutput);
			else System.out.println(processOutput + " " + processQueueOutput);
			cpu_burst++; //go to next burst
		} // end of while(procNum>0)
		
		
		
		float apt = (float)execTime/(float)size;
		float att = (float)trnArnd/(float)size;
		float awt = att - apt;
		float cpu_util = (float)cpu_burst/((float)cpu_burst+(float)idle_time);
		System.out.println("\nExecution completed. \n"
				+"Average waiting time: "+awt+"\n"
				+"Average turnaround time: "+att+"\n"
				+"Average process time: "+apt
				+"\nCPU utilization: "+cpu_util);
		
		
		
	}
	
	public static String promptForFile()
	{
		//this method initializes swing's file selector to get a location of input.txt
	    JFileChooser fileChooser = new JFileChooser();
	    	    
	    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    
	    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".txt", "txt")); //adds ability to select only .txt files
	    fileChooser.setAcceptAllFileFilterUsed(false); //only txt files can be chosen
	    if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) //no parent of the window but its ok
	    {
	        return fileChooser.getSelectedFile().getAbsolutePath();
	    }

	    return null; //in case of failure;
	}
	
	@SuppressWarnings("resource")
	public static cpuProc[] readFile(cpuProc[] procList) throws IOException
	{	
		String PathToInput = promptForFile(); //this function creates swing's window for file selection
		File intxt = new File(PathToInput); //collect the path of a selected file
		//File intxt = new File("input.txt");
				
		BufferedReader read = new BufferedReader(new FileReader(intxt)); //Access file for line reading
		
		
		//Line counter that will give us size for the process list
		int lineCount = 0;
		while(read.readLine() != null)
		{
			lineCount++;
		}
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		
		procList = new cpuProc[lineCount];//create array of process object
		
		read = new BufferedReader(new FileReader(intxt)); //access file again for reading
		String line; //for reading lines 
		int pid,arriv,brst,prior; // holders for data to be put in Process objects
		String lineSep[] = null; //for seperating the line reicevied from file
		
		for(int i = 0; i<lineCount;i++)
		{
			procList[i] = new cpuProc('a');	//initializng for arrival
			line = read.readLine(); //reading line
			lineSep = line.split(" "); //spliting it into 4 categories
			pid = Integer.parseInt(lineSep[0]); //pid is the first category
			arriv =Integer.parseInt(lineSep[1]);//arrival time is the second
			brst = Integer.parseInt(lineSep[2]);//burst time is the third
			prior = Integer.parseInt(lineSep[3]);//priority is the fourth
			
			//System.out.println(pid+" "+arriv+" "+brst+" "+prior);
			
			procList[i].setPid(pid);
			procList[i].setArrival_time(arriv);
			procList[i].setBurst_time(brst);
			procList[i].setPriority(prior);
			procList[i].setTid('P');
		}
		
		read.close();
		
		return procList;
		
	}
	
	public static void main(String[] args) throws InterruptedException, IOException
	{
		// AWT: Average wait time is sum of all the waits divided by number of processes
		// ATT: average tunraround time is (the sum of all the times when a process is finished minus their arrive time) divided by no of processes
		// APT: average process time is sum of burst time divided by no of processes
		// APT = ATT - AWT
		
		cpuProc[] procList = new cpuProc[0]; //create array of process objects
		cpuCore[] coreList = new cpuCore[2]; // 4 core processor;
		
		for(int i = 0; i<2;i++)
		{
			coreList[i] = new cpuCore(i);
		}
	
		
		Scanner read = new Scanner(System.in);
		
		System.out.println("Multicore CPU Scheduling Simulator.");
		char menuSel;
		
		do
		{
			System.out.print("Press Enter to initiate input.txt file selection menu.");
			System.in.read();
			
			procList = readFile(procList); //reads from file and updates the array accordingly
			int size = procList.length;//Receives # of processes
			
			System.out.print("Please specify the number of cores: ");
			int cores = read.nextInt();
			
			coreList = new cpuCore[cores];					
			// sleepVal for you to change;
			int sleepVal = 250;

			StringBuilder menu = new StringBuilder("Which algorithm would you like to use?\n");
			menu.append("  1. LPT\n");
			menu.append("  2. Adaptive First Come First Serve\n");
			menu.append("Enter a digit: ");
			System.out.print(menu.toString());
			int scheduler = read.nextInt();
			switch(scheduler) {
				case 1:
					multiCoreLPT(coreList, procList, size, sleepVal);
					break;
				case 2:
					adaptiveFCFS(coreList, procList, size, sleepVal);
					break;
				default:
					break;
			}
			
			System.out.println("Do you wish to repeat the program?");
			menuSel = read.next().charAt(0);
		
		} while (menuSel == 'y' || menuSel == 'Y');
		
		read.close();
	}

}