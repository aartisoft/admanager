package com.admanager.colorcallscreen.api;

import android.content.Context;
import android.util.Base64;

import com.admanager.colorcallscreen.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CallScreenService {
    public static final String STRING = ":";
    private static Retrofit retrofit;

    private static Retrofit getRetrofit(String s, final String key) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Request.Builder requestBuilder = original.newBuilder()
                                    .header("projectId", key)
                                    .header("accept", "application/json")
                                    .header("Content-Type", "application/json");

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    })
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(Oo0o00o000() + getOo0010oO() + oOoo0o0o1o() + STRING + getOooo0o() + getOooo0o() + s + "m" + getOooo0o())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

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

    public static CallScreenApi api(Context context) {
        return getRetrofit(getO0o0ooOOo1(context), get00ooOoOOo1(context))
                .create(CallScreenApi.class);
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

    private static String get00ooOoOOo1(Context context) {
        int s = 35;
        int s1 = 340;
        int s2 = 701;
        return context.getString(R.string.oOoo01oO0) + s + solve(context, context.getString(R.string.oOoOo0oo) + "WQyODdmZA", 2) + s1 + aaaa() + s2 + context.getString(R.string.oOOo0OoO0);
    }

    private static String aaaa() {
        String sss = "c";
        return "a" + 1 + sss + 0;
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
