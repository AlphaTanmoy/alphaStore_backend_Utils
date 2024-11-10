package com.alphaStore.Utils

import java.security.MessageDigest
import java.util.*

object HashMaster {

    fun getHash(tableName: String, vararg params: String): String {
        var finalString = tableName
        params.forEach { param ->
            finalString = "${finalString}${param}"
        }
        val md = MessageDigest.getInstance("MD5")
        val generatedHash = String(Base64.getEncoder().encode(md.digest(finalString.toByteArray())))
        return generatedHash
    }

    fun getHash(stringToHash: String): String {
        val md = MessageDigest.getInstance("MD5")
        return String(Base64.getEncoder().encode(md.digest(stringToHash.toByteArray())))
    }
}