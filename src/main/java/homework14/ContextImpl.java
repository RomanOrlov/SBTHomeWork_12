package homework14;

import context.Context;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContextImpl implements Context {
    private final Lock lock = new ReentrantLock();
    private final List<? extends Thread> threads;
    private final AtomicInteger failedTask = new AtomicInteger(0);
    private final AtomicInteger completedTask = new AtomicInteger(0);
    private final AtomicInteger interrupted = new AtomicInteger(0);

    public ContextImpl(List<? extends Thread> threads) {
        this.threads = threads;
    }

    public synchronized int getCompletedTaskCount() {
        return completedTask.get();
    }

    public synchronized int getFailedTaskCount() {
        return failedTask.get();
    }

    public synchronized int getInterruptedTaskCount() {
        return interrupted.get();
    }

    public void interrupt() {
        lock.lock();
        try {
            threads.forEach(Thread::interrupt);
        } finally {
            lock.unlock();
        }
    }

    public boolean isFinished() {
        lock.lock();
        try {
            for (Thread thread : threads) {
                if (thread.isAlive()) {
                    return false;
                }
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    void incrementCompletedTasks() {
        completedTask.getAndDecrement();
    }

    void incrementFailedTasks() {
        failedTask.getAndIncrement();
    }

    void incrementInterruptedTasks() {
        interrupted.getAndDecrement();
    }
}
