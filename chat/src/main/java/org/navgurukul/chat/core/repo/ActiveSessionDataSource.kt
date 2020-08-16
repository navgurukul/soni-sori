package org.navgurukul.chat.core.repo

import arrow.core.Option
import im.vector.matrix.android.api.session.Session
import org.navgurukul.chat.core.utils.BehaviorDataSource

class ActiveSessionDataSource : BehaviorDataSource<Option<Session>>()
