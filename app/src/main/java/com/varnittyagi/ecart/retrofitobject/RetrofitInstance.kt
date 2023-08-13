package com.varnittyagi.ecart.retrofitobject

import com.varnittyagi.ecart.apiinterface.ApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

private val retrofit by lazy {
    Retrofit.Builder().baseUrl("https://tutorials.mianasad.com/ecommerce/")
        .addConverterFactory(GsonConverterFactory.create()).build()
}
    val  apiInterfaceInstance: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }

}