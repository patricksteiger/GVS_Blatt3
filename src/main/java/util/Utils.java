package util;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Random;

public class Utils {
    public static final String hexadecimalChars = "0123456789abcdef";
    public static final int MAX_RESULT_LENGTH = 64;

    public static String getResultPrefix(String prefix) {
        while (true) {
            Random rnd = new Random();
            int l = rnd.nextInt(MAX_RESULT_LENGTH);
            String resultPrefix = prefix + getRandomHexadecimalString(l);
            String hash = DigestUtils.sha256Hex(resultPrefix);
            if (hash.startsWith(prefix))
                return resultPrefix;
        }
    }

    public static String getRandomHexadecimalString(int length) {
        StringBuilder sb = new StringBuilder();
        while (length-- > 0) {
            sb.append(getRandomHexadecimalChar());
        }
        return sb.toString();
    }

    public static char getRandomHexadecimalChar() {
        Random random = new Random();
        int index = random.nextInt(hexadecimalChars.length());
        return hexadecimalChars.charAt(index);
    }
}
