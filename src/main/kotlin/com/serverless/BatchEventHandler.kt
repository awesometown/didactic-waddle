package com.serverless

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import org.apache.logging.log4j.LogManager

class BatchEventHandler : RequestHandler<BatchEvent, ApiGatewayResponse> {


    override fun handleRequest(input:BatchEvent, context: Context): ApiGatewayResponse {
        LOG.info("received: " + input)

        LOG.info("Job {} moved to state {}", input.detail.jobId, input.detail.status)

        when(input.detail.status) {
            "COMPLETED" -> notifySuccess()
            "FAILED" -> notifyFailure()
            else -> {}
        }

        return ApiGatewayResponse.build {
            statusCode = 200
            objectBody = HelloResponse("Received batch event", HashMap())
            headers = mapOf("X-Powered-By" to "AWS Lambda & serverless")
        }
    }

    fun notifySuccess() {
        LOG.info("Job succeeded")
    }

    fun notifyFailure() {
        LOG.info("Job failed")
    }

    companion object {
        private val LOG = LogManager.getLogger(BatchEventHandler::class.java)
    }
}