package task;

import java.util.concurrent.Callable;

public class Task<T> {
    private final Callable<? extends T> callable;
    private volatile T result;
    private volatile boolean isResultReady;
    private volatile boolean exceptionOccurred;
    private volatile RuntimeException exception;
    private volatile boolean firstThread = true;
    private final Object lockForFirstThreads = new Object();

    public Task(Callable<? extends T> callable) {
        this.callable = callable;
    }

    public T get() {
        if (isResultReady) return getResult();
        if (allowToCalculateResult()) return calculateResult();
        synchronized (this) {
            while (!isResultReady) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Thread interrupted", e);
                }
            }
        }
        return getResult();
    }

    private boolean allowToCalculateResult() {
        if (firstThread) {
            boolean allowToCalculateResult = false;
            synchronized (lockForFirstThreads) {
                if (firstThread) {
                    firstThread = false;
                    allowToCalculateResult = true;
                }
            }
            return allowToCalculateResult;
        }
        return false;
    }

    private T calculateResult() {
        try {
            result = callable.call();
        } catch (Exception ex) {
            exception = new TaskExecutingException("Exception occurred while trying to calculate result", ex);
            exceptionOccurred = true;
        }
        isResultReady = true;
        synchronized (this) {
            notifyAll();
        }
        return getResult();
    }

    private T getResult() {
        if (exceptionOccurred) throw exception;
        return result;
    }
}