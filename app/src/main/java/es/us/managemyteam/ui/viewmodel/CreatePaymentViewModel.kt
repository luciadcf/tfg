package es.us.managemyteam.ui.viewmodel

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paypal.android.sdk.payments.ProofOfPayment
import es.managemyteam.app.R
import es.us.managemyteam.contract.PaypalInterface
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.CreatePaymentUc
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePaymentViewModel(
    private val paypalInterface: PaypalInterface,
    private val createPaymentUc: CreatePaymentUc
) : ViewModel(), PaypalInterface.PaypalResultListener {

    private val paymentProofData = CustomMediatorLiveData<Resource<Boolean>>()


    init {
        paypalInterface.setResultListener(this)
    }

    fun getPaypalData() = paymentProofData.liveData()

    fun goToPaypal(
        fragment: Fragment,
        price: String,
        concept: String
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            if (validateForm(price, concept)) {
                paypalInterface.goToPaypal(fragment, price, concept)
            } else {
                paymentProofData.setData(Resource.error(Error(R.string.registration_error_empty_fields)))
            }
        }
    }

    private fun validateForm(price: String, concept: String): Boolean {
        return !price.isBlank() && !concept.isBlank()
    }

    fun catchResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModelScope.launch(Dispatchers.Main) {
            paypalInterface.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun sendToServer(concept: String, quantity: String, paymentProof: ProofOfPayment) {
        viewModelScope.launch {
            paymentProofData.setData(Resource.loading())
            withContext(Dispatchers.IO) {
                paymentProofData.changeSource(
                    Dispatchers.Main,
                    createPaymentUc(concept, quantity, paymentProof)
                )
            }
        }
    }

    override fun onPaymentOk(concept: String, quantity: String, paymentProof: ProofOfPayment) {
        sendToServer(concept, quantity, paymentProof)
    }

    override fun onPaymentError() {
        paymentProofData.setData(Resource.error(Error(serverErrorMessage = "Ha ocurrido un error con su pago")))
    }

    override fun onPaymentCancelled() {
        paymentProofData.setData(Resource.error(Error(serverErrorMessage = "Se ha cancelado su pago")))
    }

}