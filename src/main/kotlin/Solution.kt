import java.lang.Exception

class Solution {

    interface CompleteCallback {
        fun onComplete()
        fun onError()
    }

    private val retryPolicy = object: RetryPolicy {

        override fun isFailureRecoverable(ex: Exception) = true

        override fun nextDelayMillis() = 1L
    }

    fun fetchData(requestParams: RequestParams, cb: CompletionHandler<DataResp>) {

    }

    private fun fetchBatchData(requestParams: RequestParams, cb: CompletionHandler<DataResp>) {

        val callable = this::fetchData

        val retryingCallWrapper = RetryingCallWrapper(callable, retryPolicy)

        retryingCallWrapper.call(requestParams, cb)
    }
}