package org.dwallach.xstopwatch;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.Observable;
import java.util.Observer;

public class StopwatchActivity extends Activity implements Observer {
    private static final String TAG = "StopwatchActivity";

    private StopwatchState stopwatchState = StopwatchState.getSingleton();
    private ImageButton resetButton;
    private ImageButton playButton;
    private StopwatchNotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate");
        setContentView(R.layout.activity_stopwatch);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                Log.v(TAG, "onLayoutInflated");
                resetButton = (ImageButton) stub.findViewById(R.id.resetButton);
                playButton = (ImageButton) stub.findViewById(R.id.playButton);

                // bring in saved preferences
                PreferencesHelper.loadPreferences(StopwatchActivity.this);

                // set up notification helper
                notificationHelper = new StopwatchNotificationHelper(StopwatchActivity.this,
                        R.drawable.stopwatch_ic_launcher,
                        getResources().getString(R.string.stopwatch_app_name));

                stopwatchState.addObserver(notificationHelper);
                stopwatchState.addObserver(StopwatchActivity.this);

                resetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopwatchState.reset();
                    }
                });

                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopwatchState.click();
                    }
                });
            }
        });
    }

    @Override
    public void update(Observable observable, Object data) {
        if(playButton != null) {
            if (stopwatchState.isRunning())
                playButton.setImageResource(android.R.drawable.ic_media_pause);
            else
                playButton.setImageResource(android.R.drawable.ic_media_play);

            PreferencesHelper.savePreferences(StopwatchActivity.this);
        }
    }
}
