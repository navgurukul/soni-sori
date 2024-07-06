package org.merakilearn.core.additional

import arrow.core.Option
import org.matrix.android.sdk.api.session.Session


class ActiveSessionDataSource : BehaviorDataSource<Option<Session>>()
