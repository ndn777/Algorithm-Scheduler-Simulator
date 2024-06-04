//Process class to hold information
//implements Comparable to be used in a priority queue
public class Process implements Comparable<Process> {
    private int id;
    private int arrivalTime;
    private int serviceTime;
    private int ioTime; //length of io time (1 second per io instance)
    private int [] ioActivity; //holds times when io activity occurs
    private int runningTime = 0;
    private int finishTime = -1;
    private int responseTime = -1;
    private int blockTime = 0; //time when io activity occurs
    private double priority; //priority to sort processes in a priority queue
    private boolean isHRRN = false;
    
    //Constructor
    public Process(int id, int arrivalTime, int serviceTime, int ioTime, int [] ioActivity, double priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.ioTime = ioTime;
        this.ioActivity = ioActivity;
        this.priority = priority;
    }
    
    //Getter methods
    public int getId(){
        return id;
    }
    
    public int getArrivalTime() {
        return arrivalTime;
    }
    
    public int getServiceTime() {
        return serviceTime;
    }
    
    public int getIoTime() {
        return ioTime;
    }
    
    //Returns first element
    public int getIoActivity() {
        return ioActivity[0];
    }
    
    public int getRunningTime(){
        return runningTime;
    }
    
    public int getFinishTime() {
        return finishTime;
    }
    
    public int getResponseTime() {
        return responseTime;
    }
    
    public int getBlockTime(){
        return blockTime;
    }
    
    public double getPriority(){
        return priority;
    }
    
    public boolean getHRRN() {
        return isHRRN;
    }
    
    //Setter methods
    public void setFinishTime(int time){
        finishTime = time;
    }
    
    public void setResponseTime(int time){
        responseTime = time;
    }
    
    public void setPriority(double p) {
        priority = p;
    }
    
    //true if using HRRN alg
    public void setHRRN(boolean bool) {
        isHRRN = bool;
    }
    
    //Adds one second of runtime when a process executes
    public void addRunTime() {
        runningTime++;
    }
    
    //Removes one second of runtime
    public void removeRunTime() {
        runningTime--;
    }
    
    //Runs when io activity occurs
    //Removes first element and moves each element forward one space
    public void incrementIoActivity(int time) {
        if (ioTime == 3) {
            ioActivity[0] = ioActivity[1];
            ioActivity[1] = ioActivity[2];
        }
        if (ioTime == 2) {
            ioActivity[0] = ioActivity[1];
        }
        ioTime--;
        blockTime = time; //log block time
    }
    
    //compareTo statement for a priority queue
    @Override
    public int compareTo(Process p) {
        //Compares priority values
        double pCompare = Double.compare(priority, p.getPriority());
        if (pCompare != 0) {
            //if HRRN, then sort descending to get the highest response ratio
            if (isHRRN) {
                pCompare *= -1;
            }
            if (pCompare < 0) {
                return -1;
            }
            if (pCompare > 0) {
                return 1;
            }
        }
        //if priority values are the same, then sort by arrival time
        //if HRRN, then sort by service time
        if (isHRRN) {
            return Integer.compare(serviceTime, p.getServiceTime());
        }
        return Integer.compare(arrivalTime, p.getArrivalTime());
    }
}