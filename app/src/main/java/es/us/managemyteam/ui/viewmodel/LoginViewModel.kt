package es.us.managemyteam.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.managemyteam.app.R
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.extension.isEmail
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.GetUserUc
import es.us.managemyteam.usecase.LoginUc
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val loginUc: LoginUc, private val getUserUc: GetUserUc) : ViewModel() {

    private val login: CustomMediatorLiveData<Resource<String>> = CustomMediatorLiveData()
    private val user: CustomMediatorLiveData<Resource<UserBo>> = CustomMediatorLiveData()

    fun getLoginData(): LiveData<Resource<String>> {
        return login.apply {
            //setData(null)
        }.liveData()
    }

    fun login(
        email: String,
        password: String
    ) =
        viewModelScope.launch(Dispatchers.Main) {
            if (validateForm(email, password)) {
                login.setData(Resource.loading(data = null))
                withContext(Dispatchers.IO) {
                    login.changeSource(Dispatchers.Main, loginUc(email, password))
                }
            }
        }

    fun getUserData(): LiveData<Resource<UserBo>> {
        //user.setData(null)
        return user.liveData()
    }

    fun getUser(uid: String) =
        viewModelScope.launch(Dispatchers.Main) {
            //user.setData(null)
            user.setData(Resource.loading(null))
            withContext(Dispatchers.IO) {
                user.changeSource(Dispatchers.Main, getUserUc(uid))
            }
        }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            email.isBlank() || password.isBlank() -> {
                login.setData(
                    Resource.error(
                        Error(
                            R.string.registration_error_empty_fields
                        )
                    )
                )
                false
            }
            !email.isEmail() -> {
                login.setData(
                    Resource.error(
                        Error(
                            R.string.registration_error_invalid_email
                        )
                    )
                )
                false
            }
            else -> true
        }
    }
}