package com.alphaStore.Utils

object KeywordsAndConstants {

    const val MICRO_SERVICE_USER_NAME = "alphaStore"
    const val MICRO_SERVICE_USER_PASSWORD = "alphaStorePassword"

    const val POSTGRES_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS"
    const val DATE_TIME_FORMAT_FOR_FRONTEND = "yyyy-MM-dd'T'HH:mm"
    const val DATE_TIME_FORMAT_FOR_EMAIL = "dd MMM YYYY HH:mm"
    const val DATE_FORMAT_FOR_EMAIL = "dd MMM YYYY"
    const val DATE_TIME_FORMAT_FROM_FRONTEND = "yyyy-MM-ddTHH:mm"

    const val TOKEN_EXPIRED = 401001
    const val TOKEN_NOT_VALID = 401002
    const val TOKEN_BLOCKED = 401003
    const val TOKEN_REFRESHED = 401004
    const val TOKEN_OR_API_KEY_REQUIRED = 401007

    const val PASSWORD_MIN_LENGTH = 6
    const val PASSWORD_MUST_HAVE_CAPITAL_LETTER = true
    const val PASSWORD_MUST_HAVE_SMALL_LETTER = true
    const val PASSWORD_MUST_HAVE_NUMBER = true
    const val PASSWORD_MUST_HAVE_SPECIAL_CHAR = true
    const val PASSWORD_CONFIG_BCRYPT_ROUND = 12

    const val NO_AUTH_APIS = "/api/login/email"
    const val APIS_ALLOWED_WITH_BOTH_KEY_AND_TOKEN = ""
    const val TOKEN_PREFIX = "alpha "

    const val GENERIC_JWT_CHOICE_ONE =
        "ZPXRzAVyv7TlNqcILxi6lclqRU70HdGvwEoKSoTEjK8nEBjdWSwlZO9kigODUcFF0X5R46EghR8eDs4E"
    const val GENERIC_JWT_CHOICE_TWO =
        "jjs82ECubIXT3mNZwsCV1Safh1OTUAsy395ksApTkGeOxwNiKhGbk0gBIxvl4rP6Mjo0ZDHqdq6kV0w2"
    const val GENERIC_JWT_CHOICE_THREE =
        "ALfbJ5066FtVE6vz0lUKqe49hx75Qur3z645L9tZpBeOxhq2u0IfiNaQjakRENeVyra69Fm4ftfba4HX"

    const val REFRESH_TIMEOUT_MINUTES_SHORT = 100
    const val REFRESH_TIMEOUT_MINUTES_NORMAL= 300
    const val REFRESH_TIMEOUT_MINUTES_LONG = 432000

    const val JWT_TIME_OUT_MINUTES_SHORT = 20
    const val JWT_TIME_OUT_MINUTES_NORMAL = 50
    const val JWT_TIME_OUT_MINUTES_LONG = 50

    const val TOKEN_TIRE_ONE = "alphaStore"
    const val REFRESH_TOKEN_SUB = "alphaStore-refresh"
    const val TOKEN_TIRE_TWO = "alphaStore-soft-token-tire-two"
    const val TOKEN_TIRE_THREE = "alphaStore-soft-token-tire-three"
    const val TOKEN_TIRE_FOUR = "alphaStore-soft-token-tire-four"
    const val TOKEN_TIRE_FIVE = "alphaStore-soft-token-tire-five"
    const val TOKEN_TIRE_SIX = "alphaStore-soft-token-tire-six"
    const val TOKEN_TIRE_SEVEN = "alphaStore-soft-token-tire-seven"
    const val TOKEN_TIRE_EIGHT = "alphaStore-soft-token-tire-eight"
    const val TOKEN_TIRE_NINE = "alphaStore-soft-token-tire-nine"
    const val TOKEN_TIRE_TEN = "alphaStore-soft-token-tire-ten"
    const val MAX_RETRY_FOR_FAILED_REQUEST = 5


    const val TOKEN_EXPIRED_DESCRIPTION = "token is expired"
    const val TOKEN_NOT_VALID_DESCRIPTION = "token is not valid"
    const val TOKEN_BLOCKED_DESCRIPTION = "token is blocked"
    const val TOKEN_REFRESHED_DESCRIPTION = "token is already refreshed"
    const val TOKEN_OR_API_KEY_REQUIRED_DESCRIPTION = "PLease provide either token or api key"

    const val ENCRYPTION_PASSWORD_CHOICE_ONE =
        "/h9lW;L-~n>l8j\$!IQJHx1Yx{0Ot7:j%W;5b[^8]q}\"G]9jRJgc#P;pDakVkB}G62twX+M6CpscBD;a="
    const val ENCRYPTION_PASSWORD_CHOICE_TWO =
        "teA5o?MxI<Mcp+\"RZ|UYK{[>{geCYQdo)bUN57U2p|Ea,lwg4dTK+RBcvird5DdmfakeB[Rdn*wIIhmN"
    const val ENCRYPTION_PASSWORD_CHOICE_THREE =
        "/aSM6'A8J,zz9~0)sX*d3pXoo].u<!k+ZThF\\}cRvR7i-\"`X(&'F%4#4c{X8s^/'4Ivh/qKgq;0Q6lz8"
    const val ENCRYPTION_PASSWORD_SALT_CHOICE = "18237012"
    const val LENGTH_FOR_ENCRYPTION_KEY = 80

    const val HEADER_AUTH_TOKEN = "authToken"
    const val HEADER_API_KEY = "apiKey"
    const val HEADER_APIS_ACCESS_LOG_ID = "apisAccessLogsId"
    const val HEADER_CHALLENGE_RESULT = "challengeResult"
    const val HEADER_OTP = "otp"
    const val HEADER_TRACKING_ID = "trackingId"
    const val HEADER_REQUESTING_IP = "requestingIp"

    const val NON_AUTH_APIS = "/country"
    const val apisTireTen=""
    const val apisTireNine=""
    const val apisTireEight=""
    const val apisTireSeven=""
    const val apisTireSix=""
    const val apisTireFive=""
    const val apisTireFour=""
    const val apisTireThree=""
    const val apisTireTwo=""
    const val apisTireOne="all"
}