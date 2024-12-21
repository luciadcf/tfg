package es.us.managemyteam.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import es.us.managemyteam.data.model.PaymentBo
import es.us.managemyteam.data.model.PaypalConfigBo
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.RepositoryUtil
import es.us.managemyteam.repository.util.Resource

interface PaymentRepository {

    suspend fun getMyPayments(): LiveData<Resource<List<PaymentBo>>>

    suspend fun createPayment(payment: PaymentBo): LiveData<Resource<Boolean>>

    suspend fun getPaypalConfig(): LiveData<Resource<PaypalConfigBo>>

    suspend fun createPaypalConfig(paypalConfig: PaypalConfigBo): LiveData<Resource<Boolean>>
}

class PaymentRepositoryImpl : PaymentRepository {

    private val auth = FirebaseAuth.getInstance()
    private val paymentsRef = RepositoryUtil.getDatabaseTable("payments")
    private val paypalConfigRef = RepositoryUtil.getDatabaseTable("paypalConfig")
    private val paymentData = MutableLiveData<Resource<Boolean>>()
    private val myPaymentsData = MutableLiveData<Resource<List<PaymentBo>>>()
    private val paypalConfigData = MutableLiveData<Resource<PaypalConfigBo>>()
    private val createPaypalConfigData = MutableLiveData<Resource<Boolean>>()

    override suspend fun getMyPayments(): LiveData<Resource<List<PaymentBo>>> {
        //myPaymentsData.postValue(null)
        paymentsRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                myPaymentsData.value = Resource.error(Error(serverErrorMessage = error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                myPaymentsData.value =
                    Resource.success(snapshot.children.mapNotNull { it.getValue(PaymentBo::class.java) }
                        .filter { it.userUuid == auth.currentUser?.uid })
            }

        })

        return myPaymentsData
    }

    override suspend fun createPayment(payment: PaymentBo): MutableLiveData<Resource<Boolean>> {
        val newPayment = payment.apply {
            userUuid = auth.currentUser?.uid
        }
        //paymentData.postValue(null)
        paymentsRef.push().setValue(newPayment) { databaseError, ref ->
            if (databaseError == null) {
                ref.updateChildren(
                    mapOf(
                        Pair("uuid", ref.key)
                    )
                )
                paymentData.value = Resource.success(true)
            } else {
                paymentData.value =
                    Resource.error(Error(serverErrorMessage = databaseError.message))
            }
        }

        return paymentData
    }

    override suspend fun getPaypalConfig(): LiveData<Resource<PaypalConfigBo>> {
        //paypalConfigData.postValue(null)
        paypalConfigRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                paypalConfigData.value = Resource.error(Error(serverErrorMessage = error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                paypalConfigData.value =
                    Resource.success(
                        snapshot.getValue(PaypalConfigBo::class.java) ?: PaypalConfigBo()
                    )
            }

        })
        return paypalConfigData
    }

    override suspend fun createPaypalConfig(paypalConfig: PaypalConfigBo): LiveData<Resource<Boolean>> {
        //createPaypalConfigData.postValue(null)
        paypalConfigRef.setValue(paypalConfig).addOnCompleteListener {
            if (it.isSuccessful) {
                createPaypalConfigData.value = Resource.success(true)
            } else {
                createPaypalConfigData.value = Resource.error(Error())
            }
        }
        return createPaypalConfigData
    }

}