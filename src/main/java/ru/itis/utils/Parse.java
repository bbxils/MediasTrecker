package ru.itis.utils;

import java.util.Map;

public final class Parse {

    public static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    public static Long readLong(String s) {
        String v = blankToNull(s);
        if (v == null) return null;
        try {
            return Long.valueOf(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer readInt(String s, String field, int min, int max, Map<String, String> errors) {
        String v = blankToNull(s);
        if (v == null) return null;
        try {
            int n = Integer.parseInt(v);
            if (n < min || n > max) {
                errors.put(field, "Допустимо от " + min + " до " + max);
                return null;
            }
            return n;
        } catch (NumberFormatException e) {
            errors.put(field, "Нужно целое число");
            return null;
        }
    }
    public static Integer readInt(String s) {
        String v = blankToNull(s);
        if (v == null) return null;
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}