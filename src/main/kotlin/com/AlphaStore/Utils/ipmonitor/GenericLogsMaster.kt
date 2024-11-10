package com.AlphaStore.Utils.ipmonitor

import com.alphaStore.Core.contracts.ClientDeviceRepoAggregatorContract
import com.alphaStore.Core.entity.GenericLogs
import com.alphaStore.Core.enums.ApiTire
import com.alphaStore.Core.enums.HttpMethod
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Component
class GenericLogsMaster(
    private val genericLogsRepoAggregator: GenericLogsRepoAggregator,
    private val clientDeviceRepoAggregatorContract: ClientDeviceRepoAggregatorContract,
) {
    private val genericLogs: ArrayList<GenericLogs> = ArrayList()

    /**
     * method to add a api access log to the database
     */
    fun addGenericLog(
        apiUrl: String,
        userId: String? = null,
        trackingId: String,
        apiTire: ApiTire,
        args: String = "",
        clientDeviceId:String? = null,
        httpMethod: HttpMethod? = null,
    ) {
        val genericLogs = GenericLogs(
            api = apiUrl,
            payload = args,
            userId = userId?:"",
            tokenTire = apiTire,
            clientDeviceId = clientDeviceId,
            httpMethod = httpMethod ?: HttpMethod.GET,
        )
        genericLogs.id = trackingId
        this.genericLogs.add(genericLogs)
    }


    /**
     * any api access log in the class memory needs to be dumped to database if this is not touched since 2 minutes
     */
    fun checkNonAuthGenericLogsAndDumpToDB() {
        val itemsToDump: ArrayList<GenericLogs> = ArrayList()
        genericLogs.forEach {
            if (
                it.lastUpdated.until(OffsetDateTime.now(), ChronoUnit.MINUTES) >= 2
            ) {
                itemsToDump.add(it)
            }
        }
        saveGenericLogs(itemsToDump)
        var deletedAtLeastOne: Boolean
        do {
            deletedAtLeastOne = false
            var indexToDelete = -1
            itemsToDump.forEach {
                if (indexToDelete == -1)
                    genericLogs.forEachIndexed { index, item ->
                        if (indexToDelete == -1)
                            if (item.id == it.id)
                                indexToDelete = index
                    }
            }
            if (indexToDelete != -1) {
                deletedAtLeastOne = true
                genericLogs.removeAt(indexToDelete)
            }
        } while (deletedAtLeastOne)
        reconcileDeviceActiveStatus(itemsToDump)
    }

    private fun saveGenericLogs(logs: ArrayList<GenericLogs>): Boolean {
        try {
            genericLogsRepoAggregator.saveAll(logs)
            return true
        } catch (_: Exception) {
            if (logs.size == 1) {
                genericLogsRepoAggregator.saveAll(logs)
            } else {
                do {
                    val result = saveGenericLogs(arrayListOf(logs[0]))
                    logs.removeAt(0)
                    if (!result) {
                        saveGenericLogs(logs)
                    } else {
                        logs.clear()
                    }
                } while (logs.isNotEmpty())
            }
            return false
        }
    }
    /**
     * Any client device that is not being used currently needs to be set to active false
     * this method does the changes
     * any access log that is being dumped to database needs to be checked if the client device for its still present
     * in the list.
     */
    fun reconcileDeviceActiveStatus(
        genericLogsToDumpToDB: ArrayList<GenericLogs>
    ) {
        val listToGetClientDevicesAndDeactivate: ArrayList<GenericLogs> = ArrayList()
        genericLogsToDumpToDB.forEach { apisAccessLogDumpedToDB ->
            if (apisAccessLogDumpedToDB.clientDeviceId != null &&
                apisAccessLogDumpedToDB.clientDeviceId != ""
            ) {
                var found = false
                genericLogs.find { toFind ->
                    toFind.clientDeviceId != null &&
                            toFind.clientDeviceId != "" &&
                            toFind.clientDeviceId == apisAccessLogDumpedToDB.clientDeviceId
                }?.let { _ ->
                    found = true
                }
                if (!found) {
                    listToGetClientDevicesAndDeactivate.add(apisAccessLogDumpedToDB)
                }
                found = false
            }
        }
        var negativeSimilarToCondition = "("
        listToGetClientDevicesAndDeactivate.forEachIndexed { _, apisAccessLog ->
            if (apisAccessLog.clientDeviceId != null && apisAccessLog.clientDeviceId != "") {
                negativeSimilarToCondition = if (negativeSimilarToCondition == "(") {
                    "$negativeSimilarToCondition${apisAccessLog.clientDeviceId}"
                } else {
                    "$negativeSimilarToCondition|${apisAccessLog.clientDeviceId}"
                }
            }
        }
        negativeSimilarToCondition = "$negativeSimilarToCondition)"
        clientDeviceRepoAggregatorContract.deactivateClientDevices(negativeSimilarToCondition = negativeSimilarToCondition)
    }
    /**
     * set client device as active if the device is being used and present in the apis access log
     */
    fun setClientDevicesAsActive() {
        if (genericLogs.isNotEmpty()) {
            var similarToCondition = "("
            val uniqueClientDeviceIds: ArrayList<String> = ArrayList()
            genericLogs.forEachIndexed { _, apisAccessLog ->
                if (apisAccessLog.clientDeviceId != null && apisAccessLog.clientDeviceId != "") {
                    if (!uniqueClientDeviceIds.contains(apisAccessLog.clientDeviceId)) {
                        uniqueClientDeviceIds.add(apisAccessLog.clientDeviceId!!)
                        similarToCondition = if (similarToCondition == "(") {
                            "$similarToCondition${apisAccessLog.clientDeviceId}"
                        } else {
                            "$similarToCondition|${apisAccessLog.clientDeviceId}"
                        }
                    }
                }
            }
            similarToCondition = "$similarToCondition)"
            if (similarToCondition != "()") {
                val toSave =
                    clientDeviceRepoAggregatorContract.getDevicesToMarkAsActive(
                        similarToCondition = similarToCondition,
                        skipCache = true,
                    )
                toSave.data.forEach { item ->
                    item.active = true
                    item.lastUsed = ZonedDateTime.now()
                    item.lastUpdated = ZonedDateTime.now()
                }
                clientDeviceRepoAggregatorContract.saveAll(toSave.data)
            }
        }
    }
}