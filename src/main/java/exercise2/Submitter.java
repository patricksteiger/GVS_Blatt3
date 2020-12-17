package exercise2;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import util.Utils;

public class Submitter implements Runnable {

    private final byte[] result;
    private final ZContext context;
    private final ZMQ.Socket socket;
    private final String prefix;

    public Submitter(ZContext context, byte[] result, String prefix) {
        this.result = result;
        this.prefix = prefix;
        this.context = context;
        this.socket = context.createSocket(SocketType.REQ);
        this.socket.setIPv6(true);
    }

    @Override
    public void run() {
        System.out.println("Submitter started...");
        this.socket.connect(Utils.REPLIER_ADDRESS);
        this.socket.send(this.result);
        byte[] reply = this.socket.recv();
        String answer = new String(reply, ZMQ.CHARSET);
        System.out.println("Prefix: " + this.prefix + ", Result: " + new String(result, ZMQ.CHARSET) + ", Answer: " + answer + ".");
        this.socket.close();
    }
}
