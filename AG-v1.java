import com.sun.jdi.ObjectCollectedException;
import javafx.util.Pair;

import javax.print.attribute.IntegerSyntax;
import java.lang.reflect.Array;
import java.lang.Math;
import java.util.*;

public class AG {

    static class pair<T, C>
    {
        T first;
        C second;
        pair(){this.first = null; this.second = null;}

        pair(T first, C second)
        {
            this.first = first;
            this.second = second;
        }
    }
    static class Process
    {
        String name;
        String color;
        int arrivalTime;
        int burstTime;
        int priority;
        int processQuantum;
        int index;
        int _AG;

        Process() {}
        Process(String name, String color, int arrivalTime, int burstTime, int priority, int processQuantum)
        {
            this.name = name;
            this.color = color;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.priority = priority;
            this.processQuantum = processQuantum;
            _AG = 0;
        }
    }

    static int numOfProcesses;
    static int quantum;
    static int nextArrival;
    static int totalSimTime;

    //static Map<Process, Integer> basedOnArrival = new HashMap<Process, Integer>();
   // static ArrayList<pair> basedOnArrival = new ArrayList<pair>();
    static ArrayList<Process> processes = new ArrayList<>();
    static Queue<pair> queue = new LinkedList<>();
    static ArrayList<ArrayList<Integer>> quantumHistory = new ArrayList<>();
    AG()
    {
        numOfProcesses = 0;
        quantum = 0;
        nextArrival = 0;
        totalSimTime = 0;
    }

    AG(int numOfProcesses, int quantum, int nextArrival, int totalSimTime)
    {
        this.numOfProcesses = numOfProcesses;
        this.quantum = quantum;
        this.nextArrival = nextArrival;
        this.totalSimTime = totalSimTime;
    }

    public static int quantumCalc(ArrayList<Process> arr)
    {
        double sum = 0;
        for(Process p : arr)
        {
            sum += p.processQuantum;
        }
        sum /= arr.size();              //Mean of Quantum
        sum = Math.ceil(sum/10);        //ceil (10% of the Mean)
        return (int)sum;
    }

    public static Process nextProcess(Queue<pair> queue, int timeNow)
    {
        int i = 0;
        for(pair<Process, Integer> p : queue)
        {
            if (p.second == 1 && p.first.arrivalTime >= timeNow)
            {
                if(i == 0) queue.poll();        //if the process is in the top of the queue, remove it from the queue
                else p.second = 0;              //if the process is inside the queue and for that I can't remove it, so just remark it with 0(as if it's removed from the queue)
                return p.first;
            }
            i++;
        }
        return null;
    }

    public static Process nextProcess2(Queue<pair> queue, int timeNow)
    {
        int _min = Integer.MAX_VALUE;
        int i = 0;
        for(pair<Process, Integer> p : queue)
        {
            if(p.second == 1 && p.first.arrivalTime >= timeNow)
            {
                if(p.first._AG < _min)
                {
                    if(i == 0) queue.poll();        //if the process is in the top of the queue, remove it from the queue
                    else p.second = 0;              //if the process is inside the queue and for that I can't remove it, so just remark it with 0(as if it's removed from the queue)
                    return p.first;
                }
            }
            i++;
        }
        return null;
    }
    public static ArrayList<Integer> addQuantumHistory(ArrayList<Process> processes)
    {
        ArrayList<Integer> temp = new ArrayList<>();
        for(Process p : processes)
        {
            temp.add(p.processQuantum);
        }
        return temp;
    }
    public static void main(String[] args)
    {
        Scanner cin = new Scanner(System.in);

        System.out.println("Enter the number of processes");
        numOfProcesses =  cin.nextInt();
        System.out.println("Enter the Quantum");
        quantum = cin.nextInt();

        int totalSim = 0;
        for(int i = 0 ; i<numOfProcesses ; i++)
        {
            Scanner in = new Scanner(System.in);
            Process p = new Process();
            System.out.println("Enter the information of the process");

            String name = in.nextLine();           p.name = name;
            String color = in.nextLine();           p.color = color;
            int arrival = in.nextInt();                p.arrivalTime = arrival;
            int burst = in.nextInt();               p.burstTime = burst;    totalSim += burst;
            int priority = in.nextInt();               p.priority = priority;
            int quantum = in.nextInt();               p.processQuantum = quantum;
            int idx = in.nextInt();                 p.index = i;
            p._AG = p.priority + p.arrivalTime + p.burstTime;
           // pair temp = new pair(p, p.arrivalTime);
           // basedOnArrival.add(temp);
            processes.add(p);
            pair<Process, Integer> temp2 = new pair(p, 1);
            queue.add(temp2);     //Add this process to queue;
        }
        int time = 0, index = 1;
        Process runningProcess = (Process) queue.peek().first; queue.poll();
        System.out.println(runningProcess.name);

        for (int i = 0 ; i<totalSim ; i++)      //Core implementation
        {
            ArrayList<Integer> temp = addQuantumHistory(processes);
            quantumHistory.add(temp);
            time++;
            runningProcess.burstTime--;
            if(runningProcess.burstTime == 0)
            {
                runningProcess = nextProcess(queue, i);
            }
            else if(time >= runningProcess.processQuantum  && time >= processes.get(index).arrivalTime)          //case 1 : The process used all it's quantum
            {
                if(runningProcess.burstTime > 0) //The process still have job to do, this condition I can ignore as the process will not enter here if it's finished
                {
                    pair temp3 = new pair(runningProcess, 1);
                    queue.add(temp3);                                            //step 1 : add the process to the end of the queue
                    runningProcess.processQuantum += quantumCalc(processes);    //step 2 : then increases it's Quantum time by (ceil(10% of the (mean of Quantum)))
                    runningProcess = nextProcess(queue, i);              //step 3 : next process in the queue will be active  //make it's function don't forget //if there is no process arrive yet, then this process will continue
                }
                time = 0;                                                       //restart the timer for the new process
                index = runningProcess.index;                                   //update the index by the index of the new process
            }
            else if(time >= runningProcess.processQuantum/2 && time >= processes.get(index).arrivalTime)     //case2 : The process didn’t use all its quantum time
            {
                pair<Process, Integer> temp4 = new pair(runningProcess, 1);
                queue.add(temp4);
                runningProcess.processQuantum = runningProcess.processQuantum - time;
                runningProcess = nextProcess2(queue, i);
                time = 0;
                index = runningProcess.index;
            }
        }
    }

}

//Where I poll the queue
