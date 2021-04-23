package org.navgurukul.chat.core.repo

import arrow.core.Option
import org.matrix.android.sdk.api.session.Session
import org.merakilearn.core.utils.BehaviorDataSource

class ActiveSessionDataSource : BehaviorDataSource<Option<Session>>()
