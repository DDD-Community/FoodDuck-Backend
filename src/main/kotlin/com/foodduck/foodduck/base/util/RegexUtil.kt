package com.foodduck.foodduck.base.util

import java.util.regex.Pattern

class RegexUtil {
    companion object {
        private val EMAIL_PATTERN:Pattern = Pattern.compile("^(.+)@(.+)\\.(.+)\$")
        private val PASSWORD_PATTERN:Pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@\\.%#&()–\\[{}\\]:;',?/*~$^+_=\\-<>|\"]).{8,20}$")

        fun validEmail(email: String):Boolean {
            return EMAIL_PATTERN.matcher(email).matches()
        }

        fun validPassword(password: String): Boolean {
            return PASSWORD_PATTERN.matcher(password).matches()
        }
    }
}