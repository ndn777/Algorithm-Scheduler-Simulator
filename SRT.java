import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

//Shortest Remaining Time 
public class SRT{
    private int currentTime = 0;
    
    public void runSRT() {
        //ProcessFinder class creates random processes and puts into a priority queue
        //Runs in a thread in order to run the algorithm and find new processes at the same time
        ProcessFinder pQueue = new ProcessFinder();    
        Thread pFinderThread = new Thread(pQueue,"pQueue");
        pFinderThread.start();
        
        //Ready queue
        PriorityQueue<Process> rQueue = new PriorityQueue<>();
        
        //Variables to hold averages
        double avgFinishTime = 0;
        double avgResponseTime = 0;
        double avgTAT = 0;
        double TSRatio = 0;
        double throughput = 0;
        int numFinished = 0;
        int totalServiceTime = 0;
        
        //Start report to print averages every minute
        Report report = new Report();
        report.startReport();
        
        //Holds current process to execute
        Process currentP = null;
        
        //Loops indefinitely
        while(true){
            //If there are no processes, then wait
            while (pQueue.isEmpty()) {
                System.out.println("No processes available.");
                //increment time
                currentTime++;
                pQueue.setTime(currentTime);
                timeSlice();
            }
            
            //If pQueue or ready queue are not empty
            while(!pQueue.isEmpty() || !rQueue.isEmpty()) {
                timeSlice();
                
                //if ready queue is empty
                if (rQueue.isEmpty()) {
                    //if there are no ready processes in pQueue, then wait
                    if (pQueue.peek().getArrivalTime() > currentTime) {
                        System.out.println("No processes available.");
                        currentTime++;
                        pQueue.setTime(currentTime);
                        continue;
                    }
                }
                //moves all ready processes in pQueue and put them into ready queue
                while (!pQueue.isEmpty() && (pQueue.peek().getArrivalTime() <= currentTime)) { 
                    Process tempP = pQueue.remove();
                    //sorts according to lowest service time instead of arrival time
                    tempP.setPriority(tempP.getServiceTime());
                    rQueue.add(tempP);
                }
                //Get next ready process
                currentP = rQueue.remove();
                
                //each iteration executes process for one second until it reaches service time
                for (currentP.getRunningTime(); currentP.getRunningTime() < currentP.getServiceTime(); currentP.addRunTime()){
                    //if current running time = io activity time
                    if (currentP.getIoTime() > 0 && (currentP.getRunningTime() == currentP.getIoActivity())) {
                        System.out.println("Process " + currentP.getId() + " requesting I/O.");
                        currentP.incrementIoActivity(currentTime);
                        
                        //if ready queue is not empty, then get next process before moving current process to ready queue
                        //ensures current process is not selected again for the next execution
                        if (!rQueue.isEmpty()) {
                            //tempP holds next process
                            Process tempP = rQueue.remove();
                            rQueue.add(currentP);
                            currentP = tempP;
                            //removes one second of runtime since the next for-loop iteration will add one second
                            currentP.removeRunTime();
                            continue;
                        }
                        //if ready queue is empty, then add to ready queue
                        else {
                            rQueue.add(currentP);
                            break;
                        }
                    }
                    
                    //prints information of new process and record reponse time
                    if (currentP.getRunningTime() == 0) {
                        System.out.println("New Process " + currentP.getId() + " executing with arrival time of " +
                            currentP.getArrivalTime() + " seconds and service time of " + currentP.getServiceTime() + " seconds.");
                        currentP.setResponseTime(currentTime - currentP.getArrivalTime());
                    }
                    
                    //increment time and update priority value (less remaining time)
                    currentTime++;
                    pQueue.setTime(currentTime);
                    timeSlice();
                    currentP.setPriority(currentP.getPriority()-1);
                    
                    //while pQueue is not empty and there are ready processes
                    while(!pQueue.isEmpty() && (pQueue.peek().getArrivalTime() <= currentTime)) { 
                        Process tempP = pQueue.remove();
                        //change priority to service time and add to ready queue
                        tempP.setPriority(tempP.getServiceTime());
                        rQueue.add(tempP);
                    }
                    
                    //if ready queue is not empty and next process has a lower remaining time
                    if (!rQueue.isEmpty() && (rQueue.peek().getPriority() < currentP.getPriority())) {
                        //increment runtime and add to ready queue
                        currentP.addRunTime();
                        rQueue.add(currentP);
                        break;
                    }
                }                
                
                //if process has finished executing, print results and update averages
                if(currentP.getServiceTime() == currentP.getRunningTime()) {
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
                    
                    //calculate averages
                    totalServiceTime += currentP.getServiceTime();
                    avgFinishTime = (avgFinishTime*(numFinished-1) + currentP.getFinishTime()) / numFinished;
                    avgResponseTime = (avgResponseTime*(numFinished-1) + currentP.getResponseTime()) / numFinished;
                    avgTAT = (avgTAT*(numFinished-1) + currentP.getFinishTime() - currentP.getArrivalTime()) / numFinished;
                    TSRatio = (TSRatio*(numFinished-1) + 
                        ((currentP.getFinishTime() - currentP.getArrivalTime()) / (double)currentP.getServiceTime())) / numFinished;
                    
                    //update values in report
                    report.setFinish(avgFinishTime);
                    report.setResponse(avgResponseTime);
                    report.setTAT(avgTAT);
                    report.setTSRatio(TSRatio);
                    report.setThroughput(throughput);
                }
            }
        }
    }
    
    //Method to wait one second
    public void timeSlice(){
        try{
            Thread.sleep(1000);
        }catch(Exception e) {
            e.printStackTrace();
        }   
    }
}