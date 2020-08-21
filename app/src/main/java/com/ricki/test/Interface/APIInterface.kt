package com.ricki.test.Interface

import com.ricki.test.Model.UserModel
import com.ricki.test.Service.APIService
import retrofit2.http.GET
import retrofit2.http.Query

interface APIInterface {
    @GET("search/users")
    suspend fun getAllUser(@Query("q") keyword: String, @Query("page") page: Int, @Query("per_page") per_page: Int): UserModel

    object APIAllServices {
        val retrofitService = APIService().retrofit.create(APIInterface::class.java)
    }
}