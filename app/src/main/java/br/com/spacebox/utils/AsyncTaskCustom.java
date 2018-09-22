package br.com.spacebox.utils;

import android.os.AsyncTask;

public class AsyncTaskCustom<TParams, TResult> {

    public void execute(IExecuteInBackground<TParams, TResult> executeInBackground, IExecuteInUIThread<TResult> executeInUIThread) {
        (new AsyncTask<TParams, Void, TResult>() {
            @Override
            protected TResult doInBackground(TParams... params) {
                return executeInBackground.execute(params);
            }

            @Override
            protected void onPostExecute(TResult result) {
                if (executeInUIThread != null)
                    executeInUIThread.execute(result);
            }
        }).execute();
    }
}