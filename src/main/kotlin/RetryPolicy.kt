import java.lang.Exception

interface RetryPolicy {

    fun isFailureRecoverable(ex: Exception): Boolean

    fun nextDelayMillis(): Long
}