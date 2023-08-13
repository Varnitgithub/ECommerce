package com.varnittyagi.ecart.apiinterface

import com.varnittyagi.ecart.models.allcategorydetails.AllCategoryDetails
import com.varnittyagi.ecart.models.categorymodel.CategorisedData
import com.varnittyagi.ecart.models.checkoutmodel.CheckoutModel
import com.varnittyagi.ecart.models.checkoutresponsemodel.CheckoutResponseModel
import com.varnittyagi.ecart.models.offers.OfferData
import com.varnittyagi.ecart.models.productdetailmodel.ProductDetModel
import com.varnittyagi.ecart.models.productmodel.ProductData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {


    @GET("services/listCategory")
    fun getCategorisedList(): Call<CategorisedData>

    @GET("services/listProduct")
    fun getProductList(): Call<ProductData>

    @GET("services/getProductDetails")
    fun getProductDetail(@Query("id") id: Int): Call<ProductDetModel>

    @GET("services/listFeaturedNews")
    fun getOfferList(): Call<OfferData>

    @Headers("Content-Type: application/json")
    @POST("services/submitProductOrder")
    fun checkoutModelData(
        @Header("security") securityKey: String,
        @Body checkoutModel: CheckoutModel
    ): Call<CheckoutResponseModel>

    @GET("services/listProduct")
    fun getCategoryDetails(@Query("category_id") category_id: Int): Call<AllCategoryDetails>
}