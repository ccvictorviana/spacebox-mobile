package br.com.spacebox.utils;

import android.content.res.Resources;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.HttpURLConnection;

import br.com.spacebox.R;
import br.com.spacebox.api.client.SpaceBoxClient;
import br.com.spacebox.api.model.error.APIError;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {

    public static APIError parseError(Response<?> response, Resources resource) {
        APIError error = new APIError();
        Converter<ResponseBody, APIError> converter =
                SpaceBoxClient.getInstance().getRetrofit().responseBodyConverter(APIError.class, new Annotation[0]);

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
        }

        error.setCode(response.code());
        if (!response.isSuccessful() && error.getError() == null)
            error.addError(getMessageByHttpCode(response, resource));

        return error;
    }

    private static String getMessageByHttpCode(Response<?> response, Resources resource) {
        int rId = 0;
        switch (response.code()) {
            case HttpURLConnection.HTTP_FORBIDDEN:
                rId = R.string.http_error_403;
                break;
            case HttpURLConnection.HTTP_UNAVAILABLE:
                rId = R.string.http_error_503;
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
                rId = R.string.http_error_404;
                break;
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                rId = R.string.http_error_500;
                break;
            case HttpURLConnection.HTTP_BAD_REQUEST:
                rId = R.string.http_error_400;
                break;
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                rId = R.string.http_error_401;
                break;
            default:
                rId = R.string.http_error_unexpected;
        }
        return resource.getString(rId);
    }
}