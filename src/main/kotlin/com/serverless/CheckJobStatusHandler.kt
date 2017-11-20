package com.serverless

import com.amazonaws.services.batch.AWSBatchClientBuilder
import com.amazonaws.services.batch.model.DescribeJobsRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import org.apache.logging.log4j.LogManager

class CheckJobStatusHandler : RequestHandler<String, String> {

    val batchClient = AWSBatchClientBuilder.defaultClient()

    override fun handleRequest(jobId: String, context: Context): String {
        LOG.info("received: " + jobId)

        val request = DescribeJobsRequest()
                .withJobs(jobId)

        val result = batchClient.describeJobs(request)

        val status = result.jobs[0]?.status ?: "FAILED"

        return status
//        return ApiGatewayResponse.build {
//            statusCode = 200
//            objectBody = responseBody
//            headers = Collections.singletonMap<String, String>("X-Powered-By", "AWS Lambda & serverless")
//        }
    }

    companion object {
        private val LOG = LogManager.getLogger(CheckJobStatusHandler::class.java)
    }
}