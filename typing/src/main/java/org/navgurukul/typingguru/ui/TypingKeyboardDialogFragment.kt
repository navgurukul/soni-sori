package org.navgurukul.typingguru.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import org.merakilearn.core.extentions.setWidthPercent
import org.merakilearn.core.navigator.TypingAppModuleNavigator
import org.navgurukul.commonui.platform.BaseDialogFragment
import org.navgurukul.typingguru.R
import org.navgurukul.typingguru.utils.TypingGuruPreferenceManager
import org.navgurukul.typingguru.utils.Utility

class TypingKeyboardDialogFragment : BaseDialogFragment() {

    companion object {
        const val CONTENT_KEY = "content_key"
        const val TYPE_KEY = "type_key"
        fun newInstance(content: ArrayList<String>, type: String): TypingKeyboardDialogFragment {
            return TypingKeyboardDialogFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(CONTENT_KEY, content)
                    putString(TYPE_KEY, type)
                }
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
        val btnOwn: AppCompatButton = view.findViewById(R.id.btn_own) as AppCompatButton
        val btnPurchase: AppCompatButton = view.findViewById(R.id.btn_purchase) as AppCompatButton
        val viewOtg: TextView = view.findViewById(R.id.txt_lbl_info3) as TextView
        if (Utility.isOtgSupported(requireContext())) {
            viewOtg.text = getString(R.string.otg_support)
        } else {
            viewOtg.text = getString(R.string.no_otg_support)
        }

        btnOwn.setOnClickListener {
            if (!isHidden) {
                dismiss()

                val content = requireArguments().getStringArrayList(CONTENT_KEY) as ArrayList<String>
                val type = requireArguments().getString(TYPE_KEY)!!
                TypingGuruPreferenceManager.instance().setWebViewDisplayStatus(true)
                startActivity(KeyboardActivity.newIntent(requireContext(), TypingAppModuleNavigator.Mode.Course(content, type)))

                requireActivity().finish()
            }
        }
        btnPurchase.setOnClickListener {
            if (!isHidden) {
                dismiss()
                TypingGuruPreferenceManager.instance().setWebViewDisplayStatus(true)
                startActivity(Intent(requireContext(), WebViewActivity::class.java))

                requireActivity().finish()
            }
        }
    }

    override fun shouldInstallDynamicModule(): Boolean = true
}