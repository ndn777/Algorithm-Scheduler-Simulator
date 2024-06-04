import java.util.Timer;
import java.util.TimerTask;

//Class to periodically print averages
public class Report{
    private double avgFinishTime = 0;
    private double avgResponseTime = 0;
    private double avgTAT = 0;
    private double TSRatio = 0;
    private double throughput = 0;
    
    //Setter methods
    public void setFinish(double time) {
        avgFinishTime = time;
    }
    
    public void setResponse(double time) {
        avgResponseTime = time;
    }
    public void setTAT(double time) {
        avgTAT = time;
    }
                
    public void setTSRatio(double ratio) {
        TSRatio = ratio;
    }
    
    public void setThroughput(double ratio) {
        throughput = ratio;
    }

    //runs a thread to print averages every minute
    public void startReport() {
        Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {
                    System.out.print("\nCurrent Statistics: " + 
                        "\nAverage Finish Time = ");
                    System.out.format("%.2f", avgFinishTime);
                    System.out.println(" seconds.");
                    
                    System.out.print("Average Response Time = ");
                    System.out.format("%.2f", avgResponseTime);
                    System.out.println(" seconds.");
                    
                    System.out.print("Average Turn Around Time = ");
                    System.out.format("%.2f", avgTAT);
                    System.out.println(" seconds.");
                    
                    System.out.print("Ratio of Turn Around Time to Service Time = ");
                    System.out.format("%.2f", TSRatio);
                    System.out.println(" seconds.");
                    
                    System.out.print("Throughput: ");
                    System.out.format("%.2f", throughput);
                    System.out.println(" processes per second.\n");
                }
            };
            
            //Set timer to execute every minute starting from the first minute
            timer.scheduleAtFixedRate(task, 60000L, 60000L);
    }
}