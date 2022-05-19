package com.github.jyoo980.reachhover.analytics

import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant

interface TimeUtil {

    fun humanReadableDate(ms: Instant): String {
        val currentTimestamp = Timestamp(ms.toEpochMilli())
        val date = Date(currentTimestamp.time)
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return fmt.format(date)
    }
}
