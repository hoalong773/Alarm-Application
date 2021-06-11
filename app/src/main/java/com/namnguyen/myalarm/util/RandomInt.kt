package com.namnguyen.myalarm.util

import java.util.concurrent.atomic.AtomicInteger

object RandomInt {
    private val seed = AtomicInteger()

    fun getRandomInt(): Int = seed.getAndIncrement() + System.currentTimeMillis().toInt()
}