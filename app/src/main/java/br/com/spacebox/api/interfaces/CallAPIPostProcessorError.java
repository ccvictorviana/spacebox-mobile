package br.com.spacebox.api.interfaces;

import retrofit2.Call;
import retrofit2.Response;

@FunctionalInterface
public interface CallAPIPostProcessorError<TResponse> {
    void execute(Call<TResponse> call, Response<TResponse> response, Throwable throwable);
}