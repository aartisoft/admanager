package com.admanager.colorcallscreen.api;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CallScreenApi {
    @GET("v1/bg?_l=100&_s=popularity,desc")
    Observable<List<BgModel>> getBgs(@Query("_p") int p, @Query("category") String category);

    @GET("v1/categories?_l=100")
    Observable<List<CategoryModel>> getCategories();

}