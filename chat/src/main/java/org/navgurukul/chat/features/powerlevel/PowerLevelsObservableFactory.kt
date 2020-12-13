package org.navgurukul.chat.features.powerlevel

import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.rx.mapOptional
import org.matrix.android.sdk.rx.rx
import org.matrix.android.sdk.rx.unwrap
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class PowerLevelsObservableFactory(private val room: Room) {

    fun createObservable(): Observable<PowerLevelsContent> {
        return room.rx()
                .liveStateEvent(EventType.STATE_ROOM_POWER_LEVELS, QueryStringValue.NoCondition)
                .observeOn(Schedulers.computation())
                .mapOptional { it.content.toModel<PowerLevelsContent>() }
                .unwrap()
    }
}
