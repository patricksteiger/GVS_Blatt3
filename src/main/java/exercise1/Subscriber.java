package exercise1;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import util.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Subscriber implements Runnable {
    @Override
    public void run() {
        System.out.println("New Subscriber started...");
        try (ZContext context = new ZContext()) {
            ExecutorService executor = Executors.newFixedThreadPool(Main.REQUESTER_THREAD_COUNT);
            // Create Socket and subscribe
            ZMQ.Socket subscribeSocket = context.createSocket(SocketType.SUB);
            subscribeSocket.setIPv6(true);
            subscribeSocket.connect(Utils.PUBLISHER_ADDRESS);
            subscribeSocket.subscribe("");
            // Old prefix initialized as invalid hexadecimal
            String oldPrefix = "z";
            while (true) {
                byte[] published = subscribeSocket.recv();
                String prefix = new String(published, ZMQ.CHARSET);
                // Only start new Requester-Thread, if new prefix wasn't sent before.
                if (!oldPrefix.equals(prefix)) {
                    oldPrefix = prefix;
                    executor.execute(new Requester(context, prefix));
                }
            }
        }
    }
}
