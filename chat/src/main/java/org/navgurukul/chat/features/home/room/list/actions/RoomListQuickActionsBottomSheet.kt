package org.navgurukul.chat.features.home.room.list.actions

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.bottom_sheet_generic_list.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.fragmentArgs
import org.merakilearn.core.extentions.toBundle
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.cleanup
import org.navgurukul.chat.core.extensions.configureWith

@Parcelize
data class RoomListActionsArgs(
    val roomId: String,
    val mode: Mode
) : Parcelable {

    enum class Mode {
        FULL,
        NOTIFICATIONS
    }
}

/**
 * Bottom sheet fragment that shows room information with list of contextual actions
 */
class RoomListQuickActionsBottomSheet : BottomSheetDialogFragment(),
    RoomListQuickActionsEpoxyController.Listener {

    private val sharedActionViewModel: RoomListQuickActionsSharedActionViewModel by sharedViewModel()
    private val roomListActionsEpoxyController: RoomListQuickActionsEpoxyController by inject()

    private val roomListActionsArgs: RoomListActionsArgs by fragmentArgs()
    private val viewModel: RoomListQuickActionsViewModel by viewModel(parameters = {
        parametersOf(
            RoomListQuickActionsState(roomListActionsArgs.roomId, roomListActionsArgs.mode)
        )
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_generic_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetRecyclerView.configureWith(
            roomListActionsEpoxyController,
            hasFixedSize = false,
            disableItemAnimation = true
        )
        roomListActionsEpoxyController.listener = this

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            roomListActionsEpoxyController.setData(it)
        })
    }

    override fun onDestroyView() {
        bottomSheetRecyclerView.cleanup()
        super.onDestroyView()
    }


    override fun didSelectMenuAction(quickAction: RoomListQuickActionsSharedAction) {
        sharedActionViewModel.handle(quickAction)
        // Do not dismiss for all the actions
        when (quickAction) {
            is RoomListQuickActionsSharedAction.Favorite -> Unit
            else -> dismiss()
        }
    }

    companion object {
        fun newInstance(
            roomId: String,
            mode: RoomListActionsArgs.Mode
        ): RoomListQuickActionsBottomSheet {
            return RoomListQuickActionsBottomSheet().apply {
                arguments = RoomListActionsArgs(roomId, mode).toBundle()
            }
        }
    }
}
