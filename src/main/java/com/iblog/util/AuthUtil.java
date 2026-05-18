package com.iblog.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class AuthUtil {
    private static final int COST = 10;

    public static String hashPassword(String plain) {
        return BCrypt.withDefaults().hashToString(COST, plain.toCharArray());
    }

    public static boolean verifyPassword(String plain, String hashed) {
        return BCrypt.verifyer().verify(plain.toCharArray(), hashed).verified;
    }
}
