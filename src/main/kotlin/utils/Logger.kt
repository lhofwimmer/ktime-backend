package main.kotlin.utils

import java.text.SimpleDateFormat
import java.util.*

object Logger {
    private const val ANSI_RED = "\u001B[31m"
    private const val ANSI_GREEN = "\u001B[32m"
    private const val ANSI_WHITE = "\u001B[37m"

    private val dateformat = SimpleDateFormat("yyyy-MM-dd HH:mm")

    fun info(message: String) {
        println("$ANSI_WHITE${assembleMessage(message, "info")}")
    }

    fun error(message: String) {
        System.err.println("$ANSI_RED${assembleMessage(message, "error")}")
    }

    private fun assembleMessage(message: String, type: String) =
        "[${dateformat.format(Date())}] ${type.toUpperCase()}: $ANSI_GREEN$message"

}