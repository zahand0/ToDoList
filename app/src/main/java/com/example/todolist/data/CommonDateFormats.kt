package com.example.todolist.data

import java.text.SimpleDateFormat
import java.util.*

object CommonDateFormats {

    const val DIGIT_DATE = "d.MM.yyyy"
    const val SHORT_DATE = "d MMMM yyyy"

    fun msecToDate(dateInMsec: Long, pattern: String = CommonDateFormats.DIGIT_DATE): String {
        val date = Date(dateInMsec)
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }
}