package exercise2;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import util.Utils;

public class Submitter implements Runnable {

    private final String prefix;
    private final String result;

    public Submitter(String prefix, String result) {
        this.prefix = prefix;
        this.result = result;
    }

    @Override
    public void run() {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect(Utils.REPLIER_ADDRESS);
            socket.send(result.getBytes(ZMQ.CHARSET));
            byte[] reply = socket.recv();
            String answer = new String(reply, ZMQ.CHARSET);
            System.out.println("Prefix: " + this.prefix + ", Result: " + result + ", Answer: " + answer + ".");
            socket.close();
        }
    }
}
