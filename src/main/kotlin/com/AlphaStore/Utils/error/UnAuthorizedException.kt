package com.alphaStore.Utils.error

class UnAuthorizedException(
    var errorMessage: String = "",
    var code: Int? = null
) : RuntimeException()