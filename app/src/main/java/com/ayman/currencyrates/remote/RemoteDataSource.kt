package com.ayman.currencyrates.remote

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteDataSource {

    @GET("/latest")
    fun getRates(@Query("base") currencyType: String): Single<String>

}