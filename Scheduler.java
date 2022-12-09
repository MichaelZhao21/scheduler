import java.util.*;

public class Scheduler {
    // Config for random processes
    public static final int MIN_SERVICE_TIME = 20;
    public static final int MAX_SERVICE_TIME = 40;
    public static final int MIN_ARRIVAL_TIME = 0;
    public static final int MAX_ARRIVAL_TIME = 100;
    public static final int MIN_TOTAL_IO_TIME = 0;
    public static final int MAX_TOTAL_IO_TIME = 20;
    public static final int MIN_IO_ACTIVITY = 0;
    public static final int MAX_IO_ACTIVITY = 5;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Random random = new Random();

        // Print initial prompt
        System.out.println("\n===========================");
        System.out.println("Process Scheduler Simulator");
        System.out.println("===========================");
        System.out.println("The simulator program will run each algorithm based on the selected input processes");
        System.out.println(
                "and the selected algorithm. The first to display will be the list of processes. Following that,");
        System.out.println("a run log of the system will be displayed, with the following format:");
        System.out.println("<time slice>: <running process> | [ready queue] | [blocked queue]");
        System.out.println("Each process is printed in the form: <PID>(<waiting time>/<execution time>)");
        System.out.println("The last table to print is the full analysis of the program, including the");
        System.out.println("Start Time, Finish Time, Response Time, TAT, TAT:Service Time, and Throughput");
        System.out.println("===========================");

        // Get choice of random or preset inputs
        int inputChoice = -1;
        while (inputChoice != 1 && inputChoice != 2) {
            System.out.print("\nDo you want to use preset processes (1) or random processes (2)? ");
            inputChoice = scan.nextInt();
        }

        // Create set list of processes
        int[][] processNumbers;
        if (inputChoice == 1) {
            // Preset processes
            processNumbers = new int[][] {
                    { 0, 6, 1, 1 },
                    { 2, 12, 2, 2 },
                    { 4, 8, 1, 1 },
                    { 6, 10, 0, 0 },
                    { 8, 4, 2, 2 }
            };
        } else {
            // Generate random processes
            processNumbers = new int[20][4];
            for (int i = 0; i < 20; i++) {
                processNumbers[i][0] = randInt(random, MIN_ARRIVAL_TIME, MAX_ARRIVAL_TIME);
                processNumbers[i][1] = randInt(random, MIN_SERVICE_TIME, MAX_SERVICE_TIME);
                processNumbers[i][2] = randInt(random, MIN_TOTAL_IO_TIME, MAX_TOTAL_IO_TIME);
                processNumbers[i][3] = randInt(random, MIN_IO_ACTIVITY, MAX_IO_ACTIVITY);
            }
        }

        // Create list of Process objects from generated numbers
        List<Process> processList = new LinkedList<>();
        for (int[] nl : processNumbers) {
            processList.add(new Process(nl[0], nl[1], nl[2], nl[3]));
        }

        // Print out list of processes
        System.out.println("\n===================");
        System.out.println("Process List");
        System.out.println("===================");
        System.out.printf("%3s%4s%4s%4s%4s\n", "PID", "AT", "ST", "DIO", "DA");
        for (Process p : processList) {
            System.out.printf("%3c%4d%4d%4d%4d\n", p.getCharPid(), p.arrivalTime, p.serviceTime, p.totalDiskIoTime,
                    p.diskIoActivity);
        }
        System.out.println("===================");
        System.out.println("PID = Process ID, AT = Arrival Time, ST = Service Time, DIO = Total Disk I/O Time,");
        System.out.println("DA = Disk I/O Activity (At equal intervals during service time)");

        // Present the user with a menu of which algorithm to run
        System.out.println("\nWhich algorithm would you like to run?");
        System.out.println("\t1. FCFS\n\t2. Round Robin\n\t3. SRT\n\t4. HRRN\n\t5. Linux CFS");

