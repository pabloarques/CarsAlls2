package com.example.carsalls.ValenBisi;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ValenBisiApi {
    @GET("valenbisi")
    Call<CitibikesResult> status();
}
