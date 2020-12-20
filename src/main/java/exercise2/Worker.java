package exercise2;

import exercise2.Controller;
import exercise2.Submitter;
import org.apache.commons.codec.digest.DigestUtils;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Worker {

    public static final int WORKER_THREAD_COUNT = 3;
    private static final ZContext context = new ZContext();

    public static void main(String[] args) {
        System.out.println("Worker main started...");
        ZMQ.Socket pullSocket = context.createSocket(SocketType.SUB);
        pullSocket.setIPv6(true);
        pullSocket.connect(Controller.CONTROLLER_ADDRESS);
        pullSocket.subscribe("");
        List<Thread> workerThreads = new ArrayList<>();
        String oldPrefix = "t";
        while (true) {
            byte[] pullReply = pullSocket.recv();
            String prefix = new String(pullReply, ZMQ.CHARSET);
            if (!oldPrefix.equals(prefix)) {
                workerThreads.forEach(Thread::interrupt);
                workerThreads.clear();
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
            while (!Thread.currentThread().isInterrupted() && !hash.startsWith(prefix)) {
                result = Utils.getRandomHexadecimalString(prefix);
                hash = DigestUtils.sha256Hex(result);
            }
            if (!Thread.currentThread().isInterrupted()) {
                new Thread(new Submitter(this.prefix, result)).start();
            }
            System.out.println("WorkerThread closing...");
        }
    }
}
