package br.com.spacebox.ui.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.spacebox.R;
import br.com.spacebox.api.client.SpaceBoxClient;
import br.com.spacebox.api.interfaces.CallAPI;
import br.com.spacebox.api.interfaces.CallAPIPostProcessorError;
import br.com.spacebox.api.interfaces.CallAPIPostProcessorSuccess;
import br.com.spacebox.api.model.error.APIError;
import br.com.spacebox.constants.SpaceBoxConst;
import br.com.spacebox.ui.LoginActivity;
import br.com.spacebox.utils.AsyncTaskCustom;
import br.com.spacebox.utils.AsyncTaskRecurrence;
import br.com.spacebox.utils.ErrorUtils;
import br.com.spacebox.utils.IExecuteInBackground;
import br.com.spacebox.utils.IExecuteInUIThread;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public interface IBaseCommon {

    Activity getCommonActivity();

    Context getCommonContext();

    default <T> void callAPI(ProgressBar progressBar, CallAPI<T> callEndpoint, CallAPIPostProcessorSuccess<T> callAPISuccess, CallAPIPostProcessorError<T> callAPIError, boolean disableHideLoading) {
        Call<T> call = callEndpoint.execute(SpaceBoxClient.getInstance());

        showLoading(progressBar);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    callAPISuccess.execute(response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response, getCommonContext().getResources());
                    Toast.makeText(getCommonContext(), error.getError(), Toast.LENGTH_SHORT).show();

                    if (callAPIError != null)
                        callAPIError.execute(call, response, null);

                    if (error.getCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        Intent myIntent = new Intent(getCommonContext(), LoginActivity.class);
                        getCommonContext().startActivity(myIntent);
                        getCommonActivity().finish();
                    }
                }

                if (!disableHideLoading)
                    hideLoading(progressBar);
            }

            @Override
            public void onFailure(Call<T> call, Throwable throwable) {
                if (throwable instanceof SocketTimeoutException)
                    Toast.makeText(getCommonContext(), R.string.http_error_503, Toast.LENGTH_SHORT).show();
                else if ((throwable instanceof ConnectException) || (throwable instanceof UnknownHostException))
                    Toast.makeText(getCommonContext(), R.string.internetNotAvailable, Toast.LENGTH_SHORT).show();

                if (callAPIError != null)
                    callAPIError.execute(call, null, throwable);

                hideLoading(progressBar);
            }
        });
    }

    default void showLoading() {
        showLoading(null);
    }

    default void showLoading(ProgressBar progressBar) {
        final ProgressBar pb = (progressBar == null) ? getCommonActivity().findViewById(R.id.progressBar) : progressBar;
        getCommonActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (pb != null)
            pb.setVisibility(View.VISIBLE);
    }

    default void toastMessage(int messageId) {
        Toast.makeText(getCommonContext(), getCommonContext().getResources().getString(messageId), Toast.LENGTH_SHORT).show();
    }

    default void hideLoading() {
        showLoading(null);
    }

    default void hideLoading(ProgressBar progressBar) {
        final ProgressBar pb = (progressBar == null) ? getCommonActivity().findViewById(R.id.progressBar) : progressBar;
        getCommonActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (pb != null)
            pb.setVisibility(View.INVISIBLE);
    }

    default <T> void callAPI(CallAPI<T> callEndpoint, CallAPIPostProcessorSuccess<T> callAPISuccess, boolean disableHideLoading) {
        callAPI(null, callEndpoint, callAPISuccess, null, disableHideLoading);
    }

    default <T> void callAPI(CallAPI<T> callEndpoint, CallAPIPostProcessorSuccess<T> callAPISuccess) {
        callAPI(callEndpoint, callAPISuccess, null);
    }

    default <T> void callAPI(ProgressBar progressBar, CallAPI<T> callEndpoint, CallAPIPostProcessorSuccess<T> callAPISuccessm) {
        callAPI(progressBar, callEndpoint, callAPISuccessm, null, false);
    }

    default <T> void callAPI(CallAPI<T> callEndpoint, CallAPIPostProcessorSuccess<T> callAPISuccessm, CallAPIPostProcessorError<T> callAPIError) {
        callAPI(null, callEndpoint, callAPISuccessm, callAPIError, false);
    }

    default <TParams, TResult> void callTaskAsyncRecurrence(IExecuteInBackground<TParams, TResult> executeInBackground) {
        callTaskAsyncRecurrence(executeInBackground, null);
    }

    default <TParams, TResult> void callTaskAsyncRecurrence(IExecuteInBackground<TParams, TResult> executeInBackground, IExecuteInUIThread<TResult> executeInUIThread) {
        AsyncTaskRecurrence<TParams, TResult> asyncTaskRecurrence = new AsyncTaskRecurrence<>(SpaceBoxConst.SYNC_INTERVAL);
        asyncTaskRecurrence.execute(executeInBackground, executeInUIThread);
    }

    default <TParams, TResult> void callTaskAsync(IExecuteInBackground<TParams, TResult> executeInBackground, IExecuteInUIThread<TResult> executeInUIThread) {
        (new AsyncTaskCustom<TParams, TResult>()).execute(executeInBackground, executeInUIThread);
    }

    default void showDialogYesOrNo(int messageId, DialogInterface.OnClickListener dialogClickListenerYes, DialogInterface.OnClickListener dialogClickListenerNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getCommonContext());
        builder.setTitle(R.string.warning);
        builder.setMessage(getCommonContext().getResources().getString(messageId));

        builder.setPositiveButton(R.string.yes, dialogClickListenerYes);
        builder.setNegativeButton(R.string.no, dialogClickListenerNo);

        AlertDialog alert = builder.create();
        alert.show();
    }

    default void showDialogYesOrNo(int messageId, DialogInterface.OnClickListener dialogClickListenerYes) {
        showDialogYesOrNo(messageId, dialogClickListenerYes, (dialog, which) -> {
            dialog.dismiss();
        });
    }
}
