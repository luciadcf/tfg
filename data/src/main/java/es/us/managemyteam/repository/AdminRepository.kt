package es.us.managemyteam.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import es.us.managemyteam.data.database.DatabaseTables
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.RepositoryUtil
import es.us.managemyteam.repository.util.Resource

interface AdminRepository {

    suspend fun getPlayers(enabled: Boolean): LiveData<Resource<List<UserBo>>>

    suspend fun acceptPlayer(uuid: String): LiveData<Resource<Boolean>>

    suspend fun rejectPlayer(uuid: String): LiveData<Resource<Boolean>>

    suspend fun getUsers(): LiveData<Resource<List<UserBo>>>

    suspend fun updateUsersEnable(users: List<UserBo>): LiveData<Resource<Boolean>>
}

class AdminRepositoryImpl : AdminRepository {

    private val userTable = RepositoryUtil.getDatabaseTable(DatabaseTables.USER_TABLE)
    private val playersData = MutableLiveData<Resource<List<UserBo>>>()
    private val playerNotEnabledData = MutableLiveData<Resource<Boolean>>()
    private val usersData = MutableLiveData<Resource<List<UserBo>>>()
    private val usersEnabledData = MutableLiveData<Resource<Boolean>>()

    override suspend fun getPlayers(enabled: Boolean): LiveData<Resource<List<UserBo>>> {
        //playersData.postValue(null)
        userTable.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                playersData.value = Resource.error(Error(serverErrorMessage = error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val playersNotEnabled =
                    snapshot.children.mapNotNull { it.getValue(UserBo::class.java) }
                        .filter { it.isPlayer() && it.enable == enabled }.sortedBy {
                            it.getFullName()
                        }
                playersData.value =
                    Resource.success(playersNotEnabled)
            }

        })

        return playersData
    }

    override suspend fun acceptPlayer(uuid: String): LiveData<Resource<Boolean>> {
        //playerNotEnabledData.postValue(null)
        userTable.child(uuid).child("enable")
            .setValue(true) { error, _ ->
                playerNotEnabledData.value = if (error != null) {
                    Resource.error(Error(serverErrorMessage = error.message))
                } else {
                    Resource.success(true)
                }
            }

        return playerNotEnabledData
    }

    override suspend fun rejectPlayer(uuid: String): LiveData<Resource<Boolean>> {
        //playerNotEnabledData.postValue(null)
        userTable.child(uuid).child("enable")
            .setValue(false) { error, _ ->
                playerNotEnabledData.value = if (error != null) {
                    Resource.error(Error(serverErrorMessage = error.message))
                } else {
                    Resource.success(true)
                }
            }

        return playerNotEnabledData
    }

    override suspend fun getUsers(): LiveData<Resource<List<UserBo>>> {
        //usersData.postValue(null)
        userTable.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                usersData.value = Resource.error(Error(serverErrorMessage = error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val users =
                    snapshot.children.mapNotNull { it.getValue(UserBo::class.java) }
                        .sortedWith(compareBy({ it.role }, { it.getFullName() }))
                usersData.value =
                    Resource.success(users)
            }

        })

        return usersData
    }

    override suspend fun updateUsersEnable(users: List<UserBo>): LiveData<Resource<Boolean>> {
        //usersEnabledData.postValue(null)
        users.forEach {
            it.uuid?.let { uuid ->
                userTable.child(uuid).updateChildren(
                    mapOf(
                        Pair("enable", it.enable)
                    )
                )
            }
        }
        usersEnabledData.postValue(Resource.success(true))
        return usersEnabledData
    }

}