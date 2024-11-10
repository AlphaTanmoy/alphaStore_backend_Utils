package com.alphaStore.Utils

import java.util.*
import kotlin.random.Random

object TextUtil {

    fun capitalizeFirstCharAfterSpaceAndHyphen(toProcess: String): String {
        return toProcess
            .split(" ")
            .joinToString(" ") { word ->
                if ("^[A-Z]+\$".toRegex().matches(word)) {
                    word
                } else {
                    word
                        .lowercase()
                        .replaceFirstChar { letter ->
                            if (letter.isLowerCase())
                                letter.titlecase(Locale.getDefault())
                            else
                                letter.toString()
                        }
                }
            }
            .split("-")
            .joinToString("-") { word ->
                word
                    .replaceFirstChar { letter ->
                        if (letter.isLowerCase())
                            letter.titlecase(Locale.getDefault())
                        else
                            letter.toString()
                    }
            }
    }

    fun generateRandomString(length: Int): String {
        val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    fun String.toPascalCase(): String {
        return this.split(Regex("(?=[A-Z])")).joinToString("") { word ->
            word.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() }
        }
    }

}