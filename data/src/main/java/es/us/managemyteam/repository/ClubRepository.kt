package es.us.managemyteam.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import es.us.managemyteam.data.database.DatabaseTables
import es.us.managemyteam.data.model.ClubBo
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.RepositoryUtil
import es.us.managemyteam.repository.util.Resource
import java.util.*

interface ClubRepository {

    suspend fun getClub(): LiveData<Resource<ClubBo>>

    suspend fun editClub(
        uuid: String,
        name: String,
        dateFoundation: Date?,
        location: String,
        president: String,
        coach: String,
        phoneNumber: String,
        mail: String,
        web: String
    ): LiveData<Resource<Boolean>>

}

class ClubRepositoryImpl : ClubRepository {

    private val clubRef = RepositoryUtil.getDatabaseTable(DatabaseTables.CLUB_TABLE)
    private val club = MutableLiveData<Resource<ClubBo>>()
    private val clubCreateData = MutableLiveData<Resource<Boolean>>()

    init {
        initializeClubListener()
    }

    override suspend fun getClub(): LiveData<Resource<ClubBo>> {
        return club
    }

    override suspend fun editClub(
        uuid: String,
        name: String,
        dateFoundation: Date?,
        location: String,
        president: String,
        coach: String,
        phoneNumber: String,
        mail: String,
        web: String
    ): LiveData<Resource<Boolean>> {
        val newClub =
            ClubBo(
                name, dateFoundation, location, president, coach, phoneNumber, mail, web
            ).apply {
                this.uuid = uuid
            }
        clubRef.setValue(newClub)
        { error, _ ->
            clubCreateData.value = if (error != null) {
                Resource.error(Error(serverErrorMessage = error.message))
            } else {
                Resource.success(true)
            }
        }
        return clubCreateData
    }

    private fun initializeClubListener() {
        clubRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                club.value = Resource.error(Error(serverErrorMessage = databaseError.message))
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                club.value =
                    Resource.success(dataSnapshot.getValue(ClubBo::class.java))
            }

        })
    }

}