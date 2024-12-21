package es.us.managemyteam.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.AcceptPlayerUc
import es.us.managemyteam.usecase.GetPlayersUc
import es.us.managemyteam.usecase.RemoveUserUc
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AcceptPlayersViewModel(
    private val getPlayersUc: GetPlayersUc,
    private val acceptPlayerUc: AcceptPlayerUc,
    private val removeUserUc: RemoveUserUc
) : ViewModel() {

    private val players = CustomMediatorLiveData<Resource<List<UserBo>>>()
    private val acceptPlayerData = CustomMediatorLiveData<Resource<Boolean>>()

    fun getPlayers() {
        viewModelScope.launch(Dispatchers.Main) {
            players.setData(Resource.loading())
            withContext(Dispatchers.IO) {
                players.changeSource(Dispatchers.Main, getPlayersUc(false))
            }
        }
    }

    fun getPlayersData(): LiveData<Resource<List<UserBo>>> {
        return players.liveData()
    }

    fun acceptPlayer(uuid: String) {
        viewModelScope.launch(Dispatchers.Main) {
            acceptPlayerData.setData(Resource.loading(data = null))
            withContext(Dispatchers.IO) {
                acceptPlayerData.changeSource(Dispatchers.Main, acceptPlayerUc(uuid))
            }
        }
    }

    fun getAcceptPlayerData(): LiveData<Resource<Boolean>> {
        //acceptPlayerData.setData(null)
        return acceptPlayerData.liveData()
    }

    fun rejectPlayer(uuid: String) {
        viewModelScope.launch(Dispatchers.Main) {
            acceptPlayerData.setData(Resource.loading(data = null))
            withContext(Dispatchers.IO) {
                acceptPlayerData.changeSource(Dispatchers.Main, removeUserUc(uuid))
            }
        }
    }

}