package com.example.rtspviewer

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class RtspUrlDialogFragment : DialogFragment() {

    interface RtspUrlListener {
        fun onUrlEntered(url: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val editText = EditText(requireContext())
        editText.hint = "rtsp://your.stream.url"

        return AlertDialog.Builder(requireContext())
            .setTitle("Enter RTSP URL")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val url = editText.text.toString()
                (activity as? RtspUrlListener)?.onUrlEntered(url)
            }
            .setNegativeButton("Cancel") { _, _ ->
                activity?.finish()
            }
            .create()
    }
}
