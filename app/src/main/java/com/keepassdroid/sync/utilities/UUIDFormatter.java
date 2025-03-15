package com.keepassdroid.sync.utilities;

public class UUIDFormatter {
    public static String fromStandardFormat(String uuid) {
        return uuid.replace("-", "").toUpperCase();
    }

    public static String toStandardFormat(String uuid) {
        return uuid.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5"
        ).toLowerCase();
    }
}
