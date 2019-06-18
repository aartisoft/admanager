package com.admanager.wastickers.api;

import com.admanager.wastickers.model.PackageModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface StickerApi {
    @GET("v1/stickers")
    Call<List<PackageModel>> getPacks();

    @GET("v1/stickers?id=id")
    Call<List<String>> getStickers(@Path("id") String id);

}
