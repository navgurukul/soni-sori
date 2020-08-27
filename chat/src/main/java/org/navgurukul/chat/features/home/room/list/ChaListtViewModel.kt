package org.navgurukul.chat.features.home.room.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import im.vector.matrix.android.api.session.room.model.RoomSummary
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.repo.AuthenticationRepository
import org.navgurukul.chat.core.utils.DataSource
import org.navgurukul.chat.features.home.HomeRoomListDataSource
import org.navgurukul.commonui.BaseViewModel

class ChaListtViewModel(
    authenticationRepository: AuthenticationRepository,
    activeSessionHolder: ActiveSessionHolder,
    homeRoomListDataSource: HomeRoomListDataSource,
    roomSummaryItemFactory: RoomSummaryItemFactory
) : BaseViewModel(), Listener {

    val rooms: MutableLiveData<List<RoomSummaryItem>> = MutableLiveData()

    init {
        //TODO remove this
        if (!activeSessionHolder.hasActiveSession()) {
            viewModelScope.launch {
                authenticationRepository.login()
            }
        }

        homeRoomListDataSource
            .observe()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .map { rooms -> rooms.map { roomSummaryItemFactory.create(it, this) } }
            .onErrorReturn { emptyList() }
            .subscribe { roomsList ->
                rooms.value = roomsList
            }
            .addTo(disposable)

    }

    override fun onRoomClicked(room: RoomSummary) {
        TODO("Not yet implemented")
    }

}