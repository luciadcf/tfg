package es.us.managemyteam.usecase

import androidx.lifecycle.LiveData
import es.us.managemyteam.repository.ClubRepository
import es.us.managemyteam.repository.util.Resource
import java.util.*

class EditClubUc(private val clubRepository: ClubRepository) {

    suspend operator fun invoke(
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

        return clubRepository.editClub(
            uuid,
            name,
            dateFoundation,
            location,
            president,
            coach,
            phoneNumber,
            mail,
            web
        )
    }
}