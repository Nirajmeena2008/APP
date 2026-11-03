package com.yourname.liquidalarm.data

import kotlinx.serialization.Serializable
import kotlinx.datetime.Month

@Serializable
data class MonthDay(val month: Month, val dayOfMonth: Int)
