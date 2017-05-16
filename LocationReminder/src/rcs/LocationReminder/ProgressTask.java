package rcs.LocationReminder;

public interface ProgressTask {

	// Class constants defining state of the task
	public static final int PROGRESS_TASK_DONE = 0;
	public static final int PROGRESS_TASK_RUNNING = 1;
	public static final int PROGRESS_TASK_NOT_STARTED = -1;
	public static final int PROGRESS_TASK_FAILED = -2;
	
	/**
	 * Returns the total %age of completion for the task at hand.
	 * 
	 * @return
	 */
	public float getCompletionPercentage();

	/**
	 * The actual task to be performed
	 */
	public void performTask();

	/**
	 * Returns the status of Task
	 */
	public int getTaskState();
	
	public Object getTaskResult();
}
