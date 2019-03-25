package com.polohach.geofence.example.ui.screens.main.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cleveroad.bootstrap.kotlin_core.ui.adapter.BaseRecyclerViewAdapter
import com.google.android.gms.location.Geofence
import com.polohach.geofence.example.DATE_PATTERN
import com.polohach.geofence.example.R
import com.polohach.geofence.example.extensions.getStringApp
import com.polohach.geofence.example.extensions.notNull
import com.polohach.geofence.example.models.Message
import org.joda.time.format.DateTimeFormat


class MessageAdapter(context: Context) :
        BaseRecyclerViewAdapter<Message, MessageAdapter.MessageHolder>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder =
            MessageHolder.newInstance(LayoutInflater.from(parent.context), parent)

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object {
            internal fun newInstance(inflater: LayoutInflater, parent: ViewGroup?) =
                    MessageHolder(inflater.inflate(R.layout.item_message, parent, false))
        }

        private val tvAction = itemView.findViewById<TextView>(R.id.tvAction)
        private val tvUserEmail = itemView.findViewById<TextView>(R.id.tvUserEmail)
        private val tvLocation = itemView.findViewById<TextView>(R.id.tvLocation)
        private val tvTime = itemView.findViewById<TextView>(R.id.tvTime)

        fun bind(message: Message) =
                message.run {
                    tvAction.setText(getTransaction(transition.notNull()))
                    tvUserEmail.text = email
                    tvLocation.text = getStringApp(R.string.transition_lat_lng, latitude, longitude)
                    tvTime.text = DateTimeFormat.forPattern(DATE_PATTERN).print(getDateTime())
                }

        private fun getTransaction(transaction: Int) =
                when (transaction) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> R.string.transition_enter
                    Geofence.GEOFENCE_TRANSITION_EXIT -> R.string.transition_exit
                    else -> R.string.transition_unknown
                }
    }
}