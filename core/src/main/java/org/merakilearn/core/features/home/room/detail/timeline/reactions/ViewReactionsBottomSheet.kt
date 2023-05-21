package org.merakilearn.core.features.home.room.detail.timeline.reactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.load.model.DataUrlLoader
import com.google.android.gms.common.util.DataUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.KoinScopeComponent
import org.merakilearn.core.extentions.KEY_ARG
import org.merakilearn.core.extentions.fragmentArgs
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.cleanup
import org.navgurukul.chat.core.extensions.configureWith
import org.navgurukul.chat.databinding.BottomSheetGenericListBinding
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
    private lateinit var mBinding : BottomSheetGenericListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = BottomSheetGenericListBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemBottomsheetBinding = mBinding.bottomSheetRecyclerView
        mBinding.bottomSheetRecyclerView.configureWith(epoxyController, hasFixedSize = false, showDivider = true)
        itemBottomsheetBinding.bottomSheetTitle.text = context?.getString(R.string.reactions)
        epoxyController.listener = this

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            epoxyController.setData(it)
        })
    }

    override fun onDestroyView() {
        mBinding.bottomSheetRecyclerView.cleanup()
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
