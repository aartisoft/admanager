package com.admanager.wastickers.api;

import com.admanager.wastickers.model.CategoryModel;
import com.admanager.wastickers.model.PackageModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StickerApi {
    @GET("v1/stickers")
    Call<List<PackageModel>> getPacks(@Query("category") String category);

    @GET("v1/categories")
    Call<List<CategoryModel>> getCategories();

}
