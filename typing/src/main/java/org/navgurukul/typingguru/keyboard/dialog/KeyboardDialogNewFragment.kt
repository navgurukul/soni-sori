package org.navgurukul.typingguru.keyboard.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.Window
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.layout_keyboard_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.fragmentArgs
import org.merakilearn.core.extentions.setWidthPercent
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.Mode
import org.merakilearn.core.navigator.ModeNew
import org.navgurukul.commonui.platform.BaseDialogFragment
import org.navgurukul.typingguru.R
import org.navgurukul.typingguru.keyboard.KeyboardActivity
import org.navgurukul.typingguru.keyboard.KeyboardWordActivity
import org.navgurukul.typingguru.webview.WebViewActivity

@Parcelize
data class KeyboardDialogNewArgs(
    val mode: ModeNew,
) : Parcelable

class KeyboardDialogNewFragment : BaseDialogFragment() {

    private val keyboardDialogArgs: KeyboardDialogNewArgs by fragmentArgs()
    private val viewModel : KeyboardDialogNewViewModel by viewModel(parameters = { parametersOf(keyboardDialogArgs) })

    companion object {
        fun newInstance(mode: ModeNew): KeyboardDialogNewFragment {
            return KeyboardDialogNewFragment().apply {
                arguments = KeyboardDialogNewArgs(mode).toBundle()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setWidthPercent(90)
    }

    override fun getLayoutResId() = R.layout.layout_keyboard_dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false)
        return dialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(viewLifecycleOwner, {
            tv_info.text = it.infoText
        })

        viewModel.viewEvents.observe(viewLifecycleOwner, {
            when(it) {
                KeyboardDialogViewEventsNew.Dismiss -> {
                    if (!isHidden) {
                        dismiss()
                    }
                    requireActivity().finish()
                }
                is KeyboardDialogViewEventsNew.OpenKeyboardActivity -> {
                    startActivity(KeyboardWordActivity.newIntent(requireContext(), it.mode))
                }
                KeyboardDialogViewEventsNew.OpenWebViewActivity -> {
                    startActivity(WebViewActivity.newIntent(requireContext()))
                }
            }
        })

        view.findViewById<View>(R.id.btn_own).setOnClickListener {
            viewModel.handle(KeyboardDialogViewActions.OwnButtonClicked)
        }
        view.findViewById<View>(R.id.btn_purchase).setOnClickListener {
            viewModel.handle(KeyboardDialogViewActions.BuyButtonClicked)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        requireActivity().finish()
    }

    override fun shouldInstallDynamicModule(): Boolean = true
}