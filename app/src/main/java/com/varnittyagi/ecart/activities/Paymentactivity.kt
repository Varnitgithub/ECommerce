package com.varnittyagi.ecart.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.varnittyagi.ecart.R
import im.delight.android.webview.AdvancedWebView

class Paymentactivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paymentactivity)
        supportActionBar?.title = "Payment "
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val ordernumber = intent.getStringExtra("orderNumber")

        val webView = findViewById<AdvancedWebView>(R.id.webview)

        webView.setMixedContentAllowed(true)
        webView.loadUrl("https://tutorials.mianasad.com/ecommerce/services/paymentPage?code=$ordernumber")

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()

    }

}