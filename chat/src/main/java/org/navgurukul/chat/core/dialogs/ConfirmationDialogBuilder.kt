package org.navgurukul.chat.core.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import org.navgurukul.chat.R
import org.navgurukul.chat.databinding.DialogConfirmationWithReasonBinding

object ConfirmationDialogBuilder {
    @SuppressLint("StaticFieldLeak")
    private lateinit var binding: DialogConfirmationWithReasonBinding

    fun show(activity: Activity,
             askForReason: Boolean,
             @StringRes titleRes: Int,
             @StringRes confirmationRes: Int,
             @StringRes positiveRes: Int,
             @StringRes reasonHintRes: Int,
             confirmation: (String?) -> Unit) {
        val layout = activity.layoutInflater.inflate(R.layout.dialog_confirmation_with_reason, null)
        layout.dialogConfirmationText.setText(confirmationRes)

        layout.dialogReasonCheck.isVisible = askForReason
        layout.dialogReasonTextInputLayout.isVisible = askForReason

        layout.dialogReasonCheck.setOnCheckedChangeListener { _, isChecked ->
            layout.dialogReasonTextInputLayout.isEnabled = isChecked
        }
        if (askForReason && reasonHintRes != 0) {
            layout.dialogReasonInput.setHint(reasonHintRes)
        }

        AlertDialog.Builder(activity)
                .setTitle(titleRes)
                .setView(layout)
                .setPositiveButton(positiveRes) { _, _ ->
                    val reason = layout.dialogReasonInput.text.toString()
                            .takeIf { askForReason }
                            ?.takeIf { layout.dialogReasonCheck.isChecked }
                            ?.takeIf { it.isNotBlank() }
                    confirmation(reason)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }
}