import java.util.Random;

//Class to create a queue and runs thread to randomly create new processes
public class ProcessFinder implements Runnable{
    private ProcessQueue pQueue = new ProcessQueue();
    private Random random = new Random(); //for random numbers
    private int counter = 0;        //for sequential id numbers
    private int elapsedTime = 0;    //current time in an algorithm
    
    //run method for thread
    @Override
    public void run() {
        //First 5 pre-made processes 
        pQueue.add(new Process(0,0,6,1,new int[]{3},0));
        pQueue.add(new Process(1,2,12,2,new int[]{4,8},2));
        pQueue.add(new Process(2,4,8,1,new int[]{4},4));
        pQueue.add(new Process(3,6,10,0,null,6));
        pQueue.add(new Process(4,8,4,2,new int[]{1,3},8));
        
        counter = 5;
        
        //loops indefinitely
        while (true) {
            //arbitrarily limits pQueue size to 10 processes
            if (pQueue.size() <= 10) {
                //create new process and add to queue
                Process newProcess = makeProcess();
                pQueue.add(newProcess);
                counter++; //increment for next process id
            }
            //waits if size > 10
            else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } 
    }

    //Method to create a new random process
    public Process makeProcess() {
        int id = counter;
        //Process can arrive any second within the next minute
        int arrivalTime = random.nextInt(60) + elapsedTime;
        //Arbitrary random number between 4 (to account for potential io time)
        //and 11 for a smoother simulation
        int serviceTime = random.nextInt(11) + 4;
        //can have io activity up to 3 times
        int ioTime = random.nextInt(4);
        int [] ioActivity = null;
        //priority value to sort queue based on arrival time
        double priority = arrivalTime;
        
        //randomly sets io times
        if (ioTime > 0) {
            ioActivity = new int[ioTime];
            int highest = 0;                                //value of last io time
            for (int i = 0; i < ioTime; i++) {
                //ensures remaining io time does not exceed service time
                if (highest >= (serviceTime - ioTime)) {    
                    ioActivity[i] = highest+1;
                    highest++;
                }
                //random value between previous io time and (service time - remaining io time)
                else{
                    ioActivity[i] = random.nextInt(serviceTime - ioTime - highest) + highest + 1;
                    highest = ioActivity[i];
                }
            }
        }
        
        //Store information in a process object and returns
        Process newProcess = new Process(id, arrivalTime, serviceTime, ioTime, ioActivity, priority);
        return newProcess;
    }
    
    //syncs time with algorithm
    public void setTime(int time) {
        elapsedTime = time;
    }
    
    //Queue methods
    public synchronized Process remove() {
        return pQueue.poll();
    }
    
    public synchronized boolean isEmpty() {
        return pQueue.isEmpty();
    }
    
    public synchronized Process peek(){
        return pQueue.peek();
    }
}
