package io.crmcore.intfs;

/**
 * Created by someone on 26-Jul-2015.
 */
public interface Callable<T> {
    public T call() throws Exception;
}
