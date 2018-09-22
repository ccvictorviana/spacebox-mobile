package br.com.spacebox.api.client;

import br.com.spacebox.api.model.request.LoginRequest;
import br.com.spacebox.api.model.request.UserRequest;
import br.com.spacebox.api.model.response.TokenResponse;
import br.com.spacebox.api.model.response.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface AuthClient {
    @POST("/users/login")
    Call<TokenResponse> login(@Body LoginRequest loginRequest);

    @POST("/users/")
    Call<Void> signup(@Body UserRequest request);

    @PATCH("/users/")
    Call<Void> update(@Header("Authorization") String authorization, @Body UserRequest request);

    @POST("/users/logout")
    Call<Void> logout(@Header("Authorization") String authorization);

    @GET("/users/")
    Call<UserResponse> detail(@Header("Authorization") String authorization);
}