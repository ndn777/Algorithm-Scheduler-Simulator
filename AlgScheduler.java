/******************************************************************************
Nghia Nguyen
03/25/23
*******************************************************************************/
import java.io.*;
import java.util.Scanner;

public class AlgScheduler
{
    public static void main(String[] args)
    {
        //Asks for user input to select algorithm type then runs that algorithm
        //FCFS and RR are combined into one class
        switch(getAlgNum()) {
            case 1: //FCFS 
                FCFSRR alg1 = new FCFSRR("FCFS");
                alg1.runFCFSRR();
                break;
            case 2: //RR
                FCFSRR alg2 = new FCFSRR("RR");
                alg2.runFCFSRR();
                break;
            case 3: //SRT
                SRT alg3 = new SRT();
                alg3.runSRT();
                break;
            case 4: //HRRN
                HRRN alg4 = new HRRN();
                alg4.runHRRN();
                break;
            default: 
                System.out.println("Invalid input.");
        }
    }
    
    //Method to get user input
    static int getAlgNum() {
        Scanner input = new Scanner(System.in);
        boolean validAlg = false;
        int alg = -1; 
        
        //loops until it receives a number from 1-4
        while(validAlg == false) {
            try {
                System.out.println(
                    "Type a number to choose an algorithm to use:" + '\n' +
                    "1: FCFS" + '\n' + "2: Round Robin" + '\n' +  
                    "3: SRT" + '\n' + "4: HRRN");
                    
                alg = input.nextInt();
                
            } catch(Exception e) { 
                System.out.println("Invalid input: Please type a number from 1 to 4.");
                input.nextLine(); 
                continue;
            }
            
            if (alg < 1 || alg > 4) {
                System.out.println("Invalid Input. Please try again.");
                validAlg = false;
            }
            else {
                validAlg = true;
            }
        };
        return alg;
    }
}
