package com.alphaStore.Utils.contracts

class BadRequestException(
    var errorMessage: String = "",
    var code: Int? = null,
    var type: String? = null
) : RuntimeException()