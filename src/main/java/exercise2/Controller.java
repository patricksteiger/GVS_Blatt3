package exercise2;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import util.Utils;

public class Controller {

    public static final String CONTROLLER_ADDRESS = "tcp://localhost:5557";

    public static void main(String[] args) {
        System.out.println("Controller started...");
        try (ZContext context = new ZContext()) {
            // Create socket to push prefixes
            ZMQ.Socket pushSocket = context.createSocket(SocketType.PUB);
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
                pushSocket.send(prefix.getBytes(ZMQ.CHARSET));
            }
        }
    }
}
