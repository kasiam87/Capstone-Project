package com.udacity.android.makeupapp.database;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

import timber.log.Timber;

public class DatabaseCall {

    public <R> R execute(Callable<R> get) {
        LinkedBlockingQueue<R> queue = new LinkedBlockingQueue<>();
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                queue.put((R) get.call());
            } catch (Exception e) {
                Timber.e("Could not perform successfully DB operation");
            }
        });

        try {
            return queue.take();
        } catch (InterruptedException e) {
            Timber.e("Could not retrieve result from DB operation");
            throw new RuntimeException(e);
        }
    }
}
