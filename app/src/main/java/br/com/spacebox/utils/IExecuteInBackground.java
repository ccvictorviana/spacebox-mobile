package br.com.spacebox.utils;

@FunctionalInterface
public interface IExecuteInBackground<TParams, TResult> {
    TResult execute(TParams... params);
}