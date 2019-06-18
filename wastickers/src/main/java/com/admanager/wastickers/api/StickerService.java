package com.admanager.wastickers.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StickerService {
    private static final int INT = 70;
    private static final int INT1 = 10;
    private static Retrofit retrofit;

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            int i = INT - 47;
                            Request.Builder requestBuilder = original.newBuilder()
                                    .header("projectId", "5" + get546() + "0" + i + getX() + (INT * 5 - INT1) + "a1c0" + INT + "1ab")
                                    .header("accept", "application/json")
                                    .header("Content-Type", "application/json");

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    })
                    .build();
            String s = "ps:/";
            String t = "t";
            retrofit = new Retrofit.Builder()
                    .baseUrl("h" + t + "t" + s + "/" + gett43() + "." + (INT - 20) + get546() + "irham" + getString() + "om")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static String getString() {
        return ".c";
    }

    private static String gett43() {
        StringBuilder x = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            x.append("w");
        }
        return x.toString();
    }

    private static String get546() {
        return "d";
    }

    private static String getX() {
        return "f9c" + (INT + 17) + "fd";
    }

    public static StickerApi api() {
        return getRetrofit()
                .create(StickerApi.class);
    }

}
