import java.util.concurrent.Semaphore;

/**
 * FriendRequestThread implements a runnable task for sending friend requests between students.
 * This class enables concurrent friend request operations by running them asynchronously
 * in separate threads with proper synchronization mechanisms.
 */
public class FriendRequestThread implements Runnable {
    private UniversityStudent sender;
    private UniversityStudent receiver;

    private static final Semaphore semaphore = new Semaphore(1);
    /**
     * Constructs a FriendRequestThread with sender and receiver information.
     *
     * @param sender the UniversityStudent sending the friend request
     * @param receiver the UniversityStudent receiving the friend request
     */
    public FriendRequestThread(UniversityStudent sender, UniversityStudent receiver) {
        // Constructor
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Executes the friend request operation in a separate thread.
     * This method handles the logic of sending a friend request from sender to receiver,
     * including thread-safe operations and confirmation handling.
     */
    @Override
    public void run() {
        try {
            semaphore.acquire();
            System.out.println("Friend request from " + sender.name + " to " + receiver.name);
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Friend request interrupted by: " + e.getMessage());
        }
        finally {
            semaphore.release();
        }
    }
}
