package es.us.managemyteam.manager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import com.paypal.android.sdk.payments.ShippingAddress
import es.us.managemyteam.contract.PaypalInterface
import es.us.managemyteam.data.model.PaypalConfigBo
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.viewmodel.AdminPaypalViewModel
import org.json.JSONException
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal

private const val REQUEST_CODE_PAYMENT = 1

private const val CONFIG_CLIENT_ID =
    "AYxExxPewJCCP8n5rihDlSRFjLeuKT4ksM7zEZxdS0Q85--P70l3M0OIkRy1w6dG56G40G85RKX7hqo_"

private const val CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK

class PaypalManager : PaypalInterface {

    private val tag = javaClass.simpleName
    private val config = PayPalConfiguration()
        .environment(CONFIG_ENVIRONMENT)
        .clientId(CONFIG_CLIENT_ID)
        .merchantName("ManageMyTeam")
        .sandboxUserPassword("eh^lfV4_")
        .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
        .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"))
    private var resultListener: PaypalInterface.PaypalResultListener? = null


    override fun initialize(context: Context) {
        context.startService(Intent(context, PayPalService::class.java).apply {
            putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        })

    }

    override fun goToPaypal(
        fragment: Fragment,
        price: String,
        itemName: String,
        isoCode: String
    ) {
        val viewModel: AdminPaypalViewModel by fragment.viewModel()
        viewModel.getPaypalConfigData().removeObservers(fragment)
        viewModel.getPaypalConfigData().observe(fragment, object :
            ResourceObserver<PaypalConfigBo>() {
            override fun onSuccess(response: PaypalConfigBo?) {
                response?.let {
                    startPaypalService(fragment, getItemToBuy(price, itemName, isoCode, it))
                }
            }

            override fun onError(error: Error) {
                super.onError(error)
                startPaypalService(fragment, getItemToBuy(price, itemName, isoCode))
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                val confirm =
                    data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                if (confirm != null) {
                    try {
                        Log.i(tag, confirm.toJSONObject().toString(4))
                        Log.i(tag, confirm.payment.toJSONObject().toString(4))

                        resultListener?.onPaymentOk(
                            confirm.payment.toJSONObject().getString("short_description"),
                            confirm.payment.toJSONObject().getString("amount").plus(" ").plus(
                                confirm.payment.toJSONObject().getString("currency_code")
                            ),
                            confirm.proofOfPayment
                        )


                    } catch (e: JSONException) {
                        Log.e(tag, "an extremely unlikely failure occurred: ", e)
                        resultListener?.onPaymentError()
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(tag, "The user canceled.")
                resultListener?.onPaymentCancelled()
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                    tag,
                    "An invalid Payment or PayPalConfiguration was submitted. Please see the docs."
                )
                resultListener?.onPaymentError()
            }

        }
    }

    override fun setResultListener(resultListener: PaypalInterface.PaypalResultListener) {
        this.resultListener = resultListener
    }

    private fun startPaypalService(fragment: Fragment, paypalPayment: PayPalPayment? = null) {
        fragment.startActivityForResult(
            Intent(
                fragment.context,
                PaymentActivity::class.java
            ).apply {
                putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
                putExtra(PaymentActivity.EXTRA_PAYMENT, paypalPayment)
            }, REQUEST_CODE_PAYMENT
        )
    }

    private fun getItemToBuy(
        price: String,
        itemName: String,
        isoCode: String,
        paypalConfigBo: PaypalConfigBo? = null
    ): PayPalPayment {
        return PayPalPayment(
            BigDecimal(price), isoCode, itemName,
            PayPalPayment.PAYMENT_INTENT_SALE
        ).apply {
            addAppProvidedShippingAddress(this, paypalConfigBo)
        }
    }

    /*
    * Add app-provided shipping address to payment
    */
    private fun addAppProvidedShippingAddress(
        paypalPayment: PayPalPayment,
        paypalConfigBo: PaypalConfigBo? = null
    ) {
        val shippingAddress = ShippingAddress()
            .recipientName(paypalConfigBo?.recipient ?: "")
            .line1(paypalConfigBo?.address ?: "")
            .city(paypalConfigBo?.city ?: "")
            .state(paypalConfigBo?.province ?: "")
            .postalCode(paypalConfigBo?.postcode ?: "")
            .countryCode("ES")
        paypalPayment.providedShippingAddress(shippingAddress)
    }

}