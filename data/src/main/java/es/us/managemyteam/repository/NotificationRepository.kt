package es.us.managemyteam.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.us.managemyteam.datasource.NotificationApiDataSource
import es.us.managemyteam.repository.util.Resource

interface NotificationRepository {

    suspend fun sendNotifications(
        title: String,
        message: String,
        vararg userIds: Pair<String, String>
    ): LiveData<Resource<Boolean>>

}

class NotificationRepositoryImpl(
    private val notificationApiDataSource: NotificationApiDataSource
) : NotificationRepository {

    private val notificationResponseData = MutableLiveData<Resource<Boolean>>()

    override suspend fun sendNotifications(
        title: String,
        message: String,
        vararg userIds: Pair<String, String>
    ): LiveData<Resource<Boolean>> {
        //notificationResponseData.postValue(null)

        val results = mutableListOf<Boolean>()

        userIds.forEach {
            results.add(
                notificationApiDataSource.sendNotification(
                    it.second,
                    title,
                    message,
                    it.first
                )
            )
        }

        notificationResponseData.postValue(Resource.success(!results.contains(false)))

        return notificationResponseData
    }

}
