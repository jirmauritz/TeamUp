package cz.muni.fi.pv239.teamup.data

import java.text.SimpleDateFormat

data class SportEvent(
        val name: String = "",
        val userUid: String = "",
        val date: String = "",
        val time: String = "",
        val locationId: String = "",
        val locationName: String = "",
        val maxPeople: Int = 0,
        val actualPeople: Int = 0
) {
    companion object {
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy")
        val timeFormatter = SimpleDateFormat("HH:mm")
    }
}