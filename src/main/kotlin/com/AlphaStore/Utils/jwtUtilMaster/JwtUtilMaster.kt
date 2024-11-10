package com.alphaStore.Utils.jwtUtilMaster

import com.alphaStore.Core.entity.AccessRole
import com.alphaStore.Core.enums.UserType
import com.alphaStore.Core.errormessagereqres.BadRequestExceptionThrowable
import com.alphaStore.Core.errormessagereqres.UnAuthorizedExceptionThrowable
import com.alphaStore.Core.model.TokenCreationResponse
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
    //private val accessRoleRepoAggregator: AccessRoleRepoAggregator,
    //private val userRepoAggregator: UserRepoAggregator,
) {

    fun prepareJWT(
        accessRole: AccessRole,
        id: String,
        userType: UserType,
        softTokenResponse: String?,
        trackingId: String? = null
    ): TokenCreationResponse {
        val finalTrackingId = trackingId ?: UUID.randomUUID().toString()
        try {
            val key = Keys.hmacShaKeyFor(
                KeywordsAndConstants.GENERIC_JWT_CHOICE_ONE.toByteArray()
            )
            val calNow = ZonedDateTime.now()
            val cal = ZonedDateTime.now().plusMinutes(
                KeywordsAndConstants.JWT_TIMEOUT_MINUTES_NORMAL.toLong()
            )
            val date = DateUtil.getDateFromZonedDateTime(cal)
            val token = Jwts
                .builder()
                .subject(
                    KeywordsAndConstants.TOKEN_TIRE_ONE
                )
                //.claim("role", accessRole.id)
                .claim("id", id)
                .claim("type", userType.name)
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
                            DateUtil.getStringFromZonedDateTimeUsingIsoDateFormat(cal),
                        )
                    )
                )
                .expiration(date)
                .signWith(
                    key
                )
                .compact()
            val calRefresh = ZonedDateTime.now().plusMinutes(
                KeywordsAndConstants.REFRESH_TIMEOUT_MINUTES_LONG.toLong()
            )
            val dateRefresh = DateUtil.getDateFromZonedDateTime(calRefresh)
            val refreshToken = Jwts
                .builder()
                .subject(KeywordsAndConstants.REFRESH_TOKEN_SUB)
                //.claim("role", accessRole.id)
                .claim("id", id)
                .claim("for", token)
                .claim("type", userType.name)
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

    /*fun getAccessRoleFromToken(
        token: String,
        throwErrorIfNotFound: Boolean = false
    ): Optional<AccessRole> {
        val optionalBody = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, ""),
        )
        return if (optionalBody.isPresent) {
            val body: Claims = optionalBody.get()
            val accessRoleId = body["role"]
            val accessRoleFromDB = accessRoleRepoAggregator.findByIdAndDataStatus(
                "$accessRoleId",
            )
            if (throwErrorIfNotFound && accessRoleFromDB.isEmpty()) {
                throw BadRequestException("Access role not found")
            }
            if (accessRoleFromDB.isEmpty()) {
                return Optional.empty()
            } else {
                Optional.of(accessRoleFromDB[0])
            }
        } else {
            Optional.empty()
        }
    }*/

    fun getUserType(token: String): Optional<UserType> {
        val bodyOptional = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, ""),
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
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, ""),
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

    /*fun getUserFromToken(token: String, throwErrorIfNotFound: Boolean = true): Optional<User> {
        val bodyOptional = getBody(
            token.replace(KeywordsAndConstants.TOKEN_PREFIX, ""),
        )
        return if (bodyOptional.isPresent) {
            val id = bodyOptional.get()["id"]
            val toReturn = userRepoAggregator.findByIdAndDataStatus(
                id = id.toString(),
            )
            if (throwErrorIfNotFound && toReturn.isEmpty()) {
                throw BadRequestException("User not found")
            }
            if (toReturn.isEmpty()) {
                return Optional.empty()
            } else {
                Optional.of(toReturn[0])
            }
        } else {
            Optional.empty()
        }
    }*/


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
                    token.replace(KeywordsAndConstants.TOKEN_PREFIX, ""),
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
            KeywordsAndConstants.NO_AUTH_APIS.split(",").forEach { api ->
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