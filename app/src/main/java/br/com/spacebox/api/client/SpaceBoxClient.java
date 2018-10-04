package br.com.spacebox.api.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import br.com.spacebox.api.converters.DateDeserializer;
import br.com.spacebox.constants.SpaceBoxConst;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpaceBoxClient {
    private static SpaceBoxClient instance = null;
    private Retrofit retrofit;

    private AuthClient authClient;
    private FileClient fileClient;

    private SpaceBoxClient() {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder().addConverterFactory(gsonConverterFactory());
        authClient = retrofitBuilder.baseUrl(SpaceBoxConst.USER_API_CLIENT_BASE_URL).build().create(AuthClient.class);
        fileClient = retrofitBuilder.baseUrl(SpaceBoxConst.FILE_API_CLIENT_BASE_URL).build().create(FileClient.class);
        retrofit = retrofitBuilder.build();
    }

    private GsonConverterFactory gsonConverterFactory() {
        final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();
        return GsonConverterFactory.create(gson);
    }

    public static SpaceBoxClient getInstance() {
        if (instance == null)
            instance = new SpaceBoxClient();

        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public AuthClient auth() {
        return authClient;
    }

    public FileClient file() {
        return fileClient;
    }
}
