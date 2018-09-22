package br.com.spacebox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.modelmapper.ModelMapper;

import br.com.spacebox.R;
import br.com.spacebox.api.client.SpaceBoxClient;
import br.com.spacebox.api.interfaces.CallAPI;
import br.com.spacebox.api.interfaces.CallAPIPostProcessorError;
import br.com.spacebox.api.interfaces.CallAPIPostProcessorSuccess;
import br.com.spacebox.api.model.error.APIError;
import br.com.spacebox.repository.utils.SpaceBoxDatabaseCreate;
import br.com.spacebox.repository.utils.SpaceBoxDatabase;
import br.com.spacebox.sessions.SessionManager;
import br.com.spacebox.utils.AsyncTaskCustom;
import br.com.spacebox.utils.ErrorUtils;
import br.com.spacebox.utils.IExecuteInBackground;
import br.com.spacebox.utils.IExecuteInUIThread;
import br.com.spacebox.utils.AsyncTaskRecurrence;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity {
    protected int syncInterval = 3000;
    protected ModelMapper modelMapper;
    protected SpaceBoxDatabase database;
    protected SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modelMapper = new ModelMapper();
        database = SpaceBoxDatabaseCreate.getInstance(getApplicationContext()).getDataBase();
        sessionManager = SessionManager.getInstance(getApplicationContext());
    }

    protected void startActivity(Class<?> cls) {
        Intent myIntent = new Intent(this, cls);
        super.startActivity(myIntent);
    }

    protected void startActivity(Class<?> cls, String key, String value) {
        Intent myIntent = new Intent(this, cls);
        myIntent.putExtra(key, value);
        super.startActivity(myIntent);
    }

    protected <T> void callAPI(CallAPI<T> callEndpoint, CallAPIPostProcessorSuccess<T> callAPISuccessm) {
        callAPI(callEndpoint, callAPISuccessm, null);
    }

    protected <T> void callAPI(ProgressBar progressBar, CallAPI<T> callEndpoint, CallAPIPostProcessorSuccess<T> callAPISuccessm) {
        callAPI(progressBar, callEndpoint, callAPISuccessm, null);
    }

    protected <T> void callAPI(CallAPI<T> callEndpoint, CallAPIPostProcessorSuccess<T> callAPISuccessm, CallAPIPostProcessorError<T> callAPIError) {
        callAPI(null, callEndpoint, callAPISuccessm, callAPIError);
    }

    protected <TParams, TResult> void callTaskAsyncRecurrence(IExecuteInBackground<TParams, TResult> executeInBackground) {
        callTaskAsyncRecurrence(executeInBackground, null);
    }

    protected <TParams, TResult> void callTaskAsyncRecurrence(IExecuteInBackground<TParams, TResult> executeInBackground, IExecuteInUIThread<TResult> executeInUIThread) {
        AsyncTaskRecurrence<TParams, TResult> asyncTaskRecurrence = new AsyncTaskRecurrence<>(syncInterval);
        asyncTaskRecurrence.execute(executeInBackground, executeInUIThread);
    }

    protected <TParams, TResult> void callTaskAsync(IExecuteInBackground<TParams, TResult> executeInBackground, IExecuteInUIThread<TResult> executeInUIThread) {
        (new AsyncTaskCustom<TParams, TResult>()).execute(executeInBackground, executeInUIThread);
    }

    protected <T> void callAPI(ProgressBar progressBar, CallAPI<T> callEndpoint, CallAPIPostProcessorSuccess<T> callAPISuccess, CallAPIPostProcessorError<T> callAPIError) {
        Call<T> call = callEndpoint.execute(SpaceBoxClient.getInstance());

        showLoading(progressBar);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    callAPISuccess.execute(response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response, getResources());
                    Toast.makeText(getApplicationContext(), error.getError(), Toast.LENGTH_SHORT).show();
                    if (callAPIError != null)
                        callAPIError.execute(call, response);
                }

                hideLoading(progressBar);
            }

            @Override
            public void onFailure(Call<T> call, Throwable throwable) {
                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                if (callAPIError != null)
                    callAPIError.execute(call, null);
                hideLoading(progressBar);
            }
        });
    }

    protected void showLoading() {
        showLoading(null);
    }

    protected void showLoading(ProgressBar progressBar) {
        final ProgressBar pb = (progressBar == null) ? findViewById(R.id.progressBar) : progressBar;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (pb != null)
            pb.setVisibility(View.VISIBLE);
    }

    protected void toastMessage(int messageId) {
        Toast.makeText(getApplicationContext(), getString(messageId), Toast.LENGTH_SHORT).show();
    }

    protected void hideLoading() {
        showLoading(null);
    }

    protected void hideLoading(ProgressBar progressBar) {
        final ProgressBar pb = (progressBar == null) ? findViewById(R.id.progressBar) : progressBar;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (pb != null)
            pb.setVisibility(View.INVISIBLE);
    }
}
