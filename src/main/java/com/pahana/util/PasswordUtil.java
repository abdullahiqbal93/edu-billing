package com.pahana.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    private static final int WORK_FACTOR = 10;

    public static String hash(String password) {
        if (password == null) return null;
        return BCrypt.hashpw(password, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean verify(String candidate, String stored) {
        if (candidate == null || stored == null) return false;
        try {
            if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
                return BCrypt.checkpw(candidate, stored);
            }
        } catch (Exception ignored) {
        }
        return candidate.equals(stored);
    }
}
