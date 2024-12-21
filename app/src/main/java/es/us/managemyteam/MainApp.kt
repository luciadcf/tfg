package es.us.managemyteam

import android.app.Application
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import es.us.managemyteam.di.appModule
import es.us.managemyteam.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeKoin()
        initializeFirebaseToken()
    }

    private fun initializeKoin() {
        startKoin {
            androidContext(this@MainApp)
            modules(
                listOf(
                    appModule,
                    dataModule
                )
            )
        }
    }

    private fun initializeFirebaseToken() {
        FirebaseApp.initializeApp(applicationContext)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("Firebase", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result ?: ""
                Log.i("Token ", token)
            })
    }
}
