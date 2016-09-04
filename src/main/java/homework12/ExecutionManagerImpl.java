package homework12;

import context.Context;
import context.ExecutionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class ExecutionManagerImpl implements ExecutionManager {

    @Override
    public Context execute(Runnable callback, Runnable... tasks) {
        List<Thread> threads = new ArrayList<>();
        final ContextImpl context = new ContextImpl(threads);
        Arrays.stream(tasks)
                .map(task -> new ExecutionThread(context, task, tasks.length, callback))
                .forEach(threads::add);
        threads.forEach(Thread::start);
        return context;
    }

    private class ExecutionThread extends Thread {
        private final ContextImpl context;
        private final Runnable task;
        private final int callBackCount;
        private final Runnable callBack;

        public ExecutionThread(ContextImpl context, Runnable target, int callBackCount, Runnable callBack) {
            this.context = context;
            this.task = target;
            this.callBackCount = callBackCount;
            this.callBack = callBack;
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
                synchronized (context) {
                    if ((context.getCompletedTaskCount() +
                            context.getInterruptedTaskCount() +
                            context.getFailedTaskCount()) == callBackCount) {
                        new Thread(callBack).start();
                    }
                }
            }
        }
    }
}
