package org.lins.mmmjjkx.fakeplayermaker.util;

import java.util.Random;

public class CommonUtils {
    private static final char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = new Random().nextInt(chars.length);
            sb.append(chars[index]);
        }
        return sb.toString();
    }
}
