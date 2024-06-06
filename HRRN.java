import java.util.PriorityQueue;

//Highest Response Ratio Next
public class HRRN{
    private int currentTime = 0;
    
    public void runHRRN() {
        //ProcessFinder class creates random processes and puts into a priority queue
        //Runs in a thread in order to run the algorithm and find new processes at the same time
        ProcessFinder pQueue = new ProcessFinder();    
        Thread pFinderThread = new Thread(pQueue,"pQueue");
        pFinderThread.start();
        
        //Ready Queue
        PriorityQueue<Process> rQueue = new PriorityQueue<>();
        
        //Variables to store averages
        double avgFinishTime = 0;
        double avgResponseTime = 0;
        double avgTAT = 0;
        double TSRatio = 0;
        double throughput = 0;
        int numFinished = 0;
        
        //Runs thread to print averages every minute
        Report report = new Report();
        report.startReport();
        
        //Holds current process
        Process currentP = null;
        
        //loops indefinitely
        while(true){
            //If pQueue is empty, then wait
            while (pQueue.isEmpty()) {
                System.out.println("No processes available.");
                currentTime++;
                pQueue.setTime(currentTime);
                timeSlice();
            }
            
            //If pQueue or ready queue are not empty
            while(!pQueue.isEmpty() || !rQueue.isEmpty()) {
                timeSlice();
                
                //if ready queue is empty, then wait
                if(rQueue.isEmpty()) {
                    if (pQueue.peek().getArrivalTime() > currentTime) {
                        System.out.println("No processes available.");
                        currentTime++;
                        pQueue.setTime(currentTime);
                        continue;
                    }
                }
                
                //if pQueue is empty and there are ready processes, then put them into ready queue
                while (!pQueue.isEmpty() && (pQueue.peek().getArrivalTime() <= currentTime)) { 
                    Process tempP = pQueue.remove();
                    //update process to be HRRN type for sorting
                    tempP.setHRRN(true);
                    rQueue.add(tempP);
                }
                
                //For each process in ready queue, update priority values using the formula:
                //Response ratio = (Waiting time + service time) / service time
                //Waiting time = current time - arrival time
                for (Process p : rQueue) {
                    double pValue = (double)(currentTime - p.getArrivalTime() + p.getServiceTime()) / p.getServiceTime();
                    p.setPriority(pValue);
                }
                
                //remove then add a process to re-sort the queue
                currentP = rQueue.remove();
                rQueue.add(currentP);
                
                //get next process
                currentP = rQueue.remove();
                
                //Runs a process until it reaches its service time
                for (currentP.getRunningTime(); currentP.getRunningTime() < currentP.getServiceTime(); currentP.addRunTime()){
                    //When process reaches an io time, update info and send to rQueue
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
                    
                    //When new process executes, print information and record response time
                    if (currentP.getRunningTime() == 0) {
                        System.out.println("New Process " + currentP.getId() + " executing with arrival time of " +
                            currentP.getArrivalTime() + " seconds and service time of " + currentP.getServiceTime() + " seconds.");
                        currentP.setResponseTime(currentTime - currentP.getArrivalTime());
                    }
                    
                    //Increment one second
                    currentTime++;
                    pQueue.setTime(currentTime);
                    timeSlice();
                }                
                
                //When a process finishes executing, print results and update averages
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
                    
                    //Calculate averages
                    avgFinishTime = (avgFinishTime*(numFinished-1) + currentP.getFinishTime()) / numFinished;
                    avgResponseTime = (avgResponseTime*(numFinished-1) + currentP.getResponseTime()) / numFinished;
                    avgTAT = (avgTAT*(numFinished-1) + currentP.getFinishTime() - currentP.getArrivalTime()) / numFinished;
                    TSRatio = (TSRatio*(numFinished-1) + 
                        ((currentP.getFinishTime() - currentP.getArrivalTime()) / (double)currentP.getServiceTime())) / numFinished;
                    
                    //update report with new values
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
