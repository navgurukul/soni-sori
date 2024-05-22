package org.navgurukul.typingguru.keyboard.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import kotlinx.android.parcel.Parcelize
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.fragmentArgs
import org.merakilearn.core.extentions.setWidthPercent
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.Mode
import org.navgurukul.commonui.platform.BaseDialogFragment
import org.navgurukul.typingguru.R
import org.navgurukul.typingguru.databinding.LayoutKeyboardDialogBinding
import org.navgurukul.typingguru.keyboard.KeyboardActivity
import org.navgurukul.typingguru.webview.WebViewActivity

@Parcelize
data class KeyboardDialogArgs(
    val mode: Mode,
) : Parcelable

class KeyboardDialogFragment : BaseDialogFragment() {

    private val keyboardDialogArgs: KeyboardDialogArgs by fragmentArgs()
    private val viewModel: KeyboardDialogViewModel by viewModel(parameters = { parametersOf(keyboardDialogArgs) })

    private lateinit var binding: LayoutKeyboardDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= LayoutKeyboardDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(mode: Mode): KeyboardDialogFragment {
            return KeyboardDialogFragment().apply {
                arguments = KeyboardDialogArgs(mode).toBundle()
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
            binding.tvInfo.text = it.infoText
        })

        viewModel.viewEvents.observe(viewLifecycleOwner, {
            when(it) {
                KeyboardDialogViewEvents.Dismiss -> {
                    if (!isHidden) {
                        dismiss()
                    }
                    requireActivity().finish()
                }
                is KeyboardDialogViewEvents.OpenKeyboardActivity -> {
                    startActivity(KeyboardActivity.newIntent(requireContext(), it.mode))
                }
                KeyboardDialogViewEvents.OpenWebViewActivity -> {
                    startActivity(WebViewActivity.newIntent(requireContext()))
                }
            }
        })

        binding.btnOwn.setOnClickListener {
            viewModel.handle(KeyboardDialogViewActions.OwnButtonClicked)
        }
        binding.btnPurchase.setOnClickListener {
            viewModel.handle(KeyboardDialogViewActions.BuyButtonClicked)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        requireActivity().finish()
    }

    override fun shouldInstallDynamicModule(): Boolean = true
}