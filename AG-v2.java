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
    static Queue<pair<Process, Integer>> queue = new LinkedList<>();
    static ArrayList<pair<Process, Integer>> output = new ArrayList<>();
    static ArrayList<ArrayList<Integer>> quantumHistory = new ArrayList<>();
    static ArrayList<Process> dieList = new ArrayList<>();
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

    public static Process nextProcess(Queue<pair<Process, Integer>> queue, int timeNow)     //Extract the next process from the queue
    {
        int i = 0;
        for(pair<Process, Integer> p : queue)
        {
            if(queue.peek().second == 0) queue.poll();
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

    public static Process nextProcess2(Queue<pair<Process, Integer>> queue, int timeNow, Process currentProcess)        //Extract the min Ag process from the queue
    {
        Process _min = new Process();
        Process minProcess = currentProcess;
        _min._AG = Integer.MAX_VALUE;
        int i = 0, minIdx = -1;
        for(pair<Process, Integer> p : queue)
        {
            if(queue.peek().second == 0) queue.poll();
           // if(p.second == 1 && p.first.arrivalTime >= timeNow)
           // {
            if(p.first._AG < _min._AG)
            {
                _min = p.first;
                minIdx = i;
                minProcess = p.first;
            }
            //}
            i++;
        }
        if(currentProcess._AG <=  minProcess._AG) return currentProcess;        //if the current process is the minAG
        if(minIdx == 0 && queue.size() > 0) queue.poll();   //if the process is in the top of the queue, remove it from the queue
        else                            //if the process is inside the queue and for that I can't remove it, so just remark it with 0(as if it's removed from the queue)
        {
            for(pair<Process, Integer> p : queue)       //loop on the queue again to search for the _minProcess to make it's integer 0
            {
                if(minProcess.name == p.first.name)
                {
                    p.second = 0;
                    break;
                }
            }
        }
        return minProcess;
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
    public static void print()  //For debug
    {
        System.out.println("queue");
        for(pair<Process, Integer> p : queue)
        {
            System.out.println(p.first.name + " " + p.second);
        }
    }

    public static void main(String[] args)
    {
        Scanner cin = new Scanner(System.in);

        System.out.println("Enter the number of processes");
        numOfProcesses =  cin.nextInt();
        System.out.println("Enter the Quantum");
        quantum = cin.nextInt();

        int totalSim = 0;
        System.out.println("Enter the information of the processes");
        for(int i = 0 ; i<numOfProcesses ; i++)
        {
            Scanner in = new Scanner(System.in);
            Process p = new Process();

            String name = in.next();           p.name = name;
           // String color = in.nextLine();           p.color = color;
            int burst = in.nextInt();               p.burstTime = burst;    totalSim += burst;
            int arrival = in.nextInt();                p.arrivalTime = arrival;
            int priority = in.nextInt();               p.priority = priority;
            p.processQuantum = quantum;
            p.index = i;
            p._AG = p.priority + p.arrivalTime + p.burstTime;
            processes.add(p);
        }
        int time = 0, index = 1, queueIDX = 1;
        Process runningProcess = processes.get(0);      //active the first process
















        for (int i = 0 ; i<=totalSim ; i++)      //Core implementation
        {
            if(queueIDX < numOfProcesses && i >= processes.get(queueIDX).arrivalTime)    //if process arrive, add it to queue
            {
                System.out.println("Enter at " + i);
                pair<Process, Integer> temp2 = new pair(processes.get(queueIDX++), 1);
                queue.add(temp2);
            }

            if(i == totalSim-1)
            {
                break;                  //if this is the last second, no need to check for the 3 below cases
            }
            if(runningProcess.burstTime == 0)                                                                    //case 3 : The process finished it's job
            {
                dieList.add(runningProcess);
                runningProcess = nextProcess(queue, i);
                if(runningProcess == null) System.out.println("No processes in the queue");
            }




            ////////////////The 3 cases
            else if(time >= runningProcess.processQuantum  && time >= processes.get(index).arrivalTime)          //case 1 : The process used all it's quantum
            {
                if(runningProcess.burstTime > 0) //The process still have job to do, this condition I can ignore as the process will not enter here if it's finished
                {
                    pair temp3 = new pair(runningProcess, 10);
                    queue.add(temp3);                                            //step 1 : add the process to the end of the queue
                    runningProcess.processQuantum += quantumCalc(processes);    //step 2 : then increases it's Quantum time by (ceil(10% of the (mean of Quantum)))
                    runningProcess = nextProcess(queue, i);              //step 3 : next process in the queue will be active  //make it's function don't forget //if there is no process arrive yet, then this process will continue
                    if(runningProcess == null) System.out.println("erorr : unexpected null");
                }
                time = 0;                                                       //restart the timer for the new process
                index = runningProcess.index;                                   //update the index by the index of the new process
            }
            else if(queue.size() > 0 && time >= (int)((double)Math.ceil(runningProcess.processQuantum/2)))     //case2 : The process didn’t use all its quantum time
            {
                Process tempProcess = nextProcess2(queue, i, runningProcess);
                if(tempProcess.name != runningProcess.name)     //To handle the case if the running process is already the minAG process.
                {
                    pair<Process, Integer> temp4 = new pair(runningProcess, 1);
                    queue.add(temp4);
                    runningProcess.processQuantum += runningProcess.processQuantum - time;
                    runningProcess = tempProcess;
                    time = 0;
                    index = runningProcess.index;
                }
            }

            ////////////////////////////////////////////////Precess running
            System.out.println("running process : " + runningProcess.name);
            print();
            //System.out.println("process at queueIDX = " + queueIDX + " : " + processes.get(queueIDX).name);
            System.out.println(" ");

            ArrayList<Integer> temp = addQuantumHistory(processes);
            quantumHistory.add(temp);
            time++;
            runningProcess.burstTime--;
            //System.out.println(output.size());
            if(output.size() == 0 || runningProcess.name != output.get(output.size() - 1).first.name)       //Add the process to the output whenever it's Enter or if this is the first process in the simultion
            {
                pair<Process, Integer> temp0 = new pair(runningProcess, i);
                output.add(temp0);
            }
        }
        for(pair<Process, Integer> p : output)
        {
            System.out.println(p.first.name + p.second);
        }
    }
}

/*
p1
17
0
4
p2
6
3
9
p3
10
4
3
p4
4
29
8
*/
