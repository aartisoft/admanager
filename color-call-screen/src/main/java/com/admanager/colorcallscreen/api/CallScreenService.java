package com.admanager.colorcallscreen.api;

import android.content.Context;
import android.util.Base64;

import com.admanager.colorcallscreen.R;
import com.admanager.core.Dirham;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CallScreenService {
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
                    .baseUrl(s)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    public static CallScreenApi api(Context context) {
        return getRetrofit(Dirham.url(context), get00ooOoOOo1(context))
                .create(CallScreenApi.class);
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

}