        // Get the user's choice
        inputChoice = -1;
        while (inputChoice < 1 || inputChoice > 5) {
            System.out.print("Enter your choice (1-5): ");
            inputChoice = scan.nextInt();
        }
        System.out.println();

        // Run algorithm based on the selected choice
        List<Process> finishedList = null;
        switch (inputChoice) {
            case 1:
                finishedList = FCFS(processList);
                break;
            case 2:
                finishedList = roundRobin(processList);
                break;
            case 3:
                finishedList = SRT(processList);
                break;
            case 4:
                finishedList = HRRN(processList);
                break;
            case 5:
                finishedList = linux(processList);
                break;
            default:
                System.err.println("Uh oh");
                System.exit(1);
        }

        // Sort & Print List
        if (finishedList != null) {
            finishedList.sort(Comparator.comparing(Process::getPid));
            printFinishedList(finishedList, inputChoice);
        }

        // Close scanner
        scan.close();
    }

    public static int randInt(Random random, int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static void printFinishedList(List<Process> finishedList, int choice) {
        // ST = start, FT = finish, RT = response time (start - arrival), TAT = turn
        // around time (waiting + running), T/S = TAT:ST
        // Throughput = final time / # of processes
        double totalTat = 0;
        double totalTs = 0;
        int maxFinishTime = 0;
        System.out.println("\n=================================");
        System.out.println("Analysis for " + choiceToAlgoString(choice) + " Algorithm");
        System.out.println("=================================");
        System.out.printf("%3s%4s%4s%4s%4s%4s%4s%6s", "PID", "S", "F", "ST", "WT", "RT", "TAT", "T/S");
        for (Process p : finishedList) {
            System.out.printf("\n%3c%4d%4d%4d%4d%4d%4d%6.2f", p.getCharPid(), p.startTime, p.finishTime,
                    p.serviceTime, p.waitingTime, p.startTime - p.arrivalTime, p.waitingTime + p.runningTime,
                    1.0 * (p.waitingTime + p.runningTime) / p.serviceTime);
            totalTat += p.waitingTime + p.runningTime;
            totalTs += 1.0 * (p.waitingTime + p.runningTime) / p.serviceTime;
            if (p.finishTime > maxFinishTime) {
                maxFinishTime = p.finishTime;
            }
        }
        System.out.println("\n=================================");
        System.out.printf("Average TAT: %.2f\n", (totalTat / finishedList.size()));
        System.out.printf("Average T/S: %.2f\n", (totalTs / finishedList.size()));
        System.out.printf("Throughput: %.2f\n", 1.0 * finishedList.size() / maxFinishTime);
        System.out.println("=================================");
        System.out.println("PID = Process ID, S = Start Time, F = Finish Time, ST = Service Time,");
        System.out.println("WT = Waiting Time, RT = Response Time, TAT = Turn Around Time, T/S = TAT/Service Time\n");
    }

    public static String choiceToAlgoString(int choice) {
        switch (choice) {
            case 1:
                return "FCFS";
            case 2:
                return "Round Robin";
            case 3:
                return "SRT";
            case 4:
                return "HRRT";
            case 5:
                return "Linux CFS";
            default:
                return "[INVALID]";
        }
    }

    /**
     * Prints clock: <running process> | [ready queue] | [blocked queue]
     */
    public static void printRunningSlice(int clock, Process running, Iterable<Process> readyQueue,
            List<Process> blockedQueue) {
        System.out.println(clock + ": " + running + " | " + readyQueue + " | " + blockedQueue);
    }

    public static void printRunningSlice(int clock, Process running, RedBlackTree readyQueue, List<Process> blockedQueue) {
        System.out.println(clock + ": " + running + " | " + readyQueue + " | " + blockedQueue);
    }

    public static List<Process> FCFS(List<Process> processList) {
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(10,
                Comparator.comparingInt(Process::getWaitingTime).reversed());
        LinkedList<Process> blockedQueue = new LinkedList<>();
        LinkedList<Process> finishedList = new LinkedList<>();
        Process running = null;

        int clock = 0;
        while (!processList.isEmpty() || !readyQueue.isEmpty() || !blockedQueue.isEmpty() || running != null) {
            // Check to see if any blocked process can be put back into ready queue
            for (int i = 0; i < blockedQueue.size(); i++) {
                Process p = blockedQueue.get(i);
                if (p.lastDiskEntrance + p.diskServiceTime <= clock) {
                    Process bp = blockedQueue.remove(i);
                    readyQueue.add(bp);
                    i--;
                }
            }

            // Add next process to the ready queue if we are at its arrival time
            // Assume that no 2 processes have the same arrival time
            if (!processList.isEmpty()) {
                Process nextProcess = processList.get(0);
                if (nextProcess.arrivalTime <= clock) {
                    Process p = processList.remove(0);
                    readyQueue.add(p);
                }
            }

            // If the current running process is done, push it into the finishedList
            if (running != null && running.runningTime == running.serviceTime) {
                running.finishTime = clock;
                finishedList.add(running);
                running = null;
            }

            // If the current running process is blocked for I/O, move it to the blocked
            // queue
            if (running != null && running.runningTime % running.diskInterval == 0) {
                blockedQueue.add(running);
                running = null;
            }

            // If there is no currently running process, run the next process in the
            // readyQueue
            if (running == null && !readyQueue.isEmpty()) {
                running = readyQueue.poll();
                if (running.startTime == -1) {
                    running.startTime = clock;
                }
            }

            // Print current clock tick data
            printRunningSlice(clock, running, readyQueue, blockedQueue);

            // Clock tick
            clock++;

            // Increase time in CPU for the current running process
            if (running != null) {
                running.runningTime++;
            }

            // Increase time in waiting queue
            for (Process p : readyQueue) {
                p.waitingTime++;
            }
        }
        return finishedList;
    }

    public static List<Process> roundRobin(List<Process> processList) {
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(10,
                Comparator.comparingInt(Process::getWaitingTime).reversed());
        LinkedList<Process> blockedQueue = new LinkedList<>();
        LinkedList<Process> finishedList = new LinkedList<>();
        Process running = null;

        int clock = 0;
        int currentQuantum = 0;
        while (!processList.isEmpty() || !readyQueue.isEmpty() || !blockedQueue.isEmpty() || running != null) {
            // Check to see if any blocked process can be put back into ready queue
            for (int i = 0; i < blockedQueue.size(); i++) {
                Process p = blockedQueue.get(i);
                if (p.lastDiskEntrance + p.diskServiceTime <= clock) {
                    Process bp = blockedQueue.remove(i);
                    readyQueue.add(bp);
                    i--;
                }
            }

            // Add next process to the ready queue if we are at its arrival time
            // Assume that no 2 processes have the same arrival time
            if (!processList.isEmpty()) {
                Process nextProcess = processList.get(0);
                if (nextProcess.arrivalTime <= clock) {
                    Process p = processList.remove(0);
                    readyQueue.add(p);
                }
            }

            // If the current running process is done, push it into the finishedList
            if (running != null && running.runningTime == running.serviceTime) {
                running.finishTime = clock;
                finishedList.add(running);
                currentQuantum = 0;
                running = null;
            }

            // If the current running process is blocked for I/O, move it to the blocked
            // queue
            if (running != null && running.runningTime % running.diskInterval == 0) {
                blockedQueue.add(running);
                currentQuantum = 0;
                running = null;
            }

            // If we have reached the end of the current time slice,
            // preempt the current process
            if (currentQuantum == 4) {
                currentQuantum = 0;
                readyQueue.add(running);
                running = null;
            }

            // If there is no currently running process, run the next process in the
            // readyQueue
            if (running == null && !readyQueue.isEmpty()) {
                running = readyQueue.poll();
                if (running.startTime == -1) {
                    running.startTime = clock;
                }
            }

            // Print current clock tick data
            printRunningSlice(clock, running, readyQueue, blockedQueue);

            // Clock tick
            clock++;

            // Increase current quantum
            if (running != null) {
                currentQuantum++;
            }

            // Increase time in CPU for the current running process
            if (running != null) {
                running.runningTime++;
            }

            // Increase time in waiting queue
            for (Process p : readyQueue) {
                p.waitingTime++;
            }
        }
        return finishedList;
    }

    public static List<Process> SRT(List<Process> processList) {
        List<Process> readyQueue = new LinkedList<>();
        LinkedList<Process> blockedQueue = new LinkedList<>();
        LinkedList<Process> finishedList = new LinkedList<>();
        Process running = null;

        int clock = 0;
        while (!processList.isEmpty() || !readyQueue.isEmpty() || !blockedQueue.isEmpty() || running != null) {
            // Check to see if any blocked process can be put back into ready queue
            for (int i = 0; i < blockedQueue.size(); i++) {
                Process p = blockedQueue.get(i);
                if (p.lastDiskEntrance + p.diskServiceTime <= clock) {
                    Process bp = blockedQueue.remove(i);
                    readyQueue.add(bp);
                    i--;
                }
            }

            // Add next process to the ready queue if we are at its arrival time
            // Assume that no 2 processes have the same arrival time
            if (!processList.isEmpty()) {
                Process nextProcess = processList.get(0);
                if (nextProcess.arrivalTime <= clock) {
                    Process p = processList.remove(0);
                    readyQueue.add(p);
                }
            }

            // If the current running process is done, push it into the finishedList
            if (running != null && running.runningTime == running.serviceTime) {
                running.finishTime = clock;
                finishedList.add(running);
                running = null;
            }

            // If the current running process is blocked for I/O, move it to the blocked
            // queue
            if (running != null && running.runningTime % running.diskInterval == 0) {
                blockedQueue.add(running);
                running = null;
            }

            // If there is a shorter process than the current process, preempt the current
            // process and start the shorter process
            // If there is no running process, find the shortest
            int shortest = running != null ? running.serviceTime - running.runningTime : Integer.MAX_VALUE;
            int shortestId = -1;
            for (int i = 0; i < readyQueue.size(); i++) {
                Process curr = readyQueue.get(i);
                if (curr.serviceTime - curr.runningTime < shortest) {
                    shortest = curr.serviceTime - curr.runningTime;
                    shortestId = i;
                }
            }
            if (shortestId != -1) {
                if (running != null) {
                    readyQueue.add(running);
                }
                Process p = readyQueue.remove(shortestId);
                running = p;
                if (running.startTime == -1) {
                    running.startTime = clock;
                }
            }

            // Print current clock tick data
            printRunningSlice(clock, running, readyQueue, blockedQueue);

            // Clock tick
            clock++;

            // Increase time in CPU for the current running process
            if (running != null) {
                running.runningTime++;
            }

            // Increase time in waiting queue
            for (Process p : readyQueue) {
                p.waitingTime++;
            }
        }
        return finishedList;
    }

    public static List<Process> HRRN(List<Process> processList) {
        List<Process> readyQueue = new LinkedList<>();
        LinkedList<Process> blockedQueue = new LinkedList<>();
        LinkedList<Process> finishedList = new LinkedList<>();
        Process running = null;

        int clock = 0;
        while (!processList.isEmpty() || !readyQueue.isEmpty() || !blockedQueue.isEmpty() || running != null) {
            // Check to see if any blocked process can be put back into ready queue
            for (int i = 0; i < blockedQueue.size(); i++) {
                Process p = blockedQueue.get(i);
                if (p.lastDiskEntrance + p.diskServiceTime <= clock) {
                    Process bp = blockedQueue.remove(i);
                    readyQueue.add(bp);
                    i--;
                }
            }

            // Add next process to the ready queue if we are at its arrival time
            // Assume that no 2 processes have the same arrival time
            if (!processList.isEmpty()) {
                Process nextProcess = processList.get(0);
                if (nextProcess.arrivalTime <= clock) {
                    Process p = processList.remove(0);
                    readyQueue.add(p);
                }
            }

            // If the current running process is done, push it into the finishedList
            if (running != null && running.runningTime == running.serviceTime) {
                running.finishTime = clock;
                finishedList.add(running);
                running = null;
            }

            // If the current running process is blocked for I/O, move it to the blocked
            // queue
            if (running != null && running.runningTime % running.diskInterval == 0) {
                blockedQueue.add(running);
                running = null;
            }

            // If there is no running process, find the process with the
            // highest value of R = (w + s) / s
            if (running == null) {
                double highestVal = 0;
                int highestId = -1;
                for (int i = 0; i < readyQueue.size(); i++) {
                    Process curr = readyQueue.get(i);
                    double val = 1.0 * (curr.waitingTime + curr.serviceTime) / curr.serviceTime;
                    if (val > highestVal) {
                        highestVal = val;
                        highestId = i;
                    }
                }
                if (highestId != -1) {
                    Process p = readyQueue.remove(highestId);
                    running = p;
                    if (running.startTime == -1) {
                        running.startTime = clock;
                    }
                }
            }

            // Print current clock tick data
            printRunningSlice(clock, running, readyQueue, blockedQueue);

            // Clock tick
            clock++;

            // Increase time in CPU for the current running process
            if (running != null) {
                running.runningTime++;
            }

            // Increase time in waiting queue
            for (Process p : readyQueue) {
                p.waitingTime++;
            }
        }
        return finishedList;
    }

    public static List<Process> linux(List<Process> processList) {
        RedBlackTree readyQueue = new RedBlackTree();
        LinkedList<Process> blockedQueue = new LinkedList<>();
        LinkedList<Process> finishedList = new LinkedList<>();
        Process running = null;

        int clock = 0;
        int currentQuantum = 0;
        while (!processList.isEmpty() || readyQueue.root != null || !blockedQueue.isEmpty() || running != null) {
            // Check to see if any blocked process can be put back into ready queue
            for (int i = 0; i < blockedQueue.size(); i++) {
                Process p = blockedQueue.get(i);
                if (p.lastDiskEntrance + p.diskServiceTime <= clock) {
                    Process bp = blockedQueue.remove(i);
                    readyQueue.insertNode(bp);
                    i--;
                }
            }

            // Add next process to the ready queue if we are at its arrival time
            // Assume that no 2 processes have the same arrival time
            if (!processList.isEmpty()) {
                Process nextProcess = processList.get(0);
                if (nextProcess.arrivalTime <= clock) {
                    Process p = processList.remove(0);
                    readyQueue.insertNode(p);
                }
            }

            // If the current running process is done, push it into the finishedList
            if (running != null && running.runningTime == running.serviceTime) {
                running.finishTime = clock;
                finishedList.add(running);
                currentQuantum = 0;
                running = null;
            }

            // If the current running process is blocked for I/O, move it to the blocked
            // queue
            if (running != null && running.runningTime % running.diskInterval == 0) {
                blockedQueue.add(running);
                currentQuantum = 0;
                running = null;
            }

            // If the current process has exceeded its time quanntum
            // preempt it and start the next process
            if (currentQuantum == 12) {
                currentQuantum = 0;
                readyQueue.insertNode(running);
                running = null;
            }

            // If there is no currently running process, run the next process in the
            // readyQueue
            if (running == null && readyQueue.root != null) {
                running = readyQueue.deleteLeftmost();
                currentQuantum = 0;
                if (running.startTime == -1) {
                    running.startTime = clock;
                }
            }

            // Print current clock tick data
            printRunningSlice(clock, running, readyQueue, blockedQueue);

            // Clock tick
            clock++;

            // Increase current quantum
            if (running != null) {
                currentQuantum++;
            }

            // Increase time in CPU for the current running process
            if (running != null) {
                running.runningTime++;
                running.virtualRunningTime++;
            }

            // Increase time in waiting queue
            readyQueue.increaseWaitTimeForAllNodes();
        }
        return finishedList;
    }
}
