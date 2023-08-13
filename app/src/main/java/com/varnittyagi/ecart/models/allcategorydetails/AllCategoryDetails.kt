package com.varnittyagi.ecart.models.allcategorydetails

data class AllCategoryDetails(
    val count: Int,
    val count_total: Int,
    val pages: Int,
    val products: List<Product>,
    val status: String
)