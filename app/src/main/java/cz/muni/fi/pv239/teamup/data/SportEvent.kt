package cz.muni.fi.pv239.teamup.data

import java.util.*

data class SportEvent(
        val name: String,
        val userUid: String,
        val dateTime: Calendar,
        val locationId: String,
        val locationName: String,
        val maxPeople: Int,
        val actualPeople: Int
)