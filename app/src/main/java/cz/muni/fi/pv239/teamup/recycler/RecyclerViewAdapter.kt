package cz.muni.fi.pv239.teamup.recycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import cz.muni.fi.pv239.teamup.R
import cz.muni.fi.pv239.teamup.data.SportEvent

class RecyclerViewAdapter(val events: MutableSet<SportEvent>, val listener: (SportEvent, View) -> Unit, val dist: Boolean = false) : RecyclerView.Adapter<RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.sport_event, parent, false)
        return RecyclerViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) = holder.bind(events.elementAt(position), listener, dist)

    override fun getItemCount() = events.size
}