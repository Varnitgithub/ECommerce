package com.varnittyagi.ecart.activities

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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.varnittyagi.ecart.R
import com.varnittyagi.ecart.adapters.CartItemsAdapters
import com.varnittyagi.ecart.databinding.InternetAlertDialogBinding
import com.varnittyagi.ecart.models.firebasemodel.FirebaseModel

class CartActivity : AppCompatActivity(), CartItemsAdapters.OnincreaseItemClickListener,
    CartItemsAdapters.OndecreaseItemClickListener {
    private lateinit var cartItemsAdapters: CartItemsAdapters
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var allData: ArrayList<FirebaseModel>
    private lateinit var internetdialog: AlertDialog
    private lateinit var firebaseCollection: CollectionReference
    private lateinit var internetalertDialogBinding: InternetAlertDialogBinding
    private lateinit var progressBarCart: ProgressBar
    private var itemCount = 0
    private var totalSum = 0.0
    private lateinit var cartTotalPrice: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        supportActionBar?.title = "Cart items"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        progressBarCart = findViewById(R.id.progressBarCart)
        progressBarCart.visibility = View.VISIBLE
        itemsPopulatedLoop()

    }

    private fun itemsPopulatedLoop() {
        progressBarCart.visibility = View.VISIBLE

        val cartRecyclerView: RecyclerView = findViewById(R.id.cartRecyclerView)
        cartTotalPrice = findViewById(R.id.cartTotalPrice)

        val cartContinueButton: Button = findViewById(R.id.cartContinueButton)

        if (isInternetAvailable(this)) {
            firebaseFirestore = FirebaseFirestore.getInstance()
            allData = ArrayList()
            cartItemsAdapters = CartItemsAdapters(this, this, this)
            cartRecyclerView.layoutManager = LinearLayoutManager(this)
            cartRecyclerView.adapter = cartItemsAdapters

            firebaseCollection = firebaseFirestore.collection("products")
            totalSum = 0.0



            firebaseCollection
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
                                totalNoOfItemSelected!!,
                                productIde!!
                            )
                        )
                        totalSum += price.replace("$", "").trim().toDouble()
                        // Do something with the data
                    }
                    cartItemsAdapters.updateList(allData)
                    cartTotalPrice.text = " $totalSum"
                    progressBarCart.visibility = View.GONE

                }
                .addOnFailureListener { e ->
                    // Handle any errors
                }



            cartContinueButton.setOnClickListener {
                progressBarCart.visibility = View.VISIBLE

                val intent = Intent(this, CheckoutActivity::class.java)
                intent.putExtra("totalPrice", totalSum.toString())

                startActivity(intent)
                progressBarCart.visibility = View.VISIBLE

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
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cart_items, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        progressBarCart.visibility = View.VISIBLE
        when (item.itemId) {


            R.id.emptycart -> firebaseCollection.get().addOnSuccessListener { querySnapshot ->
                // Step 3: Delete each document
                val deleteTasks: MutableList<Task<Void>> = mutableListOf()
                for (document in querySnapshot) {
                    val deleteTask = document.reference.delete()
                    deleteTasks.add(deleteTask)
                }
                Tasks.whenAllComplete(deleteTasks)
                    .addOnSuccessListener {
                        // All documents deleted successfully
                        println("All items removed from the cart")
                        val emptyList = ArrayList<FirebaseModel>()
                        cartItemsAdapters.updateList(emptyList)
                        cartTotalPrice.text = "0"
                        progressBarCart.visibility = View.GONE
                    }
                    .addOnFailureListener { exception ->
                        // Handle error
                        println("Error removing items: ${exception.message}")
                    }
            }
                .addOnFailureListener { exception ->
                    // Handle error
                    println("Error getting documents: ${exception.message}")
                }


            R.id.help -> {
                progressBarCart.visibility = View.VISIBLE
                Toast.makeText(this, "help", Toast.LENGTH_SHORT).show()
                progressBarCart.visibility = View.GONE

            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        return super.onSupportNavigateUp()
    }

    @SuppressLint("ResourceAsColor")
    fun onCartItemClick(modelClass: ArrayList<FirebaseModel>, position: Int) {
        progressBarCart.visibility = View.VISIBLE

        progressBarCart.visibility = View.GONE

    }

    private fun openInternetSettings(context: Context) {
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        context.startActivity(intent)
    }

    private fun refreshScreen() {
        recreate()
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


    override fun onIncCartItemClick(modelClass: ArrayList<FirebaseModel>, position: Int) {
        progressBarCart.visibility = View.VISIBLE

        allData.clear()
        firebaseFirestore.collection("products")
            .document(modelClass[position].productIde).delete().addOnSuccessListener {
                val emptyList = ArrayList<FirebaseModel>()
                cartItemsAdapters.updateList(emptyList)

            }

        val count = itemCount++
        val updatedData = FirebaseModel(
            modelClass[position].image,
            modelClass[position].name,
            modelClass[position].price,
            count.toString(),
            modelClass[position].productIde
        )
        firebaseFirestore.collection("products")
            .document(modelClass[position].productIde).set(updatedData)
            .addOnSuccessListener {


                firebaseCollection
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
                                    " ${price!!}",
                                    totalNoOfItemSelected!!,
                                    productIde!!
                                )
                            )

                            totalSum += price.replace("$", "").trim().toDouble()

                            // Do something with the data
                        }

                        cartItemsAdapters.updateList(allData)
                        cartTotalPrice.text = "$ ${totalSum}"
                        progressBarCart.visibility = View.GONE

                    }
                    .addOnFailureListener { e ->
                        // Handle any errors
                        Log.e("FirestoreData", "Error getting documents: ${e.message}")
                    }


            }
            .addOnFailureListener {

            }
        //itemsPopulatedLoop()
        //refreshScreen()
    }

    override fun onDecCartItemClick(modelClass: ArrayList<FirebaseModel>, position: Int) {
        progressBarCart.visibility = View.VISIBLE

        firebaseFirestore.collection("products")
            .document(modelClass[position].productIde).delete().addOnSuccessListener {
            }
        val count = itemCount--
        val updatedData = FirebaseModel(
            modelClass[position].image,
            modelClass[position].name,
            modelClass[position].price,
            count.toString(),
            modelClass[position].productIde
        )
//        firebaseFirestore.collection("products")
//            .document(modelClass[position].productIde).set(updatedData)
//            .addOnSuccessListener {
//                Log.d(
//                    "TAGGGGGGGGGGG",
//                    "onData saved on Firebase: Data saved on firebase successfully from count"
//                )
//
        progressBarCart.visibility = View.GONE
//
//            }
//            .addOnFailureListener {
//                Log.d(
//                    "TAGGGGGGGGGGG",
//                    "onData failed on Firebase: Data saved on firebasse failure"
//                )
//
//            }
//        // refreshScreen()
        //itemsPopulatedLoop()
    }
}