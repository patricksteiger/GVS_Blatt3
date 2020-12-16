package exercise1;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {
    public static final String PUBLISHER_ADDRESS = "gvs.lxd-vs.uni-ulm.de:27341";
    public static final String REPLY_ADDRESS = "gvs.lxd-vs.uni-ulm.de:27349";
    public static final String HASH_ALGORITHM = "SHA-256";
    public static final int THREAD_COUNT = 3;

    public static void main(String[] args) {
        System.out.println("Exercise started...");
        try (ZContext context = new ZContext()) {
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            ZMQ.Socket subscribeSocket = context.createSocket(SocketType.SUB);
            subscribeSocket.setIPv6(true);
            subscribeSocket.connect(PUBLISHER_ADDRESS);
            subscribeSocket.subscribe("");
            int maxExer = 10;
            String oldPrefix = "";
            while (maxExer-- > 0) {
                byte[] published = subscribeSocket.recv();
                String prefix = new String(published, ZMQ.CHARSET);
                if (!oldPrefix.equals(prefix)) {
                    oldPrefix = prefix;
                    executor.execute(new Requester(context, prefix));
                } else {
                    maxExer++;
                }
            }
        }
    }
}
