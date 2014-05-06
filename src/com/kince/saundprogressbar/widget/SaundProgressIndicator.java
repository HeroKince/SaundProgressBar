package com.kince.saundprogressbar.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class SaundProgressIndicator extends TextView {

	public SaundProgressIndicator(Context context) {
		this(context, null);
	}

	public SaundProgressIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SaundProgressIndicator(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
}
