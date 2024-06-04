import java.util.LinkedList;
import java.util.Queue;

//joint class for First-Come-First-Serve and Round-Robin
public class FCFSRR{
    private int currentTime = 0; //tracks time
    private String algType; //tracks whether FCFS or RR
    
    public FCFSRR(String type) {
        algType = type;
    }
    
    public void runFCFSRR() {
        //ProcessFinder class creates random processes and puts into a priority queue
        //Runs in a thread in order to run the algorithm and find new processes at the same time
        ProcessFinder pQueue = new ProcessFinder();    
        Thread pFinderThread = new Thread(pQueue,"pQueue");
        pFinderThread.start();
        
        //Creates queue for blocked processes
        Queue<Process> blockedReadyQ = new LinkedList<>();
        
        //Variables to record statistics
        double avgFinishTime = 0;
        double avgResponseTime = 0;
        double avgTAT = 0;
        double TSRatio = 0;
        double throughput = 0;
        int numFinished = 0;
        int totalServiceTime = 0;
        
        //Creates report object which runs a thread to print stats every minute
        Report report = new Report();
        report.startReport();
        
        //holds current process to execute
        Process currentP = null;
        
        //loop indefinitely
        while(true){
            //if there are no initial processes, then wait
            while (pQueue.isEmpty()) {
                System.out.println("No processes available.");
                currentTime++;                  //increment time
                pQueue.setTime(currentTime);    //sync time with pqueue
                timeSlice();                    //waits for one second
            }
            
            //loops while pQueue or blocked queue are not empty
            while(!pQueue.isEmpty() || !blockedReadyQ.isEmpty()) {
                timeSlice();
                
                //if blocked queue is empty or there is a ready process in the blocked queue
                if (blockedReadyQ.isEmpty() || (!blockedReadyQ.isEmpty() && currentTime <= blockedReadyQ.peek().getBlockTime())) {
                    //if pQueue is not empty, but next process is not ready yet, then wait
                    if (pQueue.isEmpty() || pQueue.peek().getArrivalTime() > currentTime) {
                        System.out.println("No processes available.");
                        currentTime++;
                        pQueue.setTime(currentTime);
                        continue;
                    }
                    //if there is a ready process in pQueue, then remove it
                    currentP = pQueue.remove();
                }
                //if blocked queue is not empty, then remove next process
                //in FCFS and RR, then blocked queue has higher priority than pQueue
                else {
                    currentP = blockedReadyQ.remove();
                }
                
                //each loop runs process for one second until it reaches its service time
                for (currentP.getRunningTime();  currentP.getRunningTime() < currentP.getServiceTime(); currentP.addRunTime()){
                    //if current running time equals io activity time, then move to blocked queue and update io information
                    if (currentP.getIoTime() > 0 && (currentP.getRunningTime() == currentP.getIoActivity())) {
                        System.out.println("Process " + currentP.getId() + " requesting I/O.");
                        currentP.incrementIoActivity(currentTime);
                        blockedReadyQ.add(currentP);
                        break;
                    }
                    
                    //Prints information and record response time upon first execution of a process
                    if (currentP.getRunningTime() == 0) {
                        System.out.println("New Process " + currentP.getId() + " executing with arrival time of " +
                            currentP.getArrivalTime() + " seconds and service time of " + currentP.getServiceTime() + " seconds.");
                        currentP.setResponseTime(currentTime - currentP.getArrivalTime());
                    }
                    
                    //Increment time 
                    currentTime++;
                    pQueue.setTime(currentTime);
                    timeSlice();
                    
                    //If RR and current process has not finished executing
                    if (algType.equals("RR") && currentP.getRunningTime()+1 < currentP.getServiceTime()) {
                        //checks pQueue for any ready processes
                        while(!pQueue.isEmpty() && pQueue.peek().getArrivalTime() <= currentTime) {
                            //arbitrarily stops when blocked queue holds 5 processes for better simulation
                            //too many processes, then it will take too much time to finish one process
                            if (blockedReadyQ.size() >= 5) {
                                break;
                            }
                            blockedReadyQ.add(pQueue.remove());
                        }
                        
                        //adds runtime then moves process to blocked queue to execute next process
                        currentP.addRunTime();
                        blockedReadyQ.add(currentP);
                        break;
                    }
                }                
                
                //if process has finished executing
                if(currentP.getServiceTime() == currentP.getRunningTime()) {
                    //set finish time and print results
                    currentP.setFinishTime(currentTime);
                    System.out.println("\nProcess " + currentP.getId() + " has finished executing!");
                    System.out.println("Finish Time = " + currentP.getFinishTime() + " seconds.");
                    System.out.println("Response Time = " + currentP.getResponseTime() + " seconds.");
                    
                    int currentTAT = currentP.getFinishTime() - currentP.getArrivalTime();
                    System.out.println("Turn Around Time = " + currentTAT + " seconds.");
                    
                    System.out.print("Ratio of Turn Around Time to Service Time = ");
                    System.out.format("%.2f", (double)currentTAT / currentP.getServiceTime());
                    System.out.println(" seconds.");
                    
                    numFinished++;
                    throughput = (double)numFinished / currentP.getFinishTime();
                    System.out.print("Current Throughput: ");
                    System.out.format("%.2f", throughput);
                    System.out.println(" processes per second.\n");
                    
                    //update averages
                    totalServiceTime += currentP.getServiceTime();
                    //avg finish time = total finish time / # finished processes
                    avgFinishTime = (avgFinishTime*(numFinished-1) + currentP.getFinishTime()) / numFinished;
                    //avg response time = total response time / # finished processes
                    avgResponseTime = (avgResponseTime*(numFinished-1) + currentP.getResponseTime()) / numFinished;
                    //avg turn around time = total turn around time / # finished processes
                    avgTAT = (avgTAT*(numFinished-1) + currentP.getFinishTime() - currentP.getArrivalTime()) / numFinished;
                    
                    //total turn around time / total service time
                    TSRatio = (TSRatio*(numFinished-1) + 
                        ((currentP.getFinishTime() - currentP.getArrivalTime()) / (double)currentP.getServiceTime())) / numFinished;
                    
                    //update report with current averages
                    report.setFinish(avgFinishTime);
                    report.setResponse(avgResponseTime);
                    report.setTAT(avgTAT);
                    report.setTSRatio(TSRatio);
                    report.setThroughput(throughput);
                }
            }
        }
    }
    
    //method to wait one second
    public void timeSlice(){
        try{
            Thread.sleep(1000);
        }catch(Exception e) {
            e.printStackTrace();
        }   
    }
}