package com.example.clientchodientu.untils.time

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

object TimeUtil {
    public fun formatTime(timeObj: Any?): String {
        if (timeObj == null) return ""

        return try {
            when (timeObj) {
                // Trường hợp dữ liệu từ Firestore
                is Timestamp -> {
                    val date = timeObj.toDate()
                    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                    sdf.format(date)
                }
                // Trường hợp dữ liệu từ API (String)
                is String -> {
                    if (timeObj.length >= 16) timeObj.substring(11, 16) else timeObj
                }

                else -> timeObj.toString()
            }
        } catch (e: Exception) {
            ""
        }
    }
}