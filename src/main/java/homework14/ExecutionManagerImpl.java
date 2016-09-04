package homework14;

import context.Context;
import context.ExecutionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class ExecutionManagerImpl implements ExecutionManager {

    @Override
    public Context execute(Runnable callback, Runnable... tasks) {
        List<Thread> threads = new ArrayList<>();
        final ContextImpl context = new ContextImpl(threads);
        CountDownLatch countDownLatch = new CountDownLatch(tasks.length);
        Arrays.stream(tasks)
                .map(task -> new ExecutionThread(context, task, countDownLatch))
                .forEach(threads::add);
        threads.forEach(Thread::start);
        new CallBackThread(callback, countDownLatch).start();
        return context;
    }

    private class ExecutionThread extends Thread {
        private final ContextImpl context;
        private final Runnable task;
        private final CountDownLatch countDownLatch;

        public ExecutionThread(ContextImpl context, Runnable target, CountDownLatch countDownLatch) {
            this.context = context;
            this.task = target;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                if (isInterrupted()) {
                    context.incrementInterruptedTasks();
                    return;
                }
                task.run();
                if (isInterrupted()) {
                    context.incrementInterruptedTasks();
                    return;
                }
                context.incrementCompletedTasks();
            } catch (Exception e) {
                context.incrementFailedTasks();
                //e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    private class CallBackThread extends Thread {
        private final CountDownLatch countDownLatch;
        private final Runnable runnable;

        public CallBackThread(Runnable target, CountDownLatch countDownLatch) {
            this.runnable = target;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                countDownLatch.await();
                runnable.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
