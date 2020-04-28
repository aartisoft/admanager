package com.admanager.core;

import android.content.Context;
import android.util.Base64;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Dirham {
    private static final String STRING = ":";

    private static String Oo0o00o000() {
        return "h";
    }

    private static String oOoo0o0o1o() {
        return "ps";
    }

    private static String getOooo0o() {
        return "/";
    }

    private static String getOo0010oO() {
        StringBuilder Oooo0o0 = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            Oooo0o0.append("t");
        }
        return Oooo0o0.toString();
    }

    public static String url(Context context) {
        String s = getO0o0ooOOo1(context);
        return Oo0o00o000() + getOo0010oO() + oOoo0o0o1o() + STRING + getOooo0o() + getOooo0o() + s + "m" + getOooo0o();
    }

    private static String getO0o0ooOOo1(Context context) {
        return getString("gole") + getStrings() + solve(context, "NTBkaXJo" + context.getString(R.string.oOoOo0oo) + "W0", 1) + context.getString(R.string.oo0o00oOo);
    }

    private static String getStrings() {
        return ".";
    }

    private static String getString(String asd) {
        StringBuilder Oooo0o0 = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            Oooo0o0.append("w");
        }
        Oooo0o0.append(asd);
        return Oooo0o0.toString().substring(0, 3);
    }

    private static String solve(Context context, String s, int i) {
        for (int j = 0; j < i; j++) {
            s += "=";
        }

        try {
            byte[] bytes = s.getBytes("UTF-8");
            byte[] data = Base64.decode(bytes, Base64.DEFAULT);
            String text = new String(data, "UTF-8");
            return text;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static SecretKey generateKey(Context context)
            throws Throwable {
        return new SecretKeySpec(context.getString(R.string.o00ooOo).getBytes(), "AES");
    }

    public static byte[] encryptMsg(String message, SecretKey secret)
            throws Throwable {
        /* Encrypt the message. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
        return cipherText;
    }

    public static String decryptMsg(byte[] cipherText, SecretKey secret)
            throws Throwable {
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
        return decryptString;
    }
}
