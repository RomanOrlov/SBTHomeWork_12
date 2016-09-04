package homework12;

import context.Context;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Runnable callBack = () -> System.err.println("Callback happened");
        Runnable[] tasks = new Runnable[50];
        for (int i=0;i<tasks.length;i++) {
            final int ii = i;
            tasks[i] = () -> {
                System.err.println("task "+ii);
                if (ii%5==0) {
                    throw new IllegalArgumentException("exception "+ii);
                }
            };
        }
        ExecutionManagerImpl executionManager = new ExecutionManagerImpl();
        Context execute = executionManager.execute(callBack, tasks);
        System.err.println("isfinished " + execute.isFinished());
        execute.interrupt();

        // only for output
        Thread.sleep(1000);

        System.err.println("completed " + execute.getCompletedTaskCount());
        System.err.println("failed " + execute.getFailedTaskCount());
        System.err.println("interrupted " + execute.getInterruptedTaskCount());
        System.err.println("isfinished " + execute.isFinished());
    }
}
