# Process Scheduling Simulation

## Execution

Simply run `make` to build and run the tests. Follow the prompts for keyboard input. Output displays to the terminal.

You will get a choice of a couple of pre-set input processes or randomly-generated processes. The program will display the processes after the selection is made. You will then be prompted for the algorithm to simulate scheduling of the selected input processes. Following that, a trace log of the system will be printed out, followed by a detailed analysis.

# Discussion of Algorithms

## FCFS

The FCFS algorithm has no possibility of starving processes due to the fact that all processes will eventually get run. Additionally, this algorithm has the lowest context switching overhead because there is no preemption. However, the FCFS algorithm tends to be extremely unfair, as long processes can make much shorter processes wait.

To improve the FCFS algorithm, we can preempt each process at a specific time slice. This way, longer processes don't take up all the time, and it would make it more fair for shorter processes that arrive later.

## Round Robin

Similarly to FCFS, there is no possibility of starvation in Round Robin because processes are chosen in a FCFS method, meaning that all processes will get to run. However, there is slightly higher context switching overhead due to the fact that the Round Robin method will perform context switches at specified intervals. Although there might be more context switching, the tradeoff is that Round Robin is significantly more fair than the FCFS algorithm due to each process being limited to a specific time quantum to run.

A way to improve the round robin algorithm is by also factoring in process service time. This would allow for shorter processes to get finished first before longer processes. However, processes that have been in the system for too long (give a set amount of time based on priority) should be run first.

## SRT

In SRT, there IS the possibility of starvation of longer processes. It is possible that a process can be starved because SRT will simply pick processes that are shorter. The context switching overhead also has the potential to be massive due to the fact that shorter processes might be continually added to the ready queue. The fairness is not so good because, again, shorter processes are prioritized and longer processes might be indefinitely starved.

Since starvation is possible, we simply need to factor in waiting time (as I mentioned above). This would allow the SRT algorithm to take into consideration longer processes.

## HRRN

This algorithm removes the possibility of starvation due to the fact that waiting time is factored into the decision algorithm. However, the context switching overhead can also be high because it can be swapping processes pretty frequently. This algorithm is also quite fair as it prioritizes shorter processes that have been waiting for longer, while also making sure that all processes eventually run.

An improvement to this algorithm would be a more complex algorithm for determining which process should get priority. Of course, this comes at a trade-off for complexity. This algorithm is actually highly optimized, except the fact that it still uses a queue. That means that it would still require O(n) time to figure out which process has the lowest ratio. The Linux CFS is an optimization on this algorithm because it solves this issue using a red-black tree. This guarantees that the CFS algorithm will always run in O(log n) time.

# References

* [Linux CFS Overview](https://opensource.com/article/19/2/fair-scheduling-linux)
* [Binary Tree Data Structure](https://github.com/SvenWoltmann/binary-tree)

# Additional Notes

* When two processes are in the ready queue and have the same selection priority, the process with a lower PID is defaulted to be selected first
* Each "time slice" represents what happens at the end of the time slice, so Process A starting at t=0 and ending at t=6 has a service time of 6, if it has no I/O and is the only process
* Processes that need to block for I/O will block at even intervals, rounded down to the integer (this means that process E would block at execution times of 1 and 2 instead of 2 and 3)
* The Linux CFS algorithm assumes all processes have a nice value of 0, for simplicity of scheduling
