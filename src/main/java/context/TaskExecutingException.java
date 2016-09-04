package context;

public class TaskExecutingException extends RuntimeException{
    public TaskExecutingException(String message, Throwable cause) {
        super(message, cause);
    }
}
