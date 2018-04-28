package cz.muni.fi.pv239.teamup.data

import android.net.Uri

data class User(
        val uid: String,
        val displayName: String,
        val email: String,
        val photoUrl: String
)