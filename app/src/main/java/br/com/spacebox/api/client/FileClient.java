package br.com.spacebox.api.client;

import br.com.spacebox.api.model.request.FileFilterRequest;
import br.com.spacebox.api.model.request.FileRenameRequest;
import br.com.spacebox.api.model.request.FileRequest;
import br.com.spacebox.api.model.request.NotificationFilterRequest;
import br.com.spacebox.api.model.response.FileSummaryResponse;
import br.com.spacebox.api.model.response.FilesResponse;
import br.com.spacebox.api.model.response.NotificationsResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FileClient {
    @POST("/files/")
    Call<FileSummaryResponse> create(@Header("Authorization") String token, @Body FileRequest request);

    @DELETE(value = "/files/")
    Call<Void> delete(@Header("Authorization") String token, @Query("fileId") Long fileId);

    @PATCH(value = "/files/")
    Call<Void> update(@Header("Authorization") String token, @Body FileRequest request);

    @PATCH(value = "/files/rename")
    Call<Void> rename(@Header("Authorization") String token, @Body FileRenameRequest request);

    @POST(value = "/files/list")
    Call<FilesResponse> list(@Header("Authorization") String token, @Body FileFilterRequest request);

    @GET(value = "/files/download")
    Call<ResponseBody> download(@Header("Authorization") String token, @Query("fileId") Long fileId);

    @POST(value = "/notifications/")
    Call<NotificationsResponse> notifications(@Header("Authorization") String token, @Body NotificationFilterRequest request);
}