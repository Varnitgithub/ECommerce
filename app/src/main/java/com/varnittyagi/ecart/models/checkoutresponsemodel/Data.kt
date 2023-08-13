package com.varnittyagi.ecart.models.checkoutresponsemodel

data class Data(
    val address: String,
    val buyer: String,
    val code: String,
    val comment: String,
    val created_at: Long,
    val date_ship: Long,
    val email: String,
    val id: Int,
    val last_update: Long,
    val payment: String,
    val payment_data: String,
    val payment_status: String,
    val phone: String,
    val serial: String,
    val shipping: String,
    val shipping_location: String,
    val shipping_rate: Int,
    val status: String,
    val tax: Int,
    val total_fees: Int
)