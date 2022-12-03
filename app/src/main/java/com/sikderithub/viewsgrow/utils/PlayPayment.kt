package com.sikderithub.viewsgrow.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.android.billingclient.api.*
import com.google.firebase.crashlytics.internal.model.ImmutableList
import java.util.EnumSet.of
import java.util.List.of

class PlayPayment(val context: Context) {
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    //handlePurchase(purchase)
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.

            } else {
                Toast.makeText(context, "err", Toast.LENGTH_SHORT).show()
                // Handle any other error codes.
            }
        }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    val queryProductDetailsParams =
        QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.from(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("product_id_example")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()))
            .build()

    init {

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    fun lunch(){
        Toast.makeText(context, "Launch", Toast.LENGTH_SHORT).show()
        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            // check billingResult
            // process returned productDetailsList

            if(productDetailsList.size==0){
                Toast.makeText(context, "Empty Product", Toast.LENGTH_SHORT).show()
                return@queryProductDetailsAsync
            }

            val productDetails = productDetailsList.get(0)

            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                    .setProductDetails(productDetails)
                    // to get an offer token, call ProductDetails.subscriptionOfferDetails()
                    // for a list of offers that are available to the user
                    //.setOfferToken(productDetails.subscriptionOfferDetails[0].offerToken)
                    .build()
            )

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            val billingResult = billingClient.launchBillingFlow((context as Activity), billingFlowParams)
        }




    }
}