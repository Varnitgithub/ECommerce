package com.varnittyagi.ecart.models.checkoutmodel

data class ProductOrder(
    val address: String,
    val buyer: String,
    val comment: String,
    val created_at: Long,
    val date_ship: Long,
    val email: String,
    val last_update: Long,
    val phone: String,
    val serial: String,
    val shipping: String,
    val shipping_location: String,
    val shipping_rate: String,
    val status: String,
    val tax: Double,
    val total_fees: Double
)