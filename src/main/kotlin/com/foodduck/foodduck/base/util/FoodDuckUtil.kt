package com.foodduck.foodduck.base.util

import java.util.*

class FoodDuckUtil {
    companion object {

        private val random: Random = Random()
        private const val AUTHENTICATE_PASSWORD: Int = 9
        private fun randomNumber():Int {
            return 1 + random.nextInt(AUTHENTICATE_PASSWORD)
        }
        fun authenticationNumber(): String {
            val number: StringBuilder = StringBuilder()
            for (i in 0..4) {
                number.append(randomNumber())
            }
            return number.toString()
        }

        const val AUTHENTICATE_DURATION_MINUTE: Long = 3L
    }
}