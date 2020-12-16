package exercise1;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        try {
            MessageDigest md = MessageDigest.getInstance(Main.HASH_ALGORITHM);
            String result = this.prefix;
            do {
                md.update(result.getBytes(StandardCharsets.UTF_8));
                result = bytesToHex(md.digest());
            } while (!result.startsWith(this.prefix));
            this.socket.connect(Main.REPLY_ADDRESS);
            this.socket.send(result.getBytes(ZMQ.CHARSET));
            byte[] reply = this.socket.recv();
            String rank = new String(reply, ZMQ.CHARSET);
            System.out.println("Prefix: " + this.prefix + ", Hash: " + result + ", Rank: " + rank);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            this.socket.close();
        }
    }

    private static String bytesToHex(byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : bytes)
            builder.append(String.format("%02x", b));
        return builder.toString();
    }
}