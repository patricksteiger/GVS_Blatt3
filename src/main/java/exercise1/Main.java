package exercise1;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static final String PUBLISHER_ADDRESS = "tcp://gvs.lxd-vs.uni-ulm.de:27341";
    public static final String REPLY_ADDRESS = "tcp://gvs.lxd-vs.uni-ulm.de:27349";
    public static final int THREAD_COUNT = 4;

    public static void main(String[] args) {
        System.out.println("Exercise 1 started...");
        try (ZContext context = new ZContext()) {
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            ZMQ.Socket subscribeSocket = context.createSocket(SocketType.SUB);
            subscribeSocket.setIPv6(true);
            subscribeSocket.connect(PUBLISHER_ADDRESS);
            subscribeSocket.subscribe("");
            String oldPrefix = "p";
            while (true) {
                byte[] published = subscribeSocket.recv();
                String prefix = new String(published, ZMQ.CHARSET);
                if (!oldPrefix.equals(prefix)) {
                    oldPrefix = prefix;
                    executor.execute(new Requester(context, prefix));
                }
            }
        }
    }
}
