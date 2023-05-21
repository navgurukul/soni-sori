package org.navgurukul.chat.core.repo

import arrow.core.Option
import org.matrix.android.sdk.api.session.Session
import org.navgurukul.chat.core.utils.BehaviorDataSource

class ActiveSessionDataSource : BehaviorDataSource<Option<Session>>()
