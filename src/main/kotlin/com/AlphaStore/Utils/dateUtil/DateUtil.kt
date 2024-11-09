package com.alphaStore.Utils.dateUtil

import com.alphaStore.Utils.KeywordsAndConstants
import com.alphaStore.Utils.contracts.BadRequestException
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.net.URLDecoder
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*


@Component
@Primary
object DateUtil {

    fun getStringFromZonedDateTimeUsingIsoDateFormat(zonedDateTime: ZonedDateTime?): String {
        var zonedDateTimeToConvert = ZonedDateTime.now()
        zonedDateTime?.let {
            zonedDateTimeToConvert = it
        }
        val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
        return zonedDateTimeToConvert.format(dateTimeFormatter)
    }

    fun getStringFromZonedDateTimeForPostgres(zonedDateTime: ZonedDateTime?): String {
        var zonedDateTimeToConvert = ZonedDateTime.now()
        zonedDateTime?.let {
            zonedDateTimeToConvert = it
        }
        val dateTimeFormatter = DateTimeFormatter.ofPattern(KeywordsAndConstants.POSTGRES_DATE_FORMAT)
        return zonedDateTimeToConvert.format(dateTimeFormatter)
    }

    fun getStringFromZonedDateTimeForFrontEndFriendly(zonedDateTime: ZonedDateTime?): String {
        var zonedDateTimeToConvert = ZonedDateTime.now()
        zonedDateTime?.let {
            zonedDateTimeToConvert = it
        }
        val dateTimeFormatter = DateTimeFormatter.ofPattern(KeywordsAndConstants.DATE_TIME_FORMAT_FOR_FRONTEND)
        return zonedDateTimeToConvert.format(dateTimeFormatter)
    }

    fun getUserFriendlyDateTimeFromPostgresDateTime(zonedDateTime: ZonedDateTime?): String {
        var zonedDateTimeToConvert = ZonedDateTime.now()
        zonedDateTime?.let {
            zonedDateTimeToConvert = it
        }
        val dateTimeFormatter = DateTimeFormatter.ofPattern(KeywordsAndConstants.DATE_TIME_FORMAT_FOR_EMAIL)
        return zonedDateTimeToConvert.format(dateTimeFormatter)
    }

    fun getUserFriendlyDateFromPostgresDateTime(zonedDateTime: ZonedDateTime?): String {
        var zonedDateTimeToConvert = ZonedDateTime.now()
        zonedDateTime?.let {
            zonedDateTimeToConvert = it
        }
        val dateTimeFormatter = DateTimeFormatter.ofPattern(KeywordsAndConstants.DATE_FORMAT_FOR_EMAIL)
        return zonedDateTimeToConvert.format(dateTimeFormatter)
    }

    fun getZonedDateFromString(
        stringRep: String,
        format: String,
        timeZone: String?
    ): Optional<ZonedDateTime> {
        val stringToParse = sanitizeDateTimeString(stringRep, removeT = true)
        return try {
            val dateTimeFormatter = DateTimeFormatter.ofPattern(format.replace("T", " "))
            val zonedDateTime = LocalDate.parse(
                stringToParse,
                dateTimeFormatter
            )
                .atStartOfDay(
                    timeZone?.let { timeZonePositive ->
                        ZoneId.of(timeZonePositive)
                    } ?: run {
                        ZoneId.systemDefault()
                    }
                )
            Optional.of(zonedDateTime)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Optional.empty()
        }
    }

    fun getZonedDateTimeFromString(
        stringRep: String,
        format: String,
        timeZone: String? = null
    ): Optional<ZonedDateTime> {
        val stringToParse = sanitizeDateTimeString(stringRep, removeT = true)
        return try {
            val dateTimeFormatter = DateTimeFormatter.ofPattern(format.replace("T", " "))
            val localDateTime = LocalDateTime.parse(stringToParse, dateTimeFormatter)
            timeZone?.let { timeZonePositive ->
                Optional.of(localDateTime.atZone(ZoneId.of(timeZonePositive)))
            } ?: run {
                Optional.of(localDateTime.atZone(ZoneId.systemDefault()))
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            Optional.empty()
        }
    }

    fun getZonedDateTimeFromStringUsingIsoFormatServerTimeZone(
        stringRep: String,
    ): Optional<ZonedDateTime> {
        val stringToParse = sanitizeDateTimeString(stringRep)
        return try {
            val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
            val localDateTime = LocalDateTime.parse(stringToParse, dateTimeFormatter)
            Optional.of(localDateTime.atZone(ZoneId.systemDefault()))
        } catch (ex: Exception) {
            ex.printStackTrace()
            Optional.empty()
        }
    }

    private fun sanitizeDateTimeString(stringToProcess: String, removeT: Boolean = false): String {
        var result = if (stringToProcess.contains(" ")) {
            stringToProcess.replace(" ", "+")
        } else {
            stringToProcess
        }
        if (removeT) {
            result = if (stringToProcess.contains("T")) {
                stringToProcess.replace("T", " ")
            } else {
                stringToProcess
            }
        }
        return result
    }

    fun getDateFromZonedDateTime(zonedDateTime: ZonedDateTime): Date {
        return Date.from(zonedDateTime.toInstant())
    }

    fun getMinZonedDateTime(): ZonedDateTime {
        val minInstant = Instant.ofEpochMilli(Long.MIN_VALUE)
        return minInstant.atZone(ZoneId.systemDefault())
    }

    fun getMaxZonedDateTime(): ZonedDateTime {
        val minInstant = Instant.ofEpochMilli(Long.MAX_VALUE)
        return minInstant.atZone(ZoneId.systemDefault())
    }

    fun getDateForSystemBlockScheduleController(dateString: String?): ZonedDateTime {
        var dateToReturn: ZonedDateTime? = null
        dateString?.let { activeFromString ->
            val decryptedFromDate = getZonedDateTimeFromString(
                URLDecoder.decode(activeFromString, "UTF-8"),
                KeywordsAndConstants.DATE_TIME_FORMAT_FROM_FRONTEND
            )
            dateToReturn = if (decryptedFromDate.isEmpty)
                throw BadRequestException("Please enter a valid fromDate string")
            else
                decryptedFromDate.get()
        } ?: run {
            throw BadRequestException("Please enter a valid fromDate string")
        }
        return dateToReturn!!
    }

}