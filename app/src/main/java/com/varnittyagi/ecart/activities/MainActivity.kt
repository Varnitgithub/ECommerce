package com.varnittyagi.ecart.activities

import Repository
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.varnittyagi.ecart.R
import com.varnittyagi.ecart.adapters.CategorisedAdapter
import com.varnittyagi.ecart.adapters.MainAdapter
import com.varnittyagi.ecart.databinding.ActivityMainBinding
import com.varnittyagi.ecart.databinding.InternetAlertDialogBinding
import com.varnittyagi.ecart.models.categorymodel.Category
import com.varnittyagi.ecart.models.productmodel.Product
import com.varnittyagi.ecart.retrofitobject.RetrofitInstance
import com.varnittyagi.ecart.viewmodel.ECart_View_Model
import com.varnittyagi.ecart.viewmodel.news_ViewModel_Factory
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.util.Calendar

class MainActivity : AppCompatActivity(), CategorisedAdapter.OnItemClickListener,
    MainAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var categorisedAdapter: CategorisedAdapter
    private lateinit var mainAdapter: MainAdapter
    private lateinit var mViewmodel: ECart_View_Model
    private lateinit var repository: Repository
    private lateinit var internetdialog: AlertDialog
    private lateinit var internetalertDialogBinding: InternetAlertDialogBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
//        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolBar)
        binding.progressBarMain.visibility = View.VISIBLE


val humberburgIcon = binding.toolBar.navigationIcon
        drawerLayout = findViewById(R.id.drawer_layout)


        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                android.R.id.home -> {
                    drawerLayout.openDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }




        val currenttime = Calendar.getInstance().timeInMillis

        categorisedAdapter = CategorisedAdapter(this, this)
        mainAdapter = MainAdapter(this, this)

        repository = Repository(this, RetrofitInstance.apiInterfaceInstance)
        mViewmodel = ViewModelProvider(
            this,
            news_ViewModel_Factory(repository)
        ).get(ECart_View_Model::class.java)

        internetalertDialogBinding = InternetAlertDialogBinding.inflate(
            LayoutInflater.from(this)
        )

        internetdialog =
            AlertDialog.Builder(this)
                .setView(internetalertDialogBinding.root)
                .create()

        if (isInternetAvailable(this)) {
            binding.progressBarMain.visibility = View.VISIBLE
            categorisedItems()
            mainItems()
            corouselItems()
            binding.progressBarMain.visibility = View.GONE
        } else {
            internetdialog.window?.setBackgroundDrawable(ColorDrawable(android.R.color.transparent))
            internetdialog.show()
            internetalertDialogBinding.retryDialog.setOnClickListener {
                refreshScreen()
                //openInternetSettings(this)
                internetdialog.dismiss()
            }
            internetalertDialogBinding.cancelDialog.setOnClickListener {
                internetdialog.cancel()
            }
        }
    }

    private fun openInternetSettings(context: Context) {
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        context.startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun mainItems() {
        binding.progressBarMain.visibility = View.VISIBLE

        binding.mainItemsRecyclerview.layoutManager = GridLayoutManager(this, 2)
        binding.mainItemsRecyclerview.adapter = mainAdapter
        mViewmodel.mainList_Live_Data.observe(this) {
            try {
                mainAdapter.updateList(it.data as ArrayList<Product>)
                binding.progressBarMain.visibility = View.GONE


            } catch (e: Exception) {
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun categorisedItems() {
        binding.progressBarMain.visibility = View.VISIBLE

        binding.categoriesRecyclerview.layoutManager = GridLayoutManager(this, 4)
        binding.categoriesRecyclerview.adapter = categorisedAdapter
        mViewmodel.categorised_Live_Data.observe(this) {
            try {
                categorisedAdapter.updateList(it.data as ArrayList<Category>)
                binding.progressBarMain.visibility = View.GONE

            } catch (e: Exception) {
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun corouselItems() {
        binding.progressBarMain.visibility = View.VISIBLE

        val OFFER_BASE_URL = "https://tutorials.mianasad.com/ecommerce/"
        val OFFER_END_URL = "uploads/news/"
        /// val carousel: ImageCarousel = findViewById(R.id.carousel)
        binding.carousel.registerLifecycle(lifecycle)
        val list = mutableListOf<CarouselItem>()
        mViewmodel.offerListData.observe(this) {
            try {
                val size: Int? = it.data?.news_infos?.size
                for (i in 0 until size!!) {

                    list.add(CarouselItem(OFFER_BASE_URL + OFFER_END_URL + it.data.news_infos[i].image))

                }
                binding.carousel.setData(list)
                binding.progressBarMain.visibility = View.GONE

            } catch (e: Exception) {
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }


    }



    override fun onProductItemClicks(modelClass: ArrayList<Product>, position: Int) {
        binding.progressBarMain.visibility = View.VISIBLE

        val productList = ArrayList<Product>()
        productList.add(modelClass[position])

        val intent = Intent(this,ProductDetails::class.java)
        intent.putParcelableArrayListExtra("ArrayList<Product>",productList)
        startActivity(intent)
        binding.progressBarMain.visibility = View.GONE


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

    override fun onItemClick(modelClass: ArrayList<Category>, position: Int) {
        binding.progressBarMain.visibility = View.VISIBLE

        val intent = Intent(this,AllCategoryItems::class.java)
        intent.putExtra("category_id",modelClass[position].id)
        startActivity(intent)
        binding.progressBarMain.visibility = View.GONE

    }

}



