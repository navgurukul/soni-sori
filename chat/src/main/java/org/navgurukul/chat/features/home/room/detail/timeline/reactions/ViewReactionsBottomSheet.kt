package org.navgurukul.chat.features.home.room.detail.timeline.reactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_generic_list_with_title.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.KoinScopeComponent
import org.merakilearn.core.extentions.KEY_ARG
import org.merakilearn.core.extentions.fragmentArgs
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.cleanup
import org.navgurukul.chat.core.extensions.configureWith
import org.navgurukul.chat.features.home.room.detail.timeline.action.EventSharedAction
import org.navgurukul.chat.features.home.room.detail.timeline.action.MessageSharedActionDataSource
import org.navgurukul.chat.features.home.room.detail.timeline.action.TimelineEventFragmentArgs
import org.navgurukul.chat.features.home.room.detail.timeline.item.MessageInformationData

/**
 * Bottom sheet displaying list of reactions for a given event ordered by timestamp
 */
class ViewReactionsBottomSheet : BottomSheetDialogFragment(),
    ViewReactionsEpoxyController.Listener {

    private val args: TimelineEventFragmentArgs by fragmentArgs()

    private val viewModel: ViewReactionsViewModel by viewModel(parameters = { parametersOf(DisplayReactionsViewState(args.eventId, args.roomId)) })

    private val sharedActionViewModel: MessageSharedActionDataSource by lazy {
        (requireActivity() as KoinScopeComponent).scope.get()
    }

    private val epoxyController: ViewReactionsEpoxyController by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_generic_list_with_title, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetRecyclerView.configureWith(epoxyController, hasFixedSize = false, showDivider = true)
        bottomSheetTitle.text = context?.getString(R.string.reactions)
        epoxyController.listener = this

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            epoxyController.setData(it)
        })
    }

    override fun onDestroyView() {
        bottomSheetRecyclerView.cleanup()
        epoxyController.listener = null
        super.onDestroyView()
    }

    override fun didSelectUser(userId: String) {
        sharedActionViewModel.postValue(EventSharedAction.OpenUserProfile(userId))
    }

    companion object {
        fun newInstance(roomId: String, informationData: MessageInformationData): ViewReactionsBottomSheet {
            val args = Bundle()
            val parcelableArgs = TimelineEventFragmentArgs(
                    informationData.eventId,
                    roomId,
                    informationData
            )
            args.putParcelable(KEY_ARG, parcelableArgs)
            return ViewReactionsBottomSheet().apply { arguments = args }
        }
    }
}
