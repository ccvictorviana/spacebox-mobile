package br.com.spacebox.api.interfaces;

@FunctionalInterface
public interface CallAPIPostProcessorSuccess<TResponse> {
    void execute(TResponse clie);
}