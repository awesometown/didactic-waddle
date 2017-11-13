package com.serverless

import com.amazonaws.services.batch.AWSBatchClientBuilder
import com.amazonaws.services.batch.model.ContainerOverrides
import com.amazonaws.services.batch.model.KeyValuePair
import com.amazonaws.services.batch.model.SubmitJobRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import org.apache.logging.log4j.LogManager
import java.util.*

class SubmitJobHandler : RequestHandler<Map<String, Any>, SubmitJobResponse> {

    val batchClient = AWSBatchClientBuilder.defaultClient()

    override fun handleRequest(input: Map<String, Any>, context: Context): SubmitJobResponse {
        LOG.info("received: " + input.keys.toString())

        var request = SubmitJobRequest()
                .withJobDefinition("hello-world:2")
                .withJobName("ServerlessTest_" + UUID.randomUUID().toString())
                .withJobQueue("jobmaster-production")
        if (input.containsKey("workflowId")) {
            val containerOverrides = ContainerOverrides().withEnvironment(KeyValuePair().withName("workflowId").withValue(input["workflowId"].toString()))
            request = request.withContainerOverrides(containerOverrides)
        }
        val result = batchClient.submitJob(request)

        val map = hashMapOf<String, String>("jobId" to result.jobId)

        val responseBody = SubmitJobResponse(result.jobId)

        return responseBody
//        return ApiGatewayResponse.build {
//            statusCode = 200
//            objectBody = responseBody
//            headers = Collections.singletonMap<String, String>("X-Powered-By", "AWS Lambda & serverless")
//        }
    }

    companion object {
        private val LOG = LogManager.getLogger(SubmitJobHandler::class.java)
    }
}