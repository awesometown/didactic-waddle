package com.serverless

data class CheckJobStatusEvent(var jobId: String) {
    constructor() : this("")
}