package homework12;

import context.Context;

import java.util.List;

public class ContextImpl implements Context {
    private final Object lock = new Object();
    private final List<? extends Thread> threads;
    private int failedTask;
    private int completedTask;
    private int interrupted;

    public ContextImpl(List<? extends Thread> threads) {
        this.threads = threads;
    }

    public synchronized int getCompletedTaskCount() {
        return completedTask;
    }

    public synchronized int getFailedTaskCount() {
        return failedTask;
    }

    public synchronized int getInterruptedTaskCount() {
        return interrupted;
    }

    public void interrupt() {
        synchronized (lock) {
            threads.forEach(Thread::interrupt);
        }
    }

    public boolean isFinished() {
        synchronized (lock) {
            for (Thread thread : threads) {
                if (thread.isAlive()) {
                    return false;
                }
            }
            return true;
        }
    }

    synchronized void incrementCompletedTasks() {
        completedTask++;
    }

    synchronized void incrementFailedTasks() {
        failedTask++;
    }

    synchronized void incrementInterruptedTasks() {
        interrupted++;
    }
}
