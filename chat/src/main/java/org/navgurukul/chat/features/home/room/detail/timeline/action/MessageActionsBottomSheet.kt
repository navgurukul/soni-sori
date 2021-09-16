package org.navgurukul.chat.features.home.room.detail.timeline.action

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.bottom_sheet_generic_list.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.KoinScopeComponent
import org.merakilearn.core.extentions.KEY_ARG
import org.merakilearn.core.extentions.fragmentArgs
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.cleanup
import org.navgurukul.chat.core.extensions.configureWith
import org.navgurukul.chat.core.utils.DimensionConverter
import org.navgurukul.chat.features.home.room.detail.timeline.item.MessageInformationData

@Parcelize
data class TimelineEventFragmentArgs(
    val eventId: String,
    val roomId: String,
    val informationData: MessageInformationData
) : Parcelable

/**
 * Bottom sheet fragment that shows a message preview with list of contextual actions
 */
class MessageActionsBottomSheet : BottomSheetDialogFragment(),
    MessageActionsController.MessageActionsControllerListener {

    private val timelineEventFragmentArgs: TimelineEventFragmentArgs by fragmentArgs()

    private val messageActionsController: MessageActionsController by inject()

    private val viewModel: MessageActionsViewModel by viewModel(parameters = { parametersOf(MessageActionState(
        timelineEventFragmentArgs.roomId,
        timelineEventFragmentArgs.eventId,
        timelineEventFragmentArgs.informationData
    ))})

    private val sharedActionDataSource: MessageSharedActionDataSource by lazy {
        (requireActivity() as KoinScopeComponent).scope.get()
    }

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
            messageActionsController,
            hasFixedSize = false,
            disableItemAnimation = true
        )
        messageActionsController.listener = this

        viewModel.viewState.observe(this, Observer {
            messageActionsController.setData(it)
        })

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            val dialog = this as? BottomSheetDialog
            dialog?.behavior?.setPeekHeight(DimensionConverter(resources).dpToPx(400), false)
            dialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onDestroyView() {
        bottomSheetRecyclerView.cleanup()
        super.onDestroyView()
    }

    override fun onUrlClicked(url: String, title: String): Boolean {
        sharedActionDataSource.postValue(EventSharedAction.OnUrlClicked(url, title))
        // Always consume
        return true
    }

    override fun onUrlLongClicked(url: String): Boolean {
        sharedActionDataSource.postValue(EventSharedAction.OnUrlLongClicked(url))
        // Always consume
        return true
    }

    override fun didSelectMenuAction(eventAction: EventSharedAction) {
        if (eventAction is EventSharedAction.ReportContent) {
            // Toggle report menu
            // Enable item animation
            if (bottomSheetRecyclerView.itemAnimator == null) {
                bottomSheetRecyclerView.itemAnimator = DefaultItemAnimator().apply {
                    addDuration = 300L
                    removeDuration = 0
                    moveDuration = 0
                    changeDuration = 0
                }
            }
            viewModel.handle(MessageActionsAction.ToggleReportMenu)
        } else {
            sharedActionDataSource.postValue(eventAction)
            dismiss()
        }
    }

    companion object {
        fun newInstance(
            roomId: String,
            informationData: MessageInformationData
        ): MessageActionsBottomSheet {
            return MessageActionsBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_ARG, TimelineEventFragmentArgs(
                        informationData.eventId,
                        roomId,
                        informationData
                    ))

                }
            }
        }
    }
}