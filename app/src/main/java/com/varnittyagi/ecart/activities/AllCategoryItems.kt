package com.varnittyagi.ecart.activities

import Repository
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.varnittyagi.ecart.R
import com.varnittyagi.ecart.adapters.AllCategoryAdapter
import com.varnittyagi.ecart.databinding.ActivityAllCategoryItemsBinding
//import com.varnittyagi.ecart.databinding.ActivityAllCategoryItemsBinding
import com.varnittyagi.ecart.retrofitobject.RetrofitInstance
import com.varnittyagi.ecart.viewmodel.ECart_View_Model
import com.varnittyagi.ecart.viewmodel.news_ViewModel_Factory

class AllCategoryItems : AppCompatActivity(),
    AllCategoryAdapter.OnItemClickListener {
    private lateinit var allCategoryAdapter: AllCategoryAdapter
    private lateinit var binding: ActivityAllCategoryItemsBinding
    private lateinit var mViewmodel: ECart_View_Model
    private lateinit var repository: Repository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_all_category_items)
        binding = DataBindingUtil.setContentView(
            this@AllCategoryItems,
            R.layout.activity_all_category_items
        )
        binding.progressBarAllCategory.visibility = View.VISIBLE

        repository = Repository(this, RetrofitInstance.apiInterfaceInstance)
        mViewmodel = ViewModelProvider(
            this,
            news_ViewModel_Factory(repository)
        )[ECart_View_Model::class.java]
        allCategoryAdapter = AllCategoryAdapter(this, this)
        val category_id = intent.getIntExtra("category_id", 1)

        mViewmodel.categoryDetail(category_id)


        mainItems()
        binding.progressBarAllCategory.visibility = View.GONE

    }
    private fun mainItems() {

        binding.progressBarAllCategory.visibility = View.VISIBLE
        binding.allCategoryRecycler.layoutManager = LinearLayoutManager(this)
        binding.allCategoryRecycler.adapter = allCategoryAdapter
        mViewmodel.category_detailData.observe(this) {

            try {
                allCategoryAdapter.updateList(it.data as ArrayList<com.varnittyagi.ecart.models.allcategorydetails.Product>)
                //allCategoryAdapter.updateList(it.data as ArrayList<Product>)
                binding.progressBarAllCategory.visibility = View.VISIBLE


            } catch (e: Exception) {
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onItemClicks(
        modelClass: ArrayList<com.varnittyagi.ecart.models.allcategorydetails.Product>,
        position: Int
    ) {
        binding.progressBarAllCategory.visibility = View.VISIBLE



        binding.progressBarAllCategory.visibility = View.GONE

    }


}