package br.com.spacebox.utils;

@FunctionalInterface
public interface IExecuteInUIThread<TResult> {
    void execute(TResult params);
}