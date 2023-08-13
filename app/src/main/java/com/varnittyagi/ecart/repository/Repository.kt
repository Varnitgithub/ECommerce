import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.varnittyagi.ecart.apiinterface.ApiInterface
import com.varnittyagi.ecart.errorhandling.Response
import com.varnittyagi.ecart.models.allcategorydetails.AllCategoryDetails
import com.varnittyagi.ecart.models.categorymodel.CategorisedData
import com.varnittyagi.ecart.models.categorymodel.Category
import com.varnittyagi.ecart.models.checkoutmodel.CheckoutModel
import com.varnittyagi.ecart.models.checkoutresponsemodel.CheckoutResponseModel
import com.varnittyagi.ecart.models.offers.OfferData
import com.varnittyagi.ecart.models.productdetailmodel.ProductDetModel
import com.varnittyagi.ecart.models.productmodel.Product
import com.varnittyagi.ecart.models.productmodel.ProductData
import retrofit2.Call
import retrofit2.Callback

class Repository(private val context: Context, private val apiInterface: ApiInterface) {

    // ... (other code)

    private val offerList = MutableLiveData<Response<OfferData>>()
    val offerListData: LiveData<Response<OfferData>>
        get() = offerList

    private val mainList = MutableLiveData<Response<List<Product>>>()
    val mainListData: LiveData<Response<List<Product>>>
        get() = mainList

    private val categorisedData = MutableLiveData<Response<List<Category>>>()
    val categorisedLiveData: LiveData<Response<List<Category>>>
        get() = categorisedData

    private val productDetail = MutableLiveData<Response<ProductDetModel>>()
    val productDetailsLiveData: LiveData<Response<ProductDetModel>>
        get() = productDetail

    private val checkout = MutableLiveData<Response<CheckoutResponseModel>>()
    val checkoutProduct: LiveData<Response<CheckoutResponseModel>>
        get() = checkout


    private val categoryDetail = MutableLiveData<Response<List<com.varnittyagi.ecart.models.allcategorydetails.Product>>>()
    val categoryDetailsLiveData: LiveData<Response<List<com.varnittyagi.ecart.models.allcategorydetails.Product>>>
        get() = categoryDetail




    suspend fun categorised_Data() {
        apiInterface.getCategorisedList().enqueue(object : Callback<CategorisedData?> {
            override fun onResponse(
                call: Call<CategorisedData?>,
                response: retrofit2.Response<CategorisedData?>
            ) {
                if (response.isSuccessful) {
                    categorisedData.postValue(Response.Success(response.body()?.categories!!))
                } else {
                    categorisedData.postValue(Response.Error("Failed to fetch categorised data"))
                }
            }

            override fun onFailure(call: Call<CategorisedData?>, t: Throwable) {
                Log.d(TAG, "onFailure: failure")
                categorisedData.postValue(Response.Error("Failed to fetch categorised data"))
            }
        })
    }

    suspend fun offerList_Data() {
        apiInterface.getOfferList().enqueue(object : Callback<OfferData?> {
            override fun onResponse(
                call: Call<OfferData?>,
                response: retrofit2.Response<OfferData?>
            ) {
                if (response.isSuccessful) {
                    offerList.postValue(Response.Success(response.body()))
                } else {
                    offerList.postValue(Response.Error("Failed to fetch offer list"))
                }
            }

            override fun onFailure(call: Call<OfferData?>, t: Throwable) {
                Log.d(TAG, "onFailure: failure")
                offerList.postValue(Response.Error("Failed to fetch offer list"))
            }
        })
    }

    suspend fun mainList_Data() {
        apiInterface.getProductList().enqueue(object : Callback<ProductData?> {
            override fun onResponse(
                call: Call<ProductData?>,
                response: retrofit2.Response<ProductData?>
            ) {
                if (response.isSuccessful) {
                    mainList.postValue(Response.Success(response.body()?.products))
                } else {
                    mainList.postValue(Response.Error("Failed to fetch main list"))
                }
            }

            override fun onFailure(call: Call<ProductData?>, t: Throwable) {
                Log.d(TAG, "onFailure: failure")
                mainList.postValue(Response.Error("Failed to fetch main list"))
            }
        })
    }

    suspend fun productDetails_Data(id: Int) {
        apiInterface.getProductDetail(id).enqueue(object : Callback<ProductDetModel?> {
            override fun onResponse(
                call: Call<ProductDetModel?>,
                response: retrofit2.Response<ProductDetModel?>
            ) {
                if (response.isSuccessful) {
                    productDetail.postValue(Response.Success(response.body()))
                } else {
                    productDetail.postValue(Response.Error("Failed to fetch product details"))
                }
            }

            override fun onFailure(call: Call<ProductDetModel?>, t: Throwable) {
                Log.d(TAG, "onFailure: failure")
                productDetail.postValue(Response.Error("Failed to fetch product details"))
            }
        })
    }

    fun checkoutFunction(securityKey: String, checkoutModel: CheckoutModel) {
        apiInterface.checkoutModelData(securityKey, checkoutModel)
            .enqueue(object : Callback<CheckoutResponseModel?> {
                override fun onResponse(
                    call: Call<CheckoutResponseModel?>,
                    response: retrofit2.Response<CheckoutResponseModel?>
                ) {
                    if (response.isSuccessful) {
                        checkout.postValue(Response.Success(response.body()))
                    } else {
                        checkout.postValue(Response.Error("Checkout failed"))
                    }
                }

                override fun onFailure(call: Call<CheckoutResponseModel?>, t: Throwable) {
                    Log.d(TAG, "onFailure: failure")
                    checkout.postValue(Response.Error("Checkout failed"))
                }
            })
    }

    fun categoryDetails_Data(category_id: Int) {
        apiInterface.getCategoryDetails(category_id).enqueue(object : Callback<AllCategoryDetails?> {
            override fun onResponse(
                call: Call<AllCategoryDetails?>,
                response: retrofit2.Response<AllCategoryDetails?>
            ) {
                if (response.isSuccessful) {

                    categoryDetail.postValue(Response.Success(response.body()?.products))
                } else {
                    categoryDetail.postValue(Response.Error("Failed to fetch product details"))
                }
            }

            override fun onFailure(call: Call<AllCategoryDetails?>, t: Throwable) {
                Log.d(TAG, "onFailure: failure")
                categoryDetail.postValue(Response.Error("Failed to fetch product details"))
            }
        })
    }
}
