package com.varnittyagi.ecart.activities

import Repository
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.varnittyagi.ecart.R
import com.varnittyagi.ecart.adapters.CartItemsAdapters
import com.varnittyagi.ecart.databinding.ActivityCheckoutBinding
import com.varnittyagi.ecart.databinding.InternetAlertDialogBinding
import com.varnittyagi.ecart.databinding.OrderDialogBinding
import com.varnittyagi.ecart.models.checkoutmodel.CheckoutModel
import com.varnittyagi.ecart.models.checkoutmodel.ProductOrder
import com.varnittyagi.ecart.models.checkoutmodel.ProductOrderDetail
import com.varnittyagi.ecart.models.firebasemodel.FirebaseModel
import com.varnittyagi.ecart.retrofitobject.RetrofitInstance
import com.varnittyagi.ecart.viewmodel.ECart_View_Model
import com.varnittyagi.ecart.viewmodel.news_ViewModel_Factory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class CheckoutActivity : AppCompatActivity(), CartItemsAdapters.OnincreaseItemClickListener,
    CartItemsAdapters.OndecreaseItemClickListener {
    private lateinit var cartItemsAdapters: CartItemsAdapters
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var allData: ArrayList<FirebaseModel>
    private lateinit var allProductsDetails: ArrayList<ProductOrderDetail>
    private lateinit var checkoutBinding: ActivityCheckoutBinding
    private lateinit var mViewmodel: ECart_View_Model
    private lateinit var repository: Repository
    private lateinit var progressbarcheckout:ProgressBar
    private lateinit var internetdialog: AlertDialog
    private lateinit var internetalertDialogBinding: InternetAlertDialogBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        checkoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_checkout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        progressbarcheckout = findViewById(R.id.progressBarCheckout)
        progressbarcheckout.visibility = View.VISIBLE
        firebaseFirestore = FirebaseFirestore.getInstance()
        allData = ArrayList()
        allProductsDetails = ArrayList()
        cartItemsAdapters = CartItemsAdapters(this, this, this)

        repository = Repository(this, RetrofitInstance.apiInterfaceInstance)
        mViewmodel = ViewModelProvider(
            this,
            news_ViewModel_Factory(repository)
        ).get(ECart_View_Model::class.java)

        val userName = checkoutBinding.userName.text.toString()
        val userEmail = checkoutBinding.userEmail.text.toString()
        val userphoneNumber = checkoutBinding.userphoneNumber.text.toString()
        val userAddress = checkoutBinding.userAddress.text.toString()
        val usershippingDate = checkoutBinding.usershippingDate.text.toString()
        val useradditionalComments = checkoutBinding.useradditionalComments.text.toString()

        if (isInternetAvailable(this)) {
            val currentTimeMills = Calendar.getInstance().timeInMillis
            val subTotal = intent.getStringExtra("totalPrice")
            val productId = intent.getStringExtra("productId")
            checkoutBinding.totalPrice.text = "$ $subTotal"
            if (subTotal != null) {
                val finalPrice = 18 * subTotal.toDouble() / 100
                val mFinalPrice = "$ ${(finalPrice + subTotal.toDouble())}"
                val breakFinalPrice = mFinalPrice.split(".")
                val beforeDecimals = breakFinalPrice[0]
                val afterDecimals = breakFinalPrice[1]
                //    Log.d("TAGGGGGGGGGGGGGG", "onCreatedigit: ${afterDecimals.get(3)}")
                val twodigits = getFirstTwoDigits(afterDecimals)

                if (twodigits == "0" || twodigits=="00"){
                    checkoutBinding.finalPrice.text =
                        "${beforeDecimals}"
                }
                else{
                    checkoutBinding.finalPrice.text =
                        "${beforeDecimals}.${twodigits}"
                }

            } else {

            }
            checkoutBinding.checkOutRecyclerview.layoutManager = LinearLayoutManager(this)
            checkoutBinding.checkOutRecyclerview.adapter = cartItemsAdapters

            var totalSum = 0.0
            progressbarcheckout.visibility = View.VISIBLE

            firebaseFirestore.collection("products")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {

                        val name = document.getString("name")
                        val image = document.getString("image")
                        val price = document.getString("price")
                        val totalNoOfItemSelected = document.getString("totalNoOfItemSelected")
                        val productIde = document.getString("productIde")

                        allData.add(
                            FirebaseModel(
                                image!!,
                                name!!,
                                "$ ${price!!}",
                                totalNoOfItemSelected!!, productIde!!
                            )
                        )
                        allProductsDetails.add(
                            ProductOrderDetail(
                                totalNoOfItemSelected.toDouble().toInt(),
                                currentTimeMills,
                                currentTimeMills,
                                price.toDouble(),
                                productIde,
                                "Sample Product"
                            )

                        )

                        totalSum += price.toDouble()
                        // Do something with the data
                    }
                    cartItemsAdapters.updateList(allData)

                    progressbarcheckout.visibility = View.GONE



                    val dateString = "2023-07-24 12:34:56"
                    val pattern = "yyyy-MM-dd HH:mm:ss"
                    val timeInMillis = dateToLong(dateString, pattern)

                    val productOrder = ProductOrder(
                        userAddress, userName,
                        useradditionalComments,
                        currentTimeMills,
                        timeInMillis,
                        userEmail,
                        currentTimeMills,
                        userphoneNumber,
                        "cab8c1a4e4421a3b",
                        "Standard",
                        "$userAddress India", "10.00", "WAITING", 18.00, 100.00
                    )

                    mViewmodel.checkoutFunc(
                        "secure_code",
                        CheckoutModel(productOrder, allProductsDetails)
                    )

                }
                .addOnFailureListener { e ->
                    // Handle any errors
                    Log.e("FirestoreData", "Error getting documents: ${e.message}")
                }

            checkoutBinding.checkoutContinueButton.setOnClickListener {
                if (checkoutBinding.userName.text.toString().isNotEmpty() &&
                    checkoutBinding.userEmail.text.toString().isNotEmpty() &&
                    checkoutBinding.userphoneNumber.text.toString().isNotEmpty() &&
                    checkoutBinding.userAddress.text.toString().isNotEmpty() &&
                    checkoutBinding.usershippingDate.text.toString().isNotEmpty() &&
                    checkoutBinding.useradditionalComments.text.toString().isNotEmpty()
                ) {
                    mViewmodel.CheckoutResponseModelData.observe(this) {
                        val ordernumber = it.data?.data?.code
                        val orderDialogBinding: OrderDialogBinding = OrderDialogBinding.inflate(
                            LayoutInflater.from(this)
                        )
                        val dialog: AlertDialog =
                            AlertDialog.Builder(this)
                                .setView(orderDialogBinding.root)
                                .create()
                        dialog.window?.setBackgroundDrawable(ColorDrawable(android.R.color.transparent))
                        dialog.show()
                        if (it.data?.status == "success") {

                            orderDialogBinding.productOrderStatus.text = "Your order is successful"
                            orderDialogBinding.productOrderNumber.text = it.data.data.code
                            orderDialogBinding.okDialog.setOnClickListener {
                                dialog.dismiss()
                                val intent = Intent(this, Paymentactivity::class.java)
                                intent.putExtra("orderNumber", ordernumber)
                                startActivity(intent)

                            }
                        } else {
                            orderDialogBinding.productOrderStatus.text = "Your order is failed"
                            orderDialogBinding.productOrderNumber.text = ""
                            orderDialogBinding.okDialog.setOnClickListener {
                                dialog.dismiss()
                            }
                        }

                    }

                } else {
                    Toast.makeText(this, "Please fill all the given details", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        } else {
            internetalertDialogBinding = InternetAlertDialogBinding.inflate(
                LayoutInflater.from(this)
            )

            internetdialog =
                AlertDialog.Builder(this)
                    .setView(internetalertDialogBinding.root)
                    .create()
            internetdialog.window?.setBackgroundDrawable(ColorDrawable(android.R.color.transparent))
            internetdialog.show()
            internetalertDialogBinding.retryDialog.setOnClickListener {
                refreshScreen()
                //  openInternetSettings(this)
                internetdialog.dismiss()
            }
            internetalertDialogBinding.cancelDialog.setOnClickListener {
                internetdialog.cancel()
                Toast.makeText(this, "dialog cancel", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun dateToLong(dateString: String, pattern: String): Long {
        val format = SimpleDateFormat(pattern)
        val date: Date = format.parse(dateString) ?: Date()
        return date.time
    }

    private fun refreshScreen() {
        recreate()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }


    fun onItemClick(modelClass: FirebaseModel, position: Int) {
        TODO("Not yet implemented")
    }

    private fun openInternetSettings(context: Context) {
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        context.startActivity(intent)
    }

    private fun isInternetAvailable(context: Context): Boolean {
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

    override fun onDecCartItemClick(modelClass: ArrayList<FirebaseModel>, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onIncCartItemClick(modelClass: ArrayList<FirebaseModel>, position: Int) {
        TODO("Not yet implemented")
    }

    private fun getFirstTwoDigits(input: String): String {
        return if (input.length >= 2) {
            input.substring(0, 2)
        } else {
            input // Return the entire string if it's shorter than 2 characters
        }
    }


}