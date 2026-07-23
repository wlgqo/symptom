package com.symptom.util;

public final class SensitiveDataUtil {

    private SensitiveDataUtil() {
    }

    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return "-";
        }
        String v = idCard.trim();
        if (v.length() <= 8) {
            return v.charAt(0) + "****" + v.charAt(v.length() - 1);
        }
        return v.substring(0, 4) + "**********" + v.substring(v.length() - 4);
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "-";
        }
        String v = phone.trim();
        if (v.length() < 7) {
            return v.charAt(0) + "****";
        }
        return v.substring(0, 3) + "****" + v.substring(v.length() - 4);
    }
}
