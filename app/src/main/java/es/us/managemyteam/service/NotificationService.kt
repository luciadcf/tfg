package es.us.managemyteam.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import es.managemyteam.app.R
import es.us.managemyteam.constant.Preference
import es.us.managemyteam.contract.PreferencesInterface
import es.us.managemyteam.ui.activity.MainActivity
import org.koin.android.ext.android.inject

private const val CHANNEL_ID = "manage_my_team_notifications_channel"

class NotificationService : FirebaseMessagingService() {

    private val preferencesManager: PreferencesInterface by inject()

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.data.isNotEmpty()) {
            createNotificationChannel()

            val myID = FirebaseAuth.getInstance().currentUser?.uid
            if (myID == message.data.getOrDefault("uid", "")) {
                NotificationManagerCompat.from(applicationContext).notify(
                    1,
                    buildNotification(
                        message.data.getOrDefault("title", getString(R.string.app_name)),
                        message.data.getOrDefault("message", getString(R.string.app_name))
                    )
                )
            }
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        preferencesManager.save(Preference.KEY_DEVICE_INSTANCE_ID, token)
    }

    private fun buildNotification(title: String, description: String) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(description)
            .setContentIntent(
                PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }, 0)
            )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH).build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}