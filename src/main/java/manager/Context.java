package manager;

public interface Context {
    /**
     * @return amount of successfully ended threads
     */
    int getCompletedTaskCount();

    /**
     * @return amount of threads ended with exception
     */
    int getFailedTaskCount();

    /**
     * @return amount of interrupted threads
     */
    int getInterruptedTaskCount();

    /**
     * interrupt all running threads
     */
    void interrupt();

    /**
     * @return is all tasks ended (successfully or not)
     */
    boolean isFinished();
}
