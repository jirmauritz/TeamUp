package cz.muni.fi.pv239.teamup.data

import java.text.SimpleDateFormat
import java.util.*

data class SportEvent(
        val key: String = "",
        val name: String = "",
        val userUid: String = "",
        val date: String = "",
        val time: String = "",
        val locationId: String = "",
        val locationName: String = "",
        val maxPeople: Int = 0,
        var actualPeople: Int = 0,
        val signedUsers: MutableList<String> = mutableListOf()
) {
    companion object {
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy")
        val timeFormatter = SimpleDateFormat("HH:mm")

        fun getDateWithTime(date: String, time: String) : Calendar {
            val date = dateFormatter.parse(date)
            val time = timeFormatter.parse(time)

            val cal = Calendar.getInstance()
            val calTime = Calendar.getInstance()
            calTime.time = time
            cal.time = date
            cal.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY))
            cal.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE))

            return cal
        }
    }

    var dist: Float? = null
}