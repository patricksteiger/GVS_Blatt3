package util;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Random;

public class Utils {
    public static final String PUBLISHER_ADDRESS = "tcp://gvs.lxd-vs.uni-ulm.de:27341";
    public static final String REPLIER_ADDRESS = "tcp://gvs.lxd-vs.uni-ulm.de:27349";
    private static final String hexadecimalChars = "0123456789abcdef";
    private static final int MAX_RESULT_LENGTH = 64;

    /**
     * Calculate result by randomly generating hexadecimals of max length 64 beginning with given prefix,
     * generating their hashes and checking if hashes start with prefix. Checks until result is found.
     *
     * @param prefix starting string for hash and result.
     * @return result starting with prefix, while its hash also starts with prefix.
     */
    public static String getResultPrefix(String prefix) {
        // Calculate max length for result, add 1 since Random.nextInt() is exclusive
        final int addedPrefixLength = MAX_RESULT_LENGTH - prefix.length() + 1;
        while (true) {
            Random rnd = new Random();
            int l = rnd.nextInt(addedPrefixLength);
            String resultPrefix = prefix + getRandomHexadecimalString(l);
            String hash = DigestUtils.sha256Hex(resultPrefix);
            if (hash.startsWith(prefix))
                return resultPrefix;
        }
    }

    public static String getRandomHexadecimalString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(getRandomHexadecimalChar());
        return sb.toString();
    }

    public static String getRandomHexadecimalString(String prefix) {
        final int addedPrefixLength = MAX_RESULT_LENGTH - prefix.length() + 1;
        return prefix + getRandomHexadecimalString(addedPrefixLength);
    }

    public static char getRandomHexadecimalChar() {
        Random random = new Random();
        int index = random.nextInt(hexadecimalChars.length());
        return hexadecimalChars.charAt(index);
    }
}
