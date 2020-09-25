package org.navgurukul.chat.features.powerlevel

import im.vector.matrix.android.api.query.QueryStringValue
import im.vector.matrix.android.api.session.events.model.EventType
import im.vector.matrix.android.api.session.events.model.toModel
import im.vector.matrix.android.api.session.room.Room
import im.vector.matrix.android.api.session.room.model.PowerLevelsContent
import im.vector.matrix.rx.mapOptional
import im.vector.matrix.rx.rx
import im.vector.matrix.rx.unwrap
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
