package org.navgurukul.chat.core.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import org.navgurukul.chat.R
import org.navgurukul.chat.databinding.DialogConfirmationWithReasonBinding

object ConfirmationDialogBuilder {
    @SuppressLint("StaticFieldLeak")
    private lateinit var binding: DialogConfirmationWithReasonBinding

    fun show(
        activity: Activity,
        askForReason: Boolean,
        @StringRes titleRes: Int,
        @StringRes confirmationRes: Int,
        @StringRes positiveRes: Int,
        @StringRes reasonHintRes: Int,
        confirmation: (String?) -> Unit
    ) {
        binding = DialogConfirmationWithReasonBinding.inflate(activity.layoutInflater, null, false)
        binding.apply {
            dialogConfirmationText.setText(confirmationRes)

            dialogReasonCheck.isVisible = askForReason
            dialogReasonTextInputLayout.isVisible = askForReason

            dialogReasonCheck.setOnCheckedChangeListener { _, isChecked ->
                dialogReasonTextInputLayout.isEnabled = isChecked
            }
            if (askForReason && reasonHintRes != 0) {
                dialogReasonInput.setHint(reasonHintRes)
            }

            AlertDialog.Builder(activity)
                .setTitle(titleRes)
                .setView(this.root)
                .setPositiveButton(positiveRes) { _, _ ->
                    val reason = dialogReasonInput.text.toString()
                        .takeIf { askForReason }
                        ?.takeIf { dialogReasonCheck.isChecked }
                        ?.takeIf { it.isNotBlank() }
                    confirmation(reason)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }
}