package exercise2;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import util.Utils;

public class Controller {

    /*
    Controller receives prefix from Publisher and sends them to a PUB-Socket.
    Worker subscribes to PUB-Socket.
     */

    public static final String CONTROLLER_ADDRESS = "tcp://localhost:5557";

    public static void main(String[] args) {
        System.out.println("Controller started...");
        try (ZContext context = new ZContext()) {
            // Create socket to publish prefixes
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
                // Get prefix from Pub-Server
                byte[] publisherReply = subscriberSocket.recv();
                String prefix = new String(publisherReply, ZMQ.CHARSET);
                // Publish prefix to worker
                pushSocket.send(prefix.getBytes(ZMQ.CHARSET));
            }
        }
    }
}
