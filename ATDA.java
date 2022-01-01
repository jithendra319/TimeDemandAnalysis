import java.util.*;
import java.lang.*;
import java.io.*;

class Task implements Comparable<Task>{
	 double period;
	 double executionTime;
	 double relativeDeadline;
	 static double totalExecutionTime = 0;
	
	public Task(double period, double executionTime, double relativeDeadline){
		this.period = period;
		this.executionTime = executionTime;
		this.relativeDeadline = relativeDeadline;
		totalExecutionTime += executionTime;
	} 	

	public int compareTo(Task compareTask){
		double compareQuantity = ((Task)compareTask).period;
		if(this.period - compareQuantity < 0){
			return -1;
		}else{
			return 1;
		}
	}
}


public class ATDA
{
	public static void main (String[] args) throws java.lang.Exception
	{
        int taskCount;
		int i;
		double totalUtilizationRatio_U = 0;
		double liu_LeylandBound = 0;
		Scanner input = new Scanner(System.in);

		System.out.println("\n<<<<<<<<< Automatic Time Demand Analysis >>>>>>>>>");
		System.out.println("\nEnter the No. of Tasks to be Analyzed : ");
		
		taskCount = input.nextInt();
		Task[] tasks = new Task[taskCount];

		System.out.println("\nEnter the Task Details(Space separated) for "+taskCount+ " Tasks(each Task in a New Line) in the following Format(Brackets excluded): <Period ExecutionTime RelativeDeadline>");

		for(i=0;i<taskCount;i++){
			double period = input.nextDouble();
			double executionTime = input.nextDouble();
			double relativeDeadline = input.nextDouble();
			Task tempTask = new Task(period, executionTime, relativeDeadline);
			tasks[i] = tempTask;
		}

		input.close();

		System.out.println("\nGiven Task Details are : ");
		for(i=0;i<taskCount;i++){
			System.out.println("--->" + tasks[i].period + " "+tasks[i].executionTime + " "+ tasks[i].relativeDeadline);
		}

		Arrays.sort(tasks);

		System.out.println("\nGiven Task Details (Sorted according to their Priorities as per RM scheduling guidelines)are : ");
		for(i=0;i<taskCount;i++){
			System.out.println("Task-"+(i+1)+" --->  p=" + tasks[i].period + "  e="+tasks[i].executionTime + "  D="+ tasks[i].relativeDeadline);
		}

		//Logic for computing Utilization Ratio.
		for(i=0;i<taskCount;i++){
			totalUtilizationRatio_U += (tasks[i].executionTime/tasks[i].period);
		}

		System.out.println("\nTotal Utilization ratio(U) for the given system is :" + totalUtilizationRatio_U);
		if(totalUtilizationRatio_U <= 1){
			System.out.println("--->Hence the Necessary condition of U <= 1 is satisfied.");
		}else{
			System.out.println("--->The Necessary condition of U <= 1 is NOT satisfied. Hence the given set of Tasks are NOT SCHEDULABLE!!");
			System.exit(0);
		}
		System.out.println("\nNow, let's compute and check if the Sufficient condition(Liu Leyland bound, U <= n(2^(1/n) - 1)) is satisfied as well or not.");

		liu_LeylandBound = (double)taskCount * (Math.pow(2.0, (1.0/taskCount)) - 1.0);

		System.out.println("\nThe value of n(2^(1/n) - 1)) is : " + liu_LeylandBound);
		if(totalUtilizationRatio_U <= liu_LeylandBound){
			System.out.println("\n===============================================================");
			System.out.println("The Sufficient Condition is satisfied as " +totalUtilizationRatio_U+" is less than or equal to " + liu_LeylandBound + ". Hence the given set of Tasks are SCHEDULABLE.");
		    System.out.println("===============================================================\n");
			System.exit(0);
		}else{
			System.out.println("\n--->The Sufficient Condition is NOT satisfied as " +totalUtilizationRatio_U+" is NOT less than or equal to " + liu_LeylandBound + ".\n\n--->Hence, let's do the Time Demand Analysis to conclude if the given set of taks are Schedulable or not.");
		}


		//logic to find Level-pi Busy Interval Value for every Task (Ti).
		int currentTaskNumber = taskCount;

		while(currentTaskNumber > 0){
			double busyInterval_previous = -1.0;
			double busyInterval = 0.0;
			double temp_t = Task.totalExecutionTime;
			do{
				busyInterval_previous = busyInterval;
				busyInterval = 0.0;
				for(i=0;i<(currentTaskNumber);i++){
					busyInterval += (Math.ceil(temp_t/tasks[i].period) * tasks[i].executionTime);
				}
				temp_t = busyInterval;
			}while(busyInterval != busyInterval_previous);

			//Logic to check if the Busy interval computation fails to converge(for some corner cases).
			if(Double.isInfinite(busyInterval)){
				System.out.println("\n===============================================================");
					System.out.println("The given set of Tasks are NOT SCHEDULABLE. This is because the Busy Interval is not Converging for Level-"+currentTaskNumber);
					System.out.println("===============================================================\n");
					System.exit(0);
			}
	
			System.out.println("\nLength of First Level - "+currentTaskNumber + " Busy Interval is : "+busyInterval);
			System.out.println("Now Lets check if the Response times of all the jobs in Task-"+currentTaskNumber+"  released in this Busy Interval are within their Relative Deadline or not");
	
			
			//logic to find Response time for all the jobs of a particular Task(Ti) in each First Level-Pi Busy Interval.
			double job_count_per_task = Math.ceil(busyInterval/tasks[currentTaskNumber-1].period);
			double j = 0;
			
			for(j=1;j<=job_count_per_task;j++){
				double responseTime_previous = -1.0;
				double responseTime = 0.0;
				
				double temp_et = tasks[currentTaskNumber-1].executionTime;
				do{
					responseTime_previous = responseTime;
					responseTime = 0.0;
					for(i=0;i<(currentTaskNumber-1);i++){
						responseTime += (Math.ceil((temp_et+((j-1) * tasks[currentTaskNumber-1].period))/tasks[i].period) * tasks[i].executionTime);
					}
					responseTime += (j * tasks[currentTaskNumber-1].executionTime);
					responseTime -= ((j-1) * tasks[currentTaskNumber-1].period);
					temp_et = responseTime;
				}while(responseTime != responseTime_previous);
		
				if(responseTime <= tasks[currentTaskNumber-1].relativeDeadline){
					System.out.println("Converged Value of Response Time, W(" +currentTaskNumber+","+ (int)j +") is : " + responseTime +" which is less than or equal to it's Relative Deadline, "+tasks[currentTaskNumber-1].relativeDeadline);
				}else{
					System.out.println("Converged Value of Response Time, W(" +currentTaskNumber+","+ (int)j +") is : " + responseTime +" which is NOT less than or equal to it's Relative Deadline, "+tasks[currentTaskNumber-1].relativeDeadline);			
					System.out.println("\n===============================================================");
					System.out.println("Hence, using the Time Demand Analysis, we can conclude that the given set of Tasks are NOT SCHEDULABLE.");
					System.out.println("===============================================================\n");
					System.exit(0);
				}
	
			}

			Task.totalExecutionTime -= tasks[currentTaskNumber-1].executionTime;
			currentTaskNumber--;
		}

		System.out.println("\n===============================================================");
		System.out.println("Hence, using the Time Demand Analysis, we can conclude that the given set of Tasks are SCHEDULABLE.");
		System.out.println("===============================================================\n");
	}
}