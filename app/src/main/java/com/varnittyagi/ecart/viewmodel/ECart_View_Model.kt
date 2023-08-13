package com.varnittyagi.ecart.viewmodel

import Repository
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varnittyagi.ecart.errorhandling.Response
import com.varnittyagi.ecart.models.allcategorydetails.AllCategoryDetails
import com.varnittyagi.ecart.models.categorymodel.Category
import com.varnittyagi.ecart.models.checkoutmodel.CheckoutModel
import com.varnittyagi.ecart.models.checkoutresponsemodel.CheckoutResponseModel
import com.varnittyagi.ecart.models.offers.OfferData
import com.varnittyagi.ecart.models.productdetailmodel.ProductDetModel
import com.varnittyagi.ecart.models.productmodel.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ECart_View_Model(private val repository: Repository) : ViewModel() {

    val categorised_Live_Data: LiveData<Response<List<Category>>> = repository.categorisedLiveData
    val mainList_Live_Data: LiveData<Response<List<Product>>> = repository.mainListData
    val product_detailData: LiveData<Response<ProductDetModel>> = repository.productDetailsLiveData
    val category_detailData: LiveData<Response<List<com.varnittyagi.ecart.models.allcategorydetails.Product>>> = repository.categoryDetailsLiveData
    val offerListData: LiveData<Response<OfferData>> = repository.offerListData
    val CheckoutResponseModelData: LiveData<Response<CheckoutResponseModel>> = repository.checkoutProduct

    /*val live_get_Vehicle_News: LiveData<News_ModelX> = repository.live_get_Vehicle_News
    val live_get_Sourced_News: LiveData<News_ModelX> = repository.live_get_Sourced_News
    val live_get_mobile_News: LiveData<News_ModelX> = repository.live_get_mobile_News*/


    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.categorised_Data()
            repository.mainList_Data()
            repository.offerList_Data()
        }

        /*   repository.get_Vehicle_News("hyundai", "2023-05-11", "publishedAt")
           repository.get_Sourced_News("the-washington-post")
           repository.get_mobile_News("xiaomi", "2023-06-10", "2023-06-10", "popularity")*/

    }
    suspend fun productDetail(id:Int){
        repository.productDetails_Data(id)

    }
 fun checkoutFunc( securityKey: String,
                          checkoutModel: CheckoutModel
){
    repository.checkoutFunction(securityKey,checkoutModel)
}
    fun categoryDetail(category_id:Int){
        repository.categoryDetails_Data(category_id)

    }
}
