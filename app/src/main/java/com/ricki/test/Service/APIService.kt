package com.ricki.test.Service

import com.ricki.test.Constants.GlobalVariable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIService {
    // For Http Request
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(GlobalVariable().baseUrl)
        .build()
}