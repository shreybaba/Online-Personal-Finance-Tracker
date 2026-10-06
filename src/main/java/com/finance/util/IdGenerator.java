package com.finance.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates primary keys for the VARCHAR(30) id columns.
 * Format: PREFIX + epoch millis + 4 random digits, e.g. EXP17597381234561234.
 * IDs sort by creation time, which is used for "most recent" listings.
 */
public final class IdGenerator {

    private IdGenerator() {
    }

    public static String next(String prefix) {
        int suffix = ThreadLocalRandom.current().nextInt(10_000);
        return prefix + System.currentTimeMillis() + String.format("%04d", suffix);
    }
}
