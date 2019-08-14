import java.lang.Exception

interface CompletionHandler<V> {

    fun onComplete(v: V)

    fun onError(ex: Exception)
}