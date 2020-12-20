package exercise1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    /*
    Only Main needs to be executed to solve exercise.
    Multiple Subscribers means calculation of result asynchronously.
    Each Subscriber has multiple Threads to calculate different results in parallel.
     */

    // Decides how many Subscribers are started.
    public static final int SUBSCRIBER_THREAD_COUNT = 3;
    // Decides how many concurrent Requester-Threads can be started for each Subscriber.
    public static final int REQUESTER_THREAD_COUNT = 3;

    public static void main(String[] args) {
        System.out.println("Exercise 1 started...");
        ExecutorService executorService = Executors.newFixedThreadPool(SUBSCRIBER_THREAD_COUNT);
        for (int i = 0; i < SUBSCRIBER_THREAD_COUNT; i++)
            executorService.execute(new Subscriber());
    }
}
