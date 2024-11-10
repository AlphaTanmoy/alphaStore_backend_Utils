package com.alphaStore.Utils.ipmonitor

import com.alphaStore.Utils.KeywordsAndConstants
import org.springframework.stereotype.Component

@Component
class ApisAccessLogMaster {

    private val noAuthApis: ArrayList<String> = ArrayList()

    /**
     * to check if the request path is ofa  non auth api
     */
    fun checkIfUrlIsNonAuth(url: String): Boolean {
        if (noAuthApis.size == 0) {
            KeywordsAndConstants.NO_AUTH_APIS.split(",").forEach {
                noAuthApis.add(it.trim())
            }
        }
        var found = false
        noAuthApis.forEach {
            if (url.contains(it)) {
                found = true
            }
        }
        return found
    }

}