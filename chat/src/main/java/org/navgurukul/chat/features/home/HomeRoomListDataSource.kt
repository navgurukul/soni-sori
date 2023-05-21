package org.navgurukul.chat.features.home

import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.navgurukul.chat.core.utils.BehaviorDataSource

class HomeRoomListDataSource : BehaviorDataSource<List<RoomSummary>>()
