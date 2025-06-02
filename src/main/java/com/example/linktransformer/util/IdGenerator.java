package com.example.linktransformer.util;

import java.security.SecureRandom;

public class IdGenerator {

    private static final String ALPHABET_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ID_LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomId() {
        StringBuilder sb = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            sb.append(ALPHABET_CHARACTERS.charAt(RANDOM.nextInt(ALPHABET_CHARACTERS.length())));
        }
        return sb.toString();
    }
}
