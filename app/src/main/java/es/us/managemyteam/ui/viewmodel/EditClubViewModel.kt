package es.us.managemyteam.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import es.managemyteam.app.R
import es.us.managemyteam.data.model.ClubBo
import es.us.managemyteam.extension.isEmail
import es.us.managemyteam.extension.isPhone
import es.us.managemyteam.extension.isURL
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.EditClubUc
import es.us.managemyteam.usecase.GetClubUc
import es.us.managemyteam.usecase.GetUserUc
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class EditClubViewModel(
    private val getClubUc: GetClubUc,
    getUserUc: GetUserUc,
    private val editClubUc: EditClubUc
) : BaseLoggedViewModel(getUserUc) {

    private val club: CustomMediatorLiveData<Resource<ClubBo>> = CustomMediatorLiveData()
    private val editClub = CustomMediatorLiveData<Resource<Boolean>>()

    fun getClub() {
        viewModelScope.launch(Dispatchers.Main) {
            club.setData(Resource.loading(data = null))
            withContext(Dispatchers.IO) {
                club.changeSource(
                    Dispatchers.Main,
                    getClubUc()
                )
            }
        }
    }

    fun getClubData(): LiveData<Resource<ClubBo>> {
        return club.liveData()
    }

    fun editClub(
        uuid: String,
        name: String,
        dateFoundation: Date?,
        location: String,
        president: String,
        coach: String,
        phoneNumber: String,
        mail: String,
        web: String
    ) =
        viewModelScope.launch(Dispatchers.Main) {
            if (validateForm(
                    name,
                    dateFoundation.toString(),
                    location,
                    president,
                    coach,
                    phoneNumber,
                    mail,
                    web
                )
            ) {
                withContext(Dispatchers.IO) {
                    editClub.changeSource(
                        Dispatchers.Main,
                        editClubUc(
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
                    )
                }
            }
        }

    fun editClubData(): LiveData<Resource<Boolean>> {
        //editClub.setData(null)
        return editClub.liveData()
    }

    private fun validateForm(
        name: String,
        dateFoundation: String,
        location: String,
        president: String,
        coach: String,
        phoneNumber: String,
        mail: String,
        web: String
    ): Boolean {
        return when {
            name.isBlank() -> {
                editClub.setData(
                    Resource.error(
                        Error(R.string.edit_club_error_name)
                    )
                )
                false
            }
            dateFoundation.isBlank() -> {
                editClub.setData(
                    Resource.error(
                        Error(R.string.edit_club_error_date_foundation)
                    )
                )
                false
            }
            location.isBlank() -> {
                editClub.setData(
                    Resource.error(
                        Error(R.string.edit_club_error_location)
                    )
                )
                false
            }
            president.isBlank() -> {
                editClub.setData(
                    Resource.error(
                        Error(R.string.edit_club_error_president)
                    )
                )
                false
            }
            coach.isBlank() -> {
                editClub.setData(
                    Resource.error(
                        Error(R.string.edit_club_error_coach)
                    )
                )
                false
            }
            !phoneNumber.isPhone() -> {
                editClub.setData(
                    Resource.error(
                        Error(R.string.edit_club_error_phone_number)
                    )
                )
                false
            }
            !mail.isEmail() -> {
                editClub.setData(
                    Resource.error(
                        Error(R.string.edit_club_error_mail)
                    )
                )
                false
            }
            !web.isURL() -> {
                editClub.setData(
                    Resource.error(
                        Error(R.string.edit_club_error_web)
                    )
                )
                false
            }
            else -> true
        }
    }

}