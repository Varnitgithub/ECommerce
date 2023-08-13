package com.varnittyagi.ecart.activities

import Repository
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.varnittyagi.ecart.R
import com.varnittyagi.ecart.databinding.ActivityProductDetailsBinding
import com.varnittyagi.ecart.databinding.AlertDialogBinding
import com.varnittyagi.ecart.databinding.InternetAlertDialogBinding
import com.varnittyagi.ecart.models.firebasemodel.FirebaseModel
import com.varnittyagi.ecart.models.productmodel.Product
import com.varnittyagi.ecart.retrofitobject.RetrofitInstance
import com.varnittyagi.ecart.viewmodel.ECart_View_Model
import com.varnittyagi.ecart.viewmodel.news_ViewModel_Factory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetails : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var mViewmodel: ECart_View_Model
    private lateinit var repository: Repository
    private var totalNoOfSelectedItems: Double = 0.0
    private var mProductName: String? = null
    private var productId: String? = null
    private lateinit var internetdialog: AlertDialog
    private lateinit var internetalertDialogBinding: InternetAlertDialogBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        //   setContentView(R.layout.activity_product_details)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details)
        super.onCreate(savedInstanceState)
        binding.progressBarProDetails.visibility = View.VISIBLE

        firebaseFirestore = FirebaseFirestore.getInstance()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        getProductDetails()

    }

    private fun getProductDetails() {
        binding.progressBarProDetails.visibility = View.VISIBLE

        val productList = intent.getParcelableArrayListExtra<Product>("ArrayList<Product>")
        if (productList!=null){


        val API_BASE_URL = "https://tutorials.mianasad.com/ecommerce"
        val PRODUCT_IMAGE_URL = "/uploads/product/"

        repository = Repository(this, RetrofitInstance.apiInterfaceInstance)
        mViewmodel = ViewModelProvider(
            this,
            news_ViewModel_Factory(repository)
        ).get(ECart_View_Model::class.java)

        if (isInternetAvailable(this)) {
            CoroutineScope(Dispatchers.IO).launch {
                mViewmodel.productDetail(productList[0].id)
            }
            }
            mViewmodel.product_detailData.observe(this) {
                it.apply {

                    it.data?.product?.apply {
                        mProductName = name
                        Glide.with(this@ProductDetails)
                            .load(API_BASE_URL + PRODUCT_IMAGE_URL + image)
                            .into(binding.imageView2)
                        supportActionBar?.title = mProductName
                        val totalNoOfItems = 0
                        val discountPrice = price_discount * price
                        if (discountPrice / 100 == 0) {
                            binding.productDiscountPrice.text = price.toString()

                        } else {
                            val newPrice = price - discountPrice / 100
                            binding.productDiscountPrice.text = newPrice.toString()

                        }

                        binding.productName.text = name
                        binding.productPrice.text = "$ $price"
                        val textWithStrikeThrough = "$ $price"
                        binding.productPrice.text = textWithStrikeThrough
                        binding.productPrice.paintFlags =
                            binding.productPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        binding.Discount.text = "${price_discount.toString()}% Off"
                        val description = Html.fromHtml(description)
                        binding.productDescription.text = description
                        binding.progressBarProDetails.visibility = View.GONE




                        binding.addToCartButton.setOnClickListener {
                            binding.progressBarProDetails.visibility = View.VISIBLE
                                    val mData = FirebaseModel(
                                        image,
                                        mProductName!!,
                                        price.toString(),
                                        totalNoOfSelectedItems.toInt().toString(),
                                        id.toString()
                                    )
                                    firebaseFirestore.collection("products")
                                        .document(id.toString()).set(mData)
                                        .addOnSuccessListener {
                                            val intent = Intent(
                                                this@ProductDetails,
                                                CartActivity::class.java
                                            )
                                            intent.putExtra("productId", id)
                                            startActivity(intent)
                                            binding.progressBarProDetails.visibility = View.GONE
                                        }
                                        .addOnFailureListener {


                                        }
                            }

                        }
                    }
                }
            }
        }



    private fun openInternetSettings(context: Context) {
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        context.startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        binding.progressBarProDetails.visibility = View.VISIBLE


        if (item.itemId == R.id.myCart) {
            startActivity(Intent(this, CartActivity::class.java))
            binding.progressBarProDetails.visibility = View.GONE
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        binding.progressBarProDetails.visibility = View.VISIBLE

        finish()
        binding.progressBarProDetails.visibility = View.GONE

        return super.onSupportNavigateUp()
    }

    private fun refreshScreen() {
        recreate()
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        // Check for null in case the service is unavailable on the device.
        connectivityManager?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val networkCapabilities = it.getNetworkCapabilities(it.activeNetwork)
                return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    ?: false
            } else {
                // For devices running versions below Android M (API level 23)
                val activeNetworkInfo = it.activeNetworkInfo
                return activeNetworkInfo != null && activeNetworkInfo.isConnected
            }
        }

        return false
    }
}