package cz.muni.fi.pv239.teamup.recycler

import android.support.v7.widget.RecyclerView
import android.view.View
import cz.muni.fi.pv239.teamup.data.SportEvent
import kotlinx.android.synthetic.main.sport_event.view.*

class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(event: SportEvent, listener: (SportEvent, View) -> Unit) = with(itemView) {
        eventNameRow.text = event.name
        eventLocationRow.text = event.locationName
        eventDateRow.text = event.date
        eventTimeRow.text = event.time
        setOnClickListener { listener(event, it) }
    }
}