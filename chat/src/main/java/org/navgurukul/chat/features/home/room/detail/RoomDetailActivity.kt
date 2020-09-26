package org.navgurukul.chat.features.home.room.detail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.widget.Toolbar
import kotlinx.android.parcel.Parcelize
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.navgurukul.chat.ChatBaseActivity
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.replaceFragment
import org.navgurukul.chat.features.share.SharedData
import org.navgurukul.commonui.platform.ToolbarConfigurable

class RoomDetailActivity : ChatBaseActivity(), ToolbarConfigurable {

    private lateinit var currentRoomId: String

    private val viewModel: RoomDetailViewModel by viewModel(parameters = {
        parametersOf(
            currentRoomId
        )
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_detail)

        if (savedInstanceState == null) {
            val roomDetailArgs: RoomDetailArgs =
                intent?.extras?.getParcelable(EXTRA_ROOM_DETAIL_ARGS) ?: return

            currentRoomId = roomDetailArgs.roomId
            replaceFragment(
                R.id.roomDetailContainer,
                RoomDetailFragment::class.java,
                roomDetailArgs
            )
        }

    }

    override fun configure(toolbar: Toolbar) {
        configureToolbar(toolbar)
    }

    companion object {

        const val EXTRA_ROOM_DETAIL_ARGS = "EXTRA_ROOM_DETAIL_ARGS"

        fun newIntent(context: Context, roomDetailArgs: RoomDetailArgs): Intent {
            return Intent(context, RoomDetailActivity::class.java).apply {
                putExtra(EXTRA_ROOM_DETAIL_ARGS, roomDetailArgs)
            }
        }
    }
}