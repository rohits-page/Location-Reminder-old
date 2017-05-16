package rcs.LocationReminder;

import rcs.LocationReminder.general.ApplicationSettings;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author rohsharm 
 * Inner class that performs progress calculations on a second
 * thread. Implement the thread by subclassing Thread and overriding its
 * run() method. Also provide a setState(state) method to stop the
 * thread gracefully.
 */

public class ProgressThread extends Thread {

	private Handler mHandler;
	private int mState;
	private int mDelay;
	

	private ProgressTask mProgressTask;

	/**
	 * Constructor with an argument that specifies Handler on main thread to
	 * which messages will be sent by this thread. Delay specifies the frequency
	 * of update
	 * 
	 * @param h
	 * @param mDelay
	 */
	public ProgressThread(Handler h,ProgressTask progressActivity,int delay) {
		mHandler = h;
		this.mDelay = delay;
		this.mProgressTask=progressActivity;
	}

	/*
	 * Override the run() method that will be invoked automatically when the
	 * Thread starts. Do the work required to update the progress bar on this
	 * thread but send a message to the Handler on the main UI thread to
	 * actually change the visual representation of the progress. In this
	 * example we count the index total down to zero, so the horizontal progress
	 * bar will start full and count down.
	 */
	@Override
	public void run() {
		mState = ApplicationSettings.PROGRESS_THREAD_RUNNING;

		if(mProgressTask.getTaskState()==ProgressTask.PROGRESS_TASK_NOT_STARTED)
			mProgressTask.performTask();
		
		while (mState == ApplicationSettings.PROGRESS_THREAD_RUNNING) {
			/*
			 * The method Thread.sleep throws an InterruptedException if
			 * Thread.interrupt() were to be issued while thread is sleeping;
			 * the exception must be caught.
			 */
			try {
				// Control speed of update (but precision of mDelay
				// not guaranteed)
				Thread.sleep(mDelay);
			} catch (InterruptedException e) {
				Log.e("ERROR", "Thread was Interrupted");
			}

			// Send message (with current value of total as data) to Handler on
			// UI thread
			// so that it can update the progress bar.

			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putFloat("total", mProgressTask.getCompletionPercentage());
			msg.setData(b);
			mHandler.sendMessage(msg);
		}
	}

	// Set current state of thread (use state=ProgressThread.DONE to stop
	// thread)
	public void setProgressThreadState(int state) {
		mState = state;
	}
	
	public int getProgressThreadState() {
		return mState;
	}


}
