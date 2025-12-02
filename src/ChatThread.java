import java.util.concurrent.Semaphore;

/**
 * ChatThread implements a runnable task for sending chat messages between students.
 * This class enables concurrent communication by allowing messages to be sent
 * asynchronously in separate threads. Implements thread-safe communication
 * using semaphores or locks to manage message ordering and delivery.
 */
public class ChatThread implements Runnable {
    private UniversityStudent sender;
    private UniversityStudent receiver;
    private String message;
    private static final Semaphore semaphore = new Semaphore(1);

    /**
     * Constructs a ChatThread with sender, receiver, and message information.
     *
     * @param sender the UniversityStudent sending the message
     * @param receiver the UniversityStudent receiving the message
     * @param message the String content of the message to be sent
     */
    public ChatThread(UniversityStudent sender, UniversityStudent receiver, String message) {
        // Constructor
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    /**
     * Executes the chat operation in a separate thread.
     * This method handles the logic of sending a message from sender to receiver.     
     */
    @Override
    public void run() {
        try {
            semaphore.acquire();
            System.out.println(sender.name + ": " + message);
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Chat interrupted by: " + e.getMessage());
        }
        finally {
            semaphore.release();
        }
    }
}
