package com.kince.saundprogressbar.demo;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kince.saundprogressbar.SaundProgressBar;

public class MainActivity extends AppCompatActivity {

    private SaundProgressBar mSaundProgressBar;
    private int progress = 0;
    private Message message;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int p = msg.what;
            mSaundProgressBar.setProgress(p);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSaundProgressBar = this.findViewById(R.id.regularprogressbar);
        mSaundProgressBar.setMax(100);

        Drawable indicator = getResources().getDrawable(R.drawable.progress_indicator);
        Rect bounds = new Rect(0, 0, indicator.getIntrinsicWidth() + 5, indicator.getIntrinsicHeight());
        indicator.setBounds(bounds);

        mSaundProgressBar.setProgressIndicator(indicator);
        mSaundProgressBar.setProgress(0);
        mSaundProgressBar.setVisibility(View.VISIBLE);

        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            message = handler.obtainMessage();
            try {
                for (int i = 1; i <= 100; i++) {
                    int x = progress++;
                    message.what = x;
                    handler.sendEmptyMessage(message.what);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

}
