package br.com.spacebox.api.interfaces;

import br.com.spacebox.api.client.SpaceBoxClient;
import retrofit2.Call;

@FunctionalInterface
public interface CallAPI<TResponse> {
    Call<TResponse> execute(SpaceBoxClient clie);
}