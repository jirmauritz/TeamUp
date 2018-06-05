package cz.muni.fi.pv239.teamup.recycler

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import cz.muni.fi.pv239.teamup.R
import cz.muni.fi.pv239.teamup.data.SportEvent
import kotlinx.android.synthetic.main.sport_event.view.*
import kotlin.math.roundToInt

class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(event: SportEvent, listener: (SportEvent, View) -> Unit, user: String, dist: Boolean) = with(itemView) {
        eventNameRow.text = event.name
        eventLocationRow.text = event.locationName
        eventDateRow.text = event.date
        eventTimeRow.text = event.time
        val distance = event.dist
        if (dist && distance != null) {
            eventKmRow.text = when (distance) {
                in 0..1                 -> "%.1f".format(distance)
                in 10000..Int.MAX_VALUE -> ">10k"
                else                    -> distance.roundToInt().toString()
            }
            eventKmRow.visibility = View.VISIBLE
        }
        if (user == event.userUid) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight))
        } else if (event.signedUsers.contains(user)) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryLight))
        } else {
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryExtraLight))
        }

        setOnClickListener { listener(event, it) }
    }
}