public class Process implements Comparable<Process> {
    public static int pidCounter = 0;

    public int pid;
    public int arrivalTime;
    public int serviceTime;
    public int totalDiskIoTime;
    public int diskIoActivity;
    public int diskInterval;
    public int diskServiceTime;
    public int runningTime;
    public int virtualRunningTime;
    public int waitingTime;
    public int lastDiskEntrance;
    public int startTime;
    public int finishTime;

    public Process() {
        this.arrivalTime = -1;
        this.serviceTime = -1;
        this.totalDiskIoTime = 0;
        this.diskIoActivity = 0;
        this.diskInterval = 0;
        this.diskServiceTime = 0;
        this.runningTime = 0;
        this.virtualRunningTime = 0;
        this.startTime = -1;
        this.finishTime = -1;
        this.pid = -1;
    }

    public Process(int arrivalTime, int serviceTime, int totalDiskIoTime, int diskIoActivity) {
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.totalDiskIoTime = totalDiskIoTime;
        this.diskIoActivity = diskIoActivity;
        this.diskInterval = serviceTime / (diskIoActivity + 1);
        this.diskServiceTime = diskIoActivity == 0 ? 0 : totalDiskIoTime / diskIoActivity;
        this.runningTime = 0;
        this.virtualRunningTime = 0;
        this.startTime = -1;
        this.finishTime = 0;
        this.pid = pidCounter++;
    }

    public int getPid() {
        return pid;
    }

    public char getCharPid() {
        return (char) ('A' + pid);
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    @Override
    public String toString() {
        return ((char) ('A' + pid)) + "(" + waitingTime + "/" + runningTime + ")";
    }

    @Override
    public int compareTo(Process o) {
        if (virtualRunningTime == o.virtualRunningTime)
            return 0;
        else
            return virtualRunningTime - o.virtualRunningTime;
    }
}
