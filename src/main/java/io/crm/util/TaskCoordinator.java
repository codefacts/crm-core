package io.crm.util;

import io.crm.intfs.ConsumerInterface;
import io.crm.intfs.Runnable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.crm.util.ExceptionUtil.*;

/**
 * Created by someone on 12-Aug-2015.
 */
final public class TaskCoordinator {
    private final Message message;
    private io.crm.intfs.Runnable onSuccess;
    private ConsumerInterface<Throwable> onError;
    private ConsumerInterface<TaskCoordinator> onComplete;
    private int count;
    private Throwable error;

    TaskCoordinator(int count, Message message, Runnable onSuccess, ConsumerInterface<Throwable> onError, ConsumerInterface<TaskCoordinator> onComplete) {
        this.count = count;
        this.message = message;
        this.onSuccess = onSuccess;
        this.onError = onError;
        this.onComplete = onComplete;
    }

    public <T> Handler<AsyncResult<T>> add(ConsumerInterface<T> consumer) {
        return r -> {
            final ArrayList<Exception> exceptions = new ArrayList<>();
            count--;
            if (r.failed()) {
                error = r.cause();
                if (message != null) fail(message, r.cause());
            } else {
                if (consumer != null) try {
                    consumer.accept(r.result());
                } catch (Exception ex) {
                    fail(message, ex);
                    exceptions.add(ex);
                }
            }

            onFinish(exceptions);
        };
    }

    private final void onFinish(ArrayList<Exception> exceptions) {
        if (count <= 0) {
            if (error == null) {
                if (onSuccess != null) try {
                    onSuccess.run();
                } catch (Exception ex) {
                    fail(message, ex);
                    exceptions.add(ex);
                }
            } else {
                if (onError != null) try {
                    onError.accept(error);
                } catch (Exception ex) {
                    exceptions.add(ex);
                }
            }
            if (onComplete != null) try {
                onComplete.accept(this);
            } catch (Exception ex) {
                fail(message, ex);
                exceptions.add(ex);
            }
        }
        if (exceptions.size() > 0) {
            throw new RuntimeException("Exception in execution: " + String.join("; ", exceptions.stream().map(e -> e.getClass().getName() + " : " + e.getMessage()).collect(Collectors.toList())), exceptions.get(0));
        }
    }

    public void countdown(int step) {
        count -= step;
        onFinish(new ArrayList<>());
    }

    public void countdown() {
        countdown(1);
    }

    public boolean isError() {
        return error != null;
    }

    public boolean isSuccess() {
        return count <= 0 && error == null;
    }

    public boolean isComplete() {
        return count <= 0;
    }

    @Override
    public String toString() {
        return String.format("[%s complete: %s error: %s]", this.getClass().getSimpleName(), isComplete(), error);
    }

    public TaskCoordinator onSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }

    public TaskCoordinator onError(ConsumerInterface<Throwable> onError) {
        this.onError = onError;
        return this;
    }

    public TaskCoordinator onComplete(ConsumerInterface<TaskCoordinator> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public static void main(String... args) {
        System.out.println(new TaskCoordinatorBuilder().get());
    }

    public void finish() {
        countdown(count);
    }
}
