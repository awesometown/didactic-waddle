package com.serverless

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import org.apache.logging.log4j.LogManager

class GetTranscodesHandler : RequestHandler<Any, Map<String, Boolean>> {

    override fun handleRequest(stuff: Any, context: Context): Map<String, Boolean> {
        LOG.info("Received studyId: " + stuff)


        val transcodes = mapOf("SAN" to true, "TDO" to false, "WEB" to false)

        return transcodes
    }

    companion object {
        private val LOG = LogManager.getLogger(GetTranscodesHandler::class.java)
    }
}