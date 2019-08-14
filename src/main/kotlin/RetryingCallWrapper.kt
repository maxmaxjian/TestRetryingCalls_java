import java.lang.Exception
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class RetryingCallWrapper<T> (
    private val callable: (RequestParams, CompletionHandler<T>) -> Unit,
    private val retryPolicy: RetryPolicy,
    private val executor: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(4)) {

    fun call(requestParams: RequestParams, callback: CompletionHandler<T>) {
        val errorHandler = object: CompletionHandler<T> {
            private var mNumOfTries = 0

            override fun onComplete(v: T) {
                if (v.hasError()) {
                    val filterList = v.subList()
                    mNumOfTries++
                    val delayMillis = retryPolicy.nextDelayMillis()
                    schedule({applyTo(filterList, this)}, delayMillis)
                } else {
                    callback.onComplete(v)
                }
            }

            override fun onError(ex: Exception) {
                mNumOfTries++
                if (retryPolicy.isFailureRecoverable(ex)) {
                    val delayMillis = retryPolicy.nextDelayMillis()
                    if (delayMillis > 0) {
                        schedule({applyTo(requestParams, this)}, delayMillis)
                    }
                }
            }
        }

        applyTo(requestParams, errorHandler)
    }

    private fun applyTo(requestParams: RequestParams, handler: CompletionHandler<T>) {
        try {
            callable(requestParams, handler)
        } catch (ex: Exception) {
            handler.onError(ex)
        }
    }

    private fun schedule(runnable: () -> Unit, delay: Long) {
        executor.schedule(runnable, delay, TimeUnit.MILLISECONDS)
    }
}