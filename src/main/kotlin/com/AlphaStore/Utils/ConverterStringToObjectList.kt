package com.alphaStore.Utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import java.util.*

object ConverterStringToObjectList {

    inline fun <reified T> getObjectList(
        dataToConvert: String,
        shouldIncludeConditionalJsonIgnores: Boolean = true
    ): ArrayList<T> {
        if (dataToConvert.isEmpty())
            return ArrayList()
        val mapper = ObjectMapper()
        mapper.setTimeZone(TimeZone.getDefault())
        mapper.setAnnotationIntrospector(
            object : JacksonAnnotationIntrospector() {
                override fun hasIgnoreMarker(m: AnnotatedMember?): Boolean {
                    m?.let {
                        if (
                            shouldIncludeConditionalJsonIgnores
                        )
                            return false
                    }
                    return super.hasIgnoreMarker(m)
                }
            }
        )
        return mapper
            .findAndRegisterModules()
            .readValue(
                dataToConvert,
                mapper
                    .typeFactory
                    .constructCollectionType(
                        ArrayList::class.java,
                        T::class.java
                    )
            )
    }

    inline fun <reified T> getSingleObject(
        dataToConvert: String,
        shouldIncludeConditionalJsonIgnores: Boolean = true
    ): T {
        val mapper = ObjectMapper()
        mapper.setTimeZone(TimeZone.getDefault())
        mapper.setAnnotationIntrospector(
            object : JacksonAnnotationIntrospector() {
                override fun hasIgnoreMarker(m: AnnotatedMember?): Boolean {
                    m?.let { positiveM ->
                        if (
                            shouldIncludeConditionalJsonIgnores
                        )
                            return false
                    }
                    return super.hasIgnoreMarker(m)
                }
            }
        )
        return mapper
            .findAndRegisterModules()
            .readValue(
                dataToConvert,
                T::class.java
            )
    }

    fun <T> getSingleObjectTypeTwo(
        dataToConvert: String,
        clazz: Class<T>,
        shouldIncludeConditionalJsonIgnores: Boolean = true
    ): T {
        val mapper = ObjectMapper()
        mapper.setTimeZone(TimeZone.getDefault())
        mapper.setAnnotationIntrospector(
            object : JacksonAnnotationIntrospector() {
                override fun hasIgnoreMarker(m: AnnotatedMember?): Boolean {
                    m?.let { positiveM ->
                        if (
                            shouldIncludeConditionalJsonIgnores
                        )
                            return false
                    }
                    return super.hasIgnoreMarker(m)
                }
            }
        )
        return mapper
            .findAndRegisterModules()
            .readValue(
                dataToConvert,
                clazz
            )
    }


    fun convertObjectToString(
        objectToConvert: Any,
        filter: SimpleFilterProvider? = null,
        shouldIncludeConditionalJsonIgnores: Boolean = true
    ): String {
        try {
            return ObjectMapper()
                .setTimeZone(TimeZone.getDefault())
                .setAnnotationIntrospector(
                    object : JacksonAnnotationIntrospector() {
                        override fun hasIgnoreMarker(m: AnnotatedMember?): Boolean {
                            m?.let { positiveM ->
                                if (
                                    shouldIncludeConditionalJsonIgnores
                                )
                                    return false
                            }
                            return super.hasIgnoreMarker(m)
                        }
                    }
                )
                .findAndRegisterModules()
                .writer(filter)
                .writeValueAsString(objectToConvert)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "{}"
    }

    inline fun <reified T> sanitizeForOutput(
        objectToConvert: Any,
    ): ArrayList<T> {
        return getObjectList<T>(
            convertObjectToString(
                objectToConvert,
                shouldIncludeConditionalJsonIgnores = true
            ),
            shouldIncludeConditionalJsonIgnores = false
        )
    }

    inline fun <reified T> sanitizeForOutputSingleObject(
        objectToConvert: Any,
    ): T {
        return getSingleObject<T>(
            convertObjectToString(
                objectToConvert,
                shouldIncludeConditionalJsonIgnores = true
            ),
            shouldIncludeConditionalJsonIgnores = false
        )
    }
}