package com.yunyan.toybricks.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

@SuppressWarnings("deprecation")
public class ToyBricksGallery extends Gallery {

	public ToyBricksGallery(Context context) {
		super(context);
	}

	public ToyBricksGallery(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public ToyBricksGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return super.onFling(e1, e2, velocityX, velocityY);
	}
}
