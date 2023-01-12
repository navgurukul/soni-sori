package org.navgurukul.webide.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.webIDE.databinding.ItemLogBinding

class LogsAdapter(private val localWithoutIndex: String, private val jsLogs: List<ConsoleMessage>, private val darkTheme: Boolean) : RecyclerView.Adapter<LogsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView = ItemLogBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(jsLogs[position])

    override fun getItemCount(): Int = jsLogs.size

    inner class ViewHolder(private var v: ItemLogBinding) : RecyclerView.ViewHolder(v.root) {

        fun bind(consoleMessage: ConsoleMessage) {
            with (v) {
                val newId = consoleMessage.sourceId().replace(localWithoutIndex, "") + ":" + consoleMessage.lineNumber()
                val msg = consoleMessage.message()
                val msgLevel = consoleMessage.messageLevel().name.substring(0, 1)

                logLevel.text = msgLevel
                logLevel.setTextColor(getLogColor(consoleMessage.messageLevel()))
                logMessage.text = msg
                logMessage.setTextColor(if (darkTheme) { Color.WHITE } else { Color.BLACK })
                logDetails.text = newId
            }
        }

        @ColorInt
        private fun getLogColor(messageLevel: ConsoleMessage.MessageLevel): Int = when (messageLevel) {
            ConsoleMessage.MessageLevel.LOG -> if (darkTheme) {
                Color.WHITE
            } else {
                -0x79000000
            }

            ConsoleMessage.MessageLevel.TIP -> -0x83b201
            ConsoleMessage.MessageLevel.DEBUG -> -0xff198a
            ConsoleMessage.MessageLevel.ERROR -> -0xadae
            ConsoleMessage.MessageLevel.WARNING -> -0x3c00
        }
    }
}
