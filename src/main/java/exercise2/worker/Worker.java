package exercise2.worker;

import exercise2.Controller;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import util.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Worker {

    public static final int WORKER_THREAD_COUNT = 3;
    private static final ZContext context = new ZContext();

    public static void main(String[] args) {
        System.out.println("Worker main started...");
        ZMQ.Socket pullSocket = context.createSocket(SocketType.PULL);
        pullSocket.setIPv6(true);
        pullSocket.connect(Controller.CONTROLLER_ADDRESS);
        ZMQ.Socket pushSocket = context.createSocket(SocketType.PUSH);
        pushSocket.setIPv6(true);
        pushSocket.bind(Controller.SINK_ADDRESS);
        ExecutorService executorService = Executors.newFixedThreadPool(WORKER_THREAD_COUNT);
        while (true) {
            byte[] pullReply = pullSocket.recv();
            String prefix = new String(pullReply, ZMQ.CHARSET);
            executorService.execute(() -> {
                System.out.println("Worker started with prefix: " + prefix + ".");
                String result = Utils.getResultPrefix(prefix);
                System.out.println("Worker got result: " + result);
                pushSocket.send(result.getBytes(ZMQ.CHARSET));
            });
        }
    }
}
