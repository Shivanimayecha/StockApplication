package com.hd.nature.stockapplication.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {


    @GET("stocktips")
    Call<String> getDetails();

    @GET("newsstock")
    Call<String> getNews();
}
