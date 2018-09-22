package br.com.spacebox.utils;

import java.util.Timer;
import java.util.TimerTask;

public class AsyncTaskRecurrence<TParams, TResult> {
    private long frequency;
    private final Timer timerAsync = new Timer();
    private TimerTask timerTaskAsync;

    public AsyncTaskRecurrence(long frequency) {
        this.frequency = frequency;
    }

    public void execute(IExecuteInBackground<TParams, TResult> executeInBackground, IExecuteInUIThread<TResult> executeInUIThread) {
        timerTaskAsync = new TimerTask() {
            @Override
            public void run() {
                (new AsyncTaskCustom<TParams, TResult>()).execute(executeInBackground, executeInUIThread);
            }
        };
        timerAsync.schedule(timerTaskAsync, 0, this.frequency);
    }
}