package com.admanager.wastickers.utils;

public class WorkCounter {
    private final Listener listener;
    private int runningTasks;

    public WorkCounter(int numberOfTasks, Listener listener) {
        this.runningTasks = numberOfTasks;
        this.listener = listener;
    }

    // Only call this in onPostExecute! (or add synchronized to method declaration)
    public void taskFinished() {
        if (--runningTasks == 0) {
            listener.completed();
        }
    }

    public interface Listener {
        void completed();
    }
}