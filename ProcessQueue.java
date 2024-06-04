import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

//priority queue class to be used with processFinder class and an algorithm
//needs to be thread safe, so it uses synchronized methods
public class ProcessQueue{
    private PriorityQueue<Process> q = new PriorityQueue<>();

    //add an element
    public synchronized void add(Process p) {
        q.add(p);
    }
    
    //removes an element if not null
    public synchronized Process poll() {
        return q.poll();
    }
    
    //returns first element
    public synchronized Process peek() {
        return q.peek();
    }
    
    //checks if queue is empty
    public synchronized boolean isEmpty() {
        return q.isEmpty();
    }
    
    //returns size of queue
    public synchronized int size() {
        return q.size();
    }
}
