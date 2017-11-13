package com.serverless

import com.amazonaws.services.batch.AWSBatchClientBuilder
import com.amazonaws.services.batch.model.ContainerOverrides
import com.amazonaws.services.batch.model.DescribeJobsRequest
import com.amazonaws.services.batch.model.KeyValuePair
import com.amazonaws.services.batch.model.SubmitJobRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import org.apache.logging.log4j.LogManager
import java.util.*

class CheckJobStatusHandler : RequestHandler<CheckJobStatusEvent, CheckJobStatusResponse> {

    val batchClient = AWSBatchClientBuilder.defaultClient()

    override fun handleRequest(event: CheckJobStatusEvent, context: Context): CheckJobStatusResponse {
        LOG.info("received: " + event)

        val request = DescribeJobsRequest()
                .withJobs(event.jobId)

        val result = batchClient.describeJobs(request)

        val status = result.jobs[0]?.status ?: "FAILED"

        return CheckJobStatusResponse(status)
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