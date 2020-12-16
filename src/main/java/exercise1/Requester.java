package exercise1;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import util.Utils;

public class Requester implements Runnable {
    private final ZContext context;
    private final ZMQ.Socket socket;
    private final String prefix;

    public Requester(ZContext context, String prefix) {
        this.prefix = prefix;
        this.context = context;
        this.socket = this.context.createSocket(SocketType.REQ);
        this.socket.setIPv6(true);
    }

    @Override
    public void run() {
        System.out.println("Thread started! Prefix: " + this.prefix + ".");
        String result = Utils.getResultPrefix(this.prefix);
        this.socket.connect(Main.REPLY_ADDRESS);
        this.socket.send(result.getBytes(ZMQ.CHARSET));
        byte[] reply = this.socket.recv();
        String rank = new String(reply, ZMQ.CHARSET);
        System.out.println("Prefix: " + this.prefix + ", Result: " + result + ", Answer: " + rank + ".");
        this.socket.close();
    }
}
