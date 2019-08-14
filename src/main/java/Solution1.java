import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.BiFunction;

public class Solution1 {

    private RetryPolicy retryPolicy = new RetryPolicy() {
        @Override
        public boolean isFailureRecoverable(@NotNull Exception ex) {
            return false;
        }

        @Override
        public long nextDelayMillis() {
            return 0;
        }
    };

    private void fetchData(RequestParams requestParams, CompletionHandler<DataResp> handler) {

    }

    private void fetchData_retrying(RequestParams requestParams, CompletionHandler<DataResp> handler) {

        Function2<RequestParams, CompletionHandler<DataResp>, Unit> callable = (requestParams1, handler1) -> {
            fetchData(requestParams1, handler1);
            return null;
        };

        final RetryingCallWrapper retryingCallable
                = new RetryingCallWrapper<DataResp>(callable, retryPolicy, new ScheduledThreadPoolExecutor(1));

        retryingCallable.call(requestParams, handler);
    }

}
