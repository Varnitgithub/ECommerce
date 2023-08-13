package com.varnittyagi.ecart.models.productmodel

data class ProductData(
    val count: Int,
    val count_total: Int,
    val pages: Int,
    val products: List<Product>,
    val status: String
)