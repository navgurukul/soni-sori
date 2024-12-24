package org.navgurukul.chat.core.dialogs

import android.app.Activity
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import org.navgurukul.chat.R
import org.navgurukul.chat.databinding.DialogConfirmationWithReasonBinding

object ConfirmationDialogBuilder {

    fun show(
        activity: Activity,
        askForReason: Boolean,
        @StringRes titleRes: Int,
        @StringRes confirmationRes: Int,
        @StringRes positiveRes: Int,
        @StringRes reasonHintRes: Int,
        confirmation: (String?) -> Unit
    ) {
        val inflater = activity.layoutInflater
        val binding = DialogConfirmationWithReasonBinding.inflate(inflater)

        binding.dialogConfirmationText.setText(confirmationRes)

        binding.dialogReasonCheck.isVisible = askForReason
        binding.dialogReasonTextInputLayout.isVisible = askForReason

        binding.dialogReasonCheck.setOnCheckedChangeListener { _, isChecked ->
            binding.dialogReasonTextInputLayout.isEnabled = isChecked
        }
        if (askForReason && reasonHintRes != 0) {
            binding.dialogReasonInput.setHint(reasonHintRes)
        }

        AlertDialog.Builder(activity)
            .setTitle(titleRes)
            .setView(binding.root)
            .setPositiveButton(positiveRes) { _, _ ->
                val reason = binding.dialogReasonInput.text.toString()
                    .takeIf { askForReason }
                    ?.takeIf { binding.dialogReasonCheck.isChecked }
                    ?.takeIf { it.isNotBlank() }
                confirmation(reason)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}