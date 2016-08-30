package manager;

import java.util.List;

public class ContextImpl implements Context{
    private final Object lock = new Object();
    private final List<Thread> threads;
    private int failedTask;
    private int completedTask;

    public ContextImpl(List<Thread> threads) {
        this.threads = threads;
    }

    public synchronized int getCompletedTaskCount() {
        return completedTask;
    }

    public synchronized int getFailedTaskCount() {
        return failedTask;
    }

    public int getInterruptedTaskCount() {
        synchronized (lock) {
            return (int) threads.stream()
                    .filter(Thread::isInterrupted)
                    .count();
        }
    }

    public void interrupt() {
        synchronized (lock) {
            threads.forEach(Thread::interrupt);
        }
    }

    public boolean isFinished() {
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                return false;
            }
        }
        return true;
    }
}
