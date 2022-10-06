package com.greedygame.greedynews.network;

import com.greedygame.greedynews.model.BaseModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {


    @GET("v2/top-headlines")
    Call<BaseModel> getHeadline(
            @Query("apiKey") String apiKey,
            @Query("country") String country,
            @Query("category") String category,
            @Query("language") String language,
            @Query("q") String q,
            @Query("pageSize") String pageSize
    );

    @GET("v2/everything")
    Call<BaseModel> getEverything(
            @Query("apiKey") String apiKey,
            @Query("qInTitle") String qInTitle,
            @Query("sources") String sources,
            @Query("domains") String domains,
            @Query("q") String q
    );

}
