package com.alphaStore.Utils.jwtUtilMaster

import com.alphaStore.Core.entity.AccessRole
import com.alphaStore.Core.entity.ClientDevice
import com.alphaStore.Core.enums.TokenType
import com.alphaStore.Core.enums.UserType
import com.alphaStore.Core.errormessagereqres.BadRequestExceptionThrowable
import com.alphaStore.Core.errormessagereqres.UnAuthorizedExceptionThrowable
import com.alphaStore.Core.model.SoftTokenResponse
import com.alphaStore.Core.model.TokenCreationResponse
import com.alphaStore.Core.repoAggregator.AccessRoleRepoAggregator
import com.alphaStore.Core.repoAggregator.ClientDeviceRepoAggregator
import com.alphaStore.Utils.KeywordsAndConstants
import com.alphaStore.Utils.contracts.BadRequestException
import com.alphaStore.Utils.dateUtil.DateUtil
import com.alphaStore.Utils.encryptionmaster.EncodingUtil
import com.alphaStore.Utils.encryptionmaster.EncryptionMaster
import com.alphaStore.Utils.error.UnAuthorizedException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import java.time.ZonedDateTime
import java.util.*

@Component
class JwtUtilMaster(
    private val accessRoleRepoAggregator: AccessRoleRepoAggregator,
    private val clientDeviceRepoAggregatorContract: ClientDeviceRepoAggregator,
) {

    fun prepareJWT(
        accessRole: AccessRole,
        id: String,
        userType: UserType,
        softTokenResponse: SoftTokenResponse,
        clientDevice: ClientDevice,
        trackingId: String? = null
    ): TokenCreationResponse {
        val finalTrackingId = trackingId ?: UUID.randomUUID().toString()
        try {
            val key = Keys.hmacShaKeyFor(
                KeywordsAndConstants.GENERIC_JWT_CHOICE_ONE.toByteArray()
            )
            val calNow = ZonedDateTime.now()
            val cal = ZonedDateTime.now().plusMinutes(
                if (
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_TEN ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_NINE ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_EIGHT ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_SEVEN ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_SIX ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_FIVE ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_FOUR ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_THREE ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_TWO
                )
                    KeywordsAndConstants.JWT_TIME_OUT_MINUTES_SHORT.toLong()
                else if (
                    clientDevice.trusted
                )
                    KeywordsAndConstants.JWT_TIME_OUT_MINUTES_LONG.toLong()
                else
                    KeywordsAndConstants.JWT_TIME_OUT_MINUTES_NORMAL.toLong()
            )
            val date = DateUtil.getDateFromZonedDateTime(cal)
            val token = Jwts
                .builder()
                .subject(
                    when (softTokenResponse.tokenType) {
                        TokenType.JWT_SUB -> {
                            KeywordsAndConstants.TOKEN_TIRE_ONE
                        }

                        TokenType.SOFT_TOKEN_TIRE_TWO -> {
                            KeywordsAndConstants.TOKEN_TIRE_TWO
                        }

                        TokenType.SOFT_TOKEN_TIRE_THREE -> {
                            KeywordsAndConstants.TOKEN_TIRE_THREE
                        }

                        TokenType.SOFT_TOKEN_TIRE_FOUR -> {
                            KeywordsAndConstants.TOKEN_TIRE_FOUR
                        }

                        TokenType.SOFT_TOKEN_TIRE_FIVE -> {
                            KeywordsAndConstants.TOKEN_TIRE_FIVE
                        }

                        TokenType.SOFT_TOKEN_TIRE_SIX -> {
                            KeywordsAndConstants.TOKEN_TIRE_SIX
                        }

                        TokenType.SOFT_TOKEN_TIRE_SEVEN -> {
                            KeywordsAndConstants.TOKEN_TIRE_SEVEN
                        }

                        TokenType.SOFT_TOKEN_TIRE_EIGHT -> {
                            KeywordsAndConstants.TOKEN_TIRE_EIGHT
                        }

                        TokenType.SOFT_TOKEN_TIRE_NINE -> {
                            KeywordsAndConstants.TOKEN_TIRE_NINE
                        }

                        TokenType.SOFT_TOKEN_TIRE_TEN -> {
                            KeywordsAndConstants.TOKEN_TIRE_TEN
                        }
                    }
                )
                .claim("id", id)
                .claim("type", userType.name)
                .claim("clientDeviceId", clientDevice.id)
                .claim("trackingId", finalTrackingId)
                .claim(
                    "createdAt",
                    EncodingUtil.encode(
                        EncryptionMaster.encrypt(
                            DateUtil.getStringFromZonedDateTimeUsingIsoDateFormat(calNow)
                        )
                    )
                )
                .claim(
                    "expAt",
                    EncodingUtil.encode(
                        EncryptionMaster.encrypt(
                            DateUtil.getStringFromZonedDateTimeUsingIsoDateFormat(cal)
                        )
                    )
                )
                .expiration(date)
                .signWith(
                    key
                )
                .compact()
            val calRefresh = ZonedDateTime.now().plusMinutes(
                if (
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_TEN ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_NINE ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_EIGHT ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_SEVEN ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_SIX ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_FIVE ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_FOUR ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_THREE ||
                    softTokenResponse.tokenType == TokenType.SOFT_TOKEN_TIRE_TWO
                )
                    KeywordsAndConstants.REFRESH_TIMEOUT_MINUTES_SHORT.toLong()
                else if (
                    clientDevice.trusted
                )
                    KeywordsAndConstants.REFRESH_TIMEOUT_MINUTES_LONG.toLong()
                else
                    KeywordsAndConstants.REFRESH_TIMEOUT_MINUTES_NORMAL.toLong()
            )
            val dateRefresh = DateUtil.getDateFromZonedDateTime(calRefresh)
            val refreshToken = Jwts
                .builder()
                .subject(KeywordsAndConstants.REFRESH_TOKEN_SUB)
                .claim("id", id)
                .claim("for", token)
                .claim("type", userType.name)
                .claim("clientDeviceId", clientDevice.id)
                .claim("trackingId", finalTrackingId)
                .claim(
                    "createdAt",
                    EncodingUtil.encode(
                        EncryptionMaster.encrypt(
                            DateUtil.getStringFromZonedDateTimeUsingIsoDateFormat(calNow),
                        )
                    )
                )
                .claim(
                    "expAt",
                    EncodingUtil.encode(
                        EncryptionMaster.encrypt(
                            DateUtil.getStringFromZonedDateTimeUsingIsoDateFormat(calRefresh),
                        )
                    )
                )
                .expiration(dateRefresh)
                .signWith(
                    key
                )
                .compact()
            return TokenCreationResponse(
                token,
                refreshToken
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw UnAuthorizedException()
        }
    }

    fun getBody(
        authToken: String,
        tryOne: Boolean = true,
        tryTwo: Boolean = false,
        tryThree: Boolean = false,
        shouldThrowError: Boolean = true
    ): Optional<Claims> {
        val key = Keys.hmacShaKeyFor(
            if (tryOne)
                KeywordsAndConstants.GENERIC_JWT_CHOICE_ONE.toByteArray()
            else if (tryTwo)
                KeywordsAndConstants.GENERIC_JWT_CHOICE_TWO.toByteArray()
            else
                KeywordsAndConstants.GENERIC_JWT_CHOICE_THREE.toByteArray()
        )

        try {
            val claimsJWS: Jws<Claims> = Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(authToken)

            return Optional.of(claimsJWS.payload)
        } catch (expiredJwtException: ExpiredJwtException) {
            if (shouldThrowError)
                throw UnAuthorizedExceptionThrowable(
                    errorMessage = KeywordsAndConstants.TOKEN_EXPIRED_DESCRIPTION,
                    code = KeywordsAndConstants.TOKEN_EXPIRED
                )
            else
                return Optional.empty()
        } catch (signatureException: SignatureException) {
            return if (tryOne)
                getBody(
                    authToken,
                    tryOne = false,
                    tryTwo = true,
                    tryThree = false,
                    shouldThrowError = shouldThrowError
                )
            else if (tryTwo)
                getBody(
                    authToken,
                    tryOne = false,
                    tryTwo = false,
                    tryThree = true,
                    shouldThrowError = shouldThrowError
                )
            else {
                if (shouldThrowError)
                    throw UnAuthorizedExceptionThrowable(
                        errorMessage = KeywordsAndConstants.TOKEN_NOT_VALID_DESCRIPTION,
                        code = KeywordsAndConstants.TOKEN_NOT_VALID
                    )
                else
                    return Optional.empty()
            }
        } catch (ex: Exception) {
            if (shouldThrowError)
                throw BadRequestExceptionThrowable(errorMessage = "Unable to process")
            return Optional.empty()
        }
    }

    fun getAccessRoleFromToken(
        token: String,
        throwErrorIfNotFound: Boolean = false
    ): Optional<AccessRole> {
        val optionalBody = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, "")
        )
        return if (optionalBody.isPresent) {
            val body: Claims = optionalBody.get()
            val accessRoleId = body["role"]
            val accessRoleFromDB = accessRoleRepoAggregator.findByIdAndDataStatus(
                "$accessRoleId",

                )
            if (throwErrorIfNotFound && accessRoleFromDB.data.isEmpty()) {
                throw BadRequestException("Access role not found")
            }
            if (accessRoleFromDB.data.isEmpty()) {
                return Optional.empty()
            } else {
                Optional.of(accessRoleFromDB.data[0])
            }
            /*
            this is not working for inner items. need to check wit redis for it to work.
             */
            /* if (redisObjectMaster.checkIfPresentAccessRole(accessRoleId.toString())) {
                 Optional.of(redisObjectMaster.getObjectAccessRole(accessRoleId.toString()))
             } else {
                 val accessRoleFromDB = accessRoleRepoAggregator.findByIdAndDataStatus("$accessRoleId")
                 redisObjectMaster.saveAccessRole(accessRoleFromDB[0], accessRoleId.toString())
                 Optional.of(accessRoleFromDB[0])
             }*/
        } else {
            Optional.empty()
        }
    }

    fun getUserType(token: String): Optional<UserType> {
        val bodyOptional = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, "")
        )
        return if (bodyOptional.isPresent) {
            val type = bodyOptional.get()["type"]
            Optional.of(UserType.valueOf(type.toString()))
        } else {
            Optional.empty()
        }
    }

    fun getTrackingId(token: String): String {
        val bodyOptional = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, "")
        )
        return if (bodyOptional.isPresent) {
            val trackingId = bodyOptional.get()["trackingId"]
            trackingId.toString()
        } else {
            throw BadRequestException("System error")
        }
    }

    fun getUserId(token: String): Optional<String> {
        val bodyOptional = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, ""),
        )
        return if (bodyOptional.isPresent) {
            val id = bodyOptional.get()["id"]
            Optional.of(id.toString())
        } else {
            Optional.empty()
        }
    }

    fun getAccessRoleId(token: String): Optional<String> {
        val bodyOptional = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, ""),
        )
        return if (bodyOptional.isPresent) {
            val id = bodyOptional.get()["id"]
            Optional.of(id.toString())
        } else {
            Optional.empty()
        }
    }

    fun getApplicationGroupCode(token: String): Optional<String> {
        val bodyOptional = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, "")
        )
        return if (bodyOptional.isPresent) {
            val id = bodyOptional.get()["application_group_code"]
            Optional.of(id.toString())
        } else {
            Optional.empty()
        }
    }

    fun getApplicationGroupName(token: String): Optional<String> {
        val bodyOptional = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, "")
        )
        return if (bodyOptional.isPresent) {
            val id = bodyOptional.get()["application_group_name"]
            Optional.of(id.toString())
        } else {
            Optional.empty()
        }
    }

    fun getCountryId(token: String): Optional<String> {
        val bodyOptional = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, "")
        )
        return if (bodyOptional.isPresent) {
            val id = bodyOptional.get()["country_id"]
            Optional.of(id.toString())
        } else {
            Optional.empty()
        }
    }

    fun getCountryName(token: String): Optional<String> {
        val bodyOptional = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, "")
        )
        return if (bodyOptional.isPresent) {
            val name = bodyOptional.get()["known_name"].toString()
            Optional.of(name)
        } else {
            Optional.empty()
        }
    }

    /*fun getCountryType(token: String): Optional<CountryName> {
        val bodyOptional = getBody(
            token.replace(KeywordsAndConstants.tokenPrefix, "")
        )
        return if (bodyOptional.isPresent) {
            val type = bodyOptional.get()["type"]
            Optional.of(UserType.valueOf(type.toString()))
        } else {
            Optional.empty()
        }
    }*/


    /*fun getUserFromToken(
        token: String,
        throwErrorIfNotFound: Boolean = true,
        throwErrorIfNotEligibleForDataModification: Boolean = false
    ): Optional<User> {
        val bodyOptional = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, "")
        )
        return if (bodyOptional.isPresent) {
            val id = bodyOptional.get()["id"]
            val toReturn = userRepoAggregatorContract.findByIdAndDataStatus(
                id = id.toString(),

                )
            if (throwErrorIfNotFound && toReturn.data.isEmpty()) {
                throw BadRequestException("User not found")
            }
            if (throwErrorIfNotEligibleForDataModification && !toReturn.data[0].isPropagable) {
                throw throw ForbiddenException("You can not create application")
            }
            if (toReturn.data.isEmpty()) {
                return Optional.empty()
            } else {
                Optional.of(toReturn.data[0])
            }
        } else {
            Optional.empty()
        }
    }*/


    fun getClientDeviceFromToken(token: String): Optional<ClientDevice> {

        val bodyOptional = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, "")
        )
        return if (bodyOptional.isPresent) {
            val clientDeviceId = bodyOptional.get()["clientDeviceId"]
            val toReturn = clientDeviceRepoAggregatorContract.findByIdAndDataStatus(
                deviceId = clientDeviceId.toString(),
            )
            if (toReturn.data.isEmpty()) {
                throw BadRequestException("Client device not found")
            }
            Optional.of(toReturn.data[0])
        } else {
            Optional.empty()
        }
    }

    fun mayGetClientDeviceId(token: String): String? {
        if (token == "")
            return null
        val bodyOptional = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, "")
        )
        return if (bodyOptional.isPresent) {
            val clientDeviceId = bodyOptional.get()["clientDeviceId"]
            clientDeviceId.toString()
        } else {
            null
        }
    }

    fun getExpDate(token: String): Optional<ZonedDateTime> {
        try {
            val bodyOptional = getBody(
                token.replace(KeywordsAndConstants.TOKEN_PREFIX, ""),
                shouldThrowError = false
            )
            if (bodyOptional.isPresent) {
                val expAt =
                    EncryptionMaster.decrypt(EncodingUtil.decode(bodyOptional.get()["expAt"].toString()))
                val expDateOpt =
                    DateUtil.getZonedDateTimeFromStringUsingIsoFormatServerTimeZone(expAt)
                if (expDateOpt.isEmpty) {
                    throw BadRequestException("Invalid refresh token")
                }
                return Optional.of(expDateOpt.get())
            } else {
                val body: HashMap<String, String> = getBodyFromJwtIgnoringExpiryDate(
                    token.replace(KeywordsAndConstants.TOKEN_PREFIX, "")
                )
                val expAt = EncryptionMaster.decrypt(EncodingUtil.decode(body["expAt"].toString()))
                val expDateOpt =
                    DateUtil.getZonedDateTimeFromStringUsingIsoFormatServerTimeZone(expAt)
                if (expDateOpt.isEmpty) {
                    throw BadRequestException("Invalid refresh token")
                }
                return Optional.of(expDateOpt.get())
            }
        } catch (_: Exception) {
            return Optional.empty()
        }
    }

    fun getBodyFromJwtIgnoringExpiryDate(
        authToken: String,
        tryOne: Boolean = true,
        tryTwo: Boolean = false,
        tryThree: Boolean = false,
    ): HashMap<String, String> {
        val mapToReturn = hashMapOf<String, String>()
        val key = Keys.hmacShaKeyFor(
            if (tryOne)
                KeywordsAndConstants.GENERIC_JWT_CHOICE_ONE.toByteArray()
            else if (tryTwo)
                KeywordsAndConstants.GENERIC_JWT_CHOICE_TWO.toByteArray()
            else
                KeywordsAndConstants.GENERIC_JWT_CHOICE_THREE.toByteArray()
        )

        try {
            val claimsJWS: Jws<Claims> = Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(authToken)

            mapToReturn["id"] = claimsJWS.payload["id"].toString()
            mapToReturn["type"] = claimsJWS.payload["type"].toString()
            return mapToReturn
        } catch (expiredJwtException: ExpiredJwtException) {
            val dataPartOfJwt = authToken.split(".")[1]
            var base64Decoded = String(Base64.getDecoder().decode(dataPartOfJwt))
            base64Decoded = base64Decoded.replace("{", "'").replace("}", "")
            val splits = base64Decoded.split(",")
            splits.forEach {
                val innerSplits = it.split(":")
                mapToReturn[innerSplits[0].replace("\"", "")] =
                    innerSplits[1].replace("\"", "")
            }
            return mapToReturn
        } catch (signatureException: SignatureException) {
            return if (tryOne)
                getBodyFromJwtIgnoringExpiryDate(authToken, tryOne = false, tryTwo = true, tryThree = false)
            else if (tryTwo)
                getBodyFromJwtIgnoringExpiryDate(authToken, tryOne = false, tryTwo = false, tryThree = true)
            else
                throw UnAuthorizedExceptionThrowable(
                    errorMessage = KeywordsAndConstants.TOKEN_NOT_VALID_DESCRIPTION,
                    code = KeywordsAndConstants.TOKEN_NOT_VALID
                )
        } catch (_: Exception) {
            throw BadRequestExceptionThrowable(errorMessage = "Unable to process")
        }
    }

    fun getJwtAuth(serverWebExchange: ServerWebExchange, mustReturnTokenOrElseCrash: Boolean = false): String? {
        var header = serverWebExchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        header?.let {
            KeywordsAndConstants.NON_AUTH_APIS.split(",").forEach { api ->
                if (serverWebExchange.request.uri.toString().contains(api, ignoreCase = true)) {
                    return null
                }
            }
        } ?: run {
            if (mustReturnTokenOrElseCrash)
                throw UnAuthorizedExceptionThrowable("Please provide auth token")
            else
                return null
        }
        if (!header.contains(KeywordsAndConstants.TOKEN_PREFIX)) {
            throw UnAuthorizedExceptionThrowable("Please provide auth token")
        }
        header = header.replace(KeywordsAndConstants.TOKEN_PREFIX, "")
        return header
    }
}