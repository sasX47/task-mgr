# Task Manager
 Sample Task Processor

- Startup class is Main Class
- Main class holds the code to generate the tasks and task group and submit the tasks.

### Modifications
- All given methods are put into separate classes.
- TaskAction in Task Record is wrapped with TaskAction Abstract class, adding the capability to add-on observers.

### Assumption
- The tasks are added in queue FIFO fashion and submitted for execution in same way, i.e. if any task T1 added before task T2, and if T1 cannot be executed at the moment, then T2 will also wait.
- If Task T1 and T2 belongs to same task group TG1, then if T1 is running currently, T2 will wait as well as all other tasks behind T2 in the queue.
- Above 2 feature gurantees, `The first task submitted must be the first task started` and `Tasks sharing the same TaskGroup must not run concurrently`.
- Task execution is designed as, obtaining random integer between 50ms tp 5000ms and wait for that much time. Then generate a string with thread name and the wait time. This string acts as output of the task.
- Task Manager class contains the Queue and logic to maintain the queue and adding and executing tasks.
