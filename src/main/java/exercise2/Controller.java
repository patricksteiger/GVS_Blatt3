package exercise2;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import util.Utils;

public class Controller {

    public static final String CONTROLLER_ADDRESS = "tcp://localhost:5557";
    public static final String SINK_ADDRESS = "tcp://localhost:5558";

    public static void main(String[] args) {
        System.out.println("Controller started...");
        try (ZContext context = new ZContext()) {
            // Create socket to push prefixes
            ZMQ.Socket pushSocket = context.createSocket(SocketType.PUSH);
            pushSocket.setIPv6(true);
            pushSocket.bind(CONTROLLER_ADDRESS);
            // Subscribe to Publisher-socket
            ZMQ.Socket subscriberSocket = context.createSocket(SocketType.SUB);
            subscriberSocket.setIPv6(true);
            subscriberSocket.connect(Utils.PUBLISHER_ADDRESS);
            subscriberSocket.subscribe("");
            String oldPrefix = "z";
            while (true) {
                byte[] publisherReply = subscriberSocket.recv();
                String prefix = new String(publisherReply, ZMQ.CHARSET);
                if (!oldPrefix.equals(prefix)) {
                    oldPrefix = prefix;
                    System.out.println("Controller sends prefix: " + prefix);
                    pushSocket.send(prefix.getBytes(ZMQ.CHARSET));
                    new Thread(new Submitter(context, prefix)).start();
                }
            }
        }
    }
}
