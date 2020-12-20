package exercise2;

import org.apache.commons.codec.digest.DigestUtils;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Worker {

    /*
    Worker subscribes to PUB-Socket of Controller.
    Starts WorkerThreads to calculate result in parallel.
    WORKER_THREAD_COUNT decides how many in parallel.
    WorkerThreads creates new Submitter to submit results.
     */

    public static final int WORKER_THREAD_COUNT = 3;
    private static final ZContext context = new ZContext();

    public static void main(String[] args) {
        System.out.println("Worker main started...");
        // Subscribe to Pub-Socket of Controller
        ZMQ.Socket pullSocket = context.createSocket(SocketType.SUB);
        pullSocket.setIPv6(true);
        pullSocket.connect(Controller.CONTROLLER_ADDRESS);
        pullSocket.subscribe("");
        // Initialize list to hold threads
        List<Thread> workerThreads = new ArrayList<>();
        String oldPrefix = "t";
        while (true) {
            // Get response from Controller
            byte[] pullReply = pullSocket.recv();
            String prefix = new String(pullReply, ZMQ.CHARSET);
            if (!oldPrefix.equals(prefix)) {
                // If new prefix, interrupt all threads
                workerThreads.forEach(Thread::interrupt);
                workerThreads.clear();
                // Create and start new threads for new prefix
                for (int i = 0; i < WORKER_THREAD_COUNT; i++)
                    workerThreads.add(new Thread(new WorkerThread(prefix)));
                workerThreads.forEach(Thread::start);
                oldPrefix = prefix;
            }
        }
    }

    static class WorkerThread implements Runnable {

        private final String prefix;

        public WorkerThread(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public void run() {
            System.out.println("WorkerThread with prefix: " + prefix);
            String hash = "t";
            String result = prefix;
            // As long as thread isn't interrupted, try to calculate result
            while (!Thread.currentThread().isInterrupted() && !hash.startsWith(prefix)) {
                result = Utils.getRandomHexadecimalString(prefix);
                hash = DigestUtils.sha256Hex(result);
            }
            // Only send result if thread wasn't interrupted
            if (!Thread.currentThread().isInterrupted()) {
                new Thread(new Submitter(this.prefix, result)).start();
            }
            System.out.println("WorkerThread closing...");
        }
    }
}
