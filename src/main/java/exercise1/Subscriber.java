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
            ZMQ.Socket subscribeSocket = context.createSocket(SocketType.SUB);
            subscribeSocket.setIPv6(true);
            subscribeSocket.connect(Utils.PUBLISHER_ADDRESS);
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
