package com.alphaStore.Utils

object KeywordsAndConstants {

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

    const val JWT_TIMEOUT_MINUTES_NORMAL = 50
    const val REFRESH_TIMEOUT_MINUTES_LONG = 300

    const val TOKEN_TIRE_ONE = "link-auth"
    const val REFRESH_TOKEN_SUB = "link-auth-refresh"

    const val TOKEN_EXPIRED_DESCRIPTION = "token is expired"
    const val TOKEN_NOT_VALID_DESCRIPTION = "token is not valid"
    const val TOKEN_BLOCKED_DESCRIPTION = "token is blocked"
    const val TOKEN_REFRESHED_DESCRIPTION = "token is already refreshed"
    const val TOKEN_OR_API_KEY_REQUIRED_DESCRIPTION = "PLease provide either token or api key"


}