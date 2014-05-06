package com.kince.saundprogressbar;

import com.kince.saundprogressbar.widget.SaundProgressBar;
import android.view.View;
import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MainActivity extends Activity {

	private SaundProgressBar mPbar;
    private int progress=0;
    private Message message;
    private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int p=msg.what;
			mPbar.setProgress(p);
		}
    	
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPbar = (SaundProgressBar) this.findViewById(R.id.regularprogressbar);
		mPbar.setMax(100);
		
		Drawable indicator = getResources().getDrawable(
				R.drawable.progress_indicator);
		Rect bounds = new Rect(0, 0, indicator.getIntrinsicWidth() + 5,
				indicator.getIntrinsicHeight());
		indicator.setBounds(bounds);

		mPbar.setProgressIndicator(indicator);
		mPbar.setProgress(0);
		mPbar.setVisibility(View.VISIBLE);
		
		new Thread(runnable).start();
	}

	Runnable runnable=new Runnable() {
		
		@Override
		public void run() {
			message=handler.obtainMessage();
			// TODO Auto-generated method stub
			try {
				for (int i = 1; i <= 100; i++) {
					int x=progress++;
					message.what=x;
					handler.sendEmptyMessage(message.what);
					Thread.sleep(1000);
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}
