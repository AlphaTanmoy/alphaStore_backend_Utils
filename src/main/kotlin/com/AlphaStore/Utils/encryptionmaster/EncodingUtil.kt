package com.alphaStore.Utils.encryptionmaster

import com.alphaStore.Core.contracts.EncodingUtilContract
import org.springframework.stereotype.Component
import java.util.*

@Component
object EncodingUtil : EncodingUtilContract {
    override fun encode(toEncode: String): String {
        return Base64.getEncoder().encodeToString(toEncode.encodeToByteArray())
    }

    override fun decode(toDecode: String): String {
        return String(Base64.getDecoder().decode(toDecode))
    }
}