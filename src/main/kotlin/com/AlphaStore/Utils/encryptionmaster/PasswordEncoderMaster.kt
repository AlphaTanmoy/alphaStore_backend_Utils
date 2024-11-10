package com.alphaStore.Utils.encryptionmaster

import com.alphaStore.Utils.KeywordsAndConstants
import org.springframework.stereotype.Component
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Component
class PasswordEncoderMaster {

    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder(KeywordsAndConstants.PASSWORD_CONFIG_BCRYPT_ROUND)
    }

    fun matches(passwordToCheck: String, hashToCheck: String): Boolean {
        return passwordEncoder().matches(passwordToCheck, hashToCheck)
    }

    fun encode(passwordToEncode: String): String {
        return passwordEncoder().encode(passwordToEncode)
    }
}