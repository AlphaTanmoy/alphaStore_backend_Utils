package com.alphaStore.Utils.validation

import com.alphaStore.Utils.KeywordsAndConstants
import com.alphaStore.Utils.contracts.BadRequestException
import org.springframework.stereotype.Component

@Component
class ValidateForUUID {

    fun validate(passwordString: String, throwExceptionIfFailed: Boolean = true): Boolean {
        var valid = true
        var passwordValidationMessage = ""
        var genericMessageAdded = false
        if (
            KeywordsAndConstants.PASSWORD_MIN_LENGTH != 0 &&
            passwordString.length < KeywordsAndConstants.PASSWORD_MIN_LENGTH
        ) {
            passwordValidationMessage = "Password minimum length must be ${KeywordsAndConstants.PASSWORD_MIN_LENGTH}. "
            valid = false
        }
        if (
            KeywordsAndConstants.PASSWORD_MUST_HAVE_CAPITAL_LETTER &&
            !passwordString.contains(Regex(".*[A-Z]+.*"))
        ) {
            passwordValidationMessage = "${passwordValidationMessage}Password must have at least one capital latter"
            valid = false
            genericMessageAdded = true
        }
        if (
            KeywordsAndConstants.PASSWORD_MUST_HAVE_SMALL_LETTER &&
            !passwordString.contains(Regex(".*[a-z]+.*"))
        ) {
            passwordValidationMessage = if (genericMessageAdded)
                "$passwordValidationMessage, small letter"
            else
                "${passwordValidationMessage}Password must have at least one small latter"
            valid = false
            genericMessageAdded = true
        }
        if (
            KeywordsAndConstants.PASSWORD_MUST_HAVE_NUMBER &&
            !passwordString.contains(Regex(".*\\d+.*"))
        ) {
            passwordValidationMessage = if (genericMessageAdded)
                "$passwordValidationMessage, number"
            else
                "${passwordValidationMessage}Password must have at least one number"
            valid = false
            genericMessageAdded = true
        }
        if (
            KeywordsAndConstants.PASSWORD_MUST_HAVE_SPECIAL_CHAR &&
            !passwordString.contains(Regex(".*\\W+.*"))
        ) {
            passwordValidationMessage = if (genericMessageAdded)
                "$passwordValidationMessage, special character (@%+#)"
            else
                "${passwordValidationMessage}Password must have at least one special character (@%+#)"
            valid = false
            genericMessageAdded = true
        }

        if (!valid && throwExceptionIfFailed) {
            throw BadRequestException(passwordValidationMessage)
        }

        return valid
    }
}