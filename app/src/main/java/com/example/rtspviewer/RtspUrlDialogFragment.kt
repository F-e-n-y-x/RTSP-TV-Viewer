package com.example.rtspviewer

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager

/**
 * Dialog for entering / editing the RTSP URL. Validates the input before
 * accepting it, and pre-fills the current values so it doubles as an editor.
 */
class RtspUrlDialogFragment : DialogFragment() {

    interface RtspUrlListener {
        fun onUrlEntered(url: String, forceTcp: Boolean)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_rtsp_url, null)
        val input = view.findViewById<EditText>(R.id.urlInput)
        val tcp = view.findViewById<CheckBox>(R.id.forceTcp)

        input.setText(arguments?.getString(ARG_URL).orEmpty())
        input.setSelection(input.text.length)
        tcp.isChecked = arguments?.getBoolean(ARG_TCP, true) ?: true

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.enter_rtsp_url)
            .setView(view)
            // Pass null so the dialog does NOT auto-dismiss; we validate first.
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.cancel) { _, _ -> handleCancel() }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val url = input.text.toString().trim()
                if (!isValidRtsp(url)) {
                    input.error = getString(R.string.invalid_url)
                } else {
                    (activity as? RtspUrlListener)?.onUrlEntered(url, tcp.isChecked)
                    dialog.dismiss()
                }
            }
        }
        return dialog
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        handleCancel()
    }

    /** On first run (no saved URL) cancelling leaves nothing to show, so exit. */
    private fun handleCancel() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (prefs.getString(PlayerActivity.KEY_URL, null).isNullOrBlank()) {
            activity?.finish()
        }
    }

    private fun isValidRtsp(url: String): Boolean =
        url.startsWith("rtsp://", ignoreCase = true) && url.length > "rtsp://".length

    companion object {
        private const val ARG_URL = "arg_url"
        private const val ARG_TCP = "arg_tcp"

        fun newInstance(url: String?, forceTcp: Boolean): RtspUrlDialogFragment =
            RtspUrlDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URL, url)
                    putBoolean(ARG_TCP, forceTcp)
                }
            }
    }
}
