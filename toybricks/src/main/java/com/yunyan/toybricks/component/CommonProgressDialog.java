package com.yunyan.toybricks.component;

import java.text.NumberFormat;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yunyan.toybricks.utils.Constant;
import com.yunyan.toybricks.utils.ResourcesUtils;

public class CommonProgressDialog extends AlertDialog {

	private ProgressBar toybricks_progress_dialog_pg_progressbar;
	private TextView toybricks_progress_dialog_tv_title;
	private TextView toybricks_progress_dialog_tv_speed;
	private TextView toybricks_progress_dialog_tv_progress;

	private Handler mViewUpdateHandler;
	private int mMax;
	private CharSequence mMessage;
	private boolean mHasStarted;
	private int mProgressVal;
	private int speed;
	private String TAG = "CommonProgressDialog";
	private String mProgressNumberFormat;
	private NumberFormat mProgressPercentFormat;
	private Context context;
	public CommonProgressDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		initFormats();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(ResourcesUtils.getLayout(context, "toybricks_popwindow_progress"));
		toybricks_progress_dialog_pg_progressbar = (ProgressBar) findViewById(ResourcesUtils.getId(context, "toybricks_progress_dialog_pg_progressbar"));
		toybricks_progress_dialog_tv_title = (TextView) findViewById(ResourcesUtils.getId(context, "toybricks_progress_dialog_tv_title"));
		toybricks_progress_dialog_tv_speed = (TextView) findViewById(ResourcesUtils.getId(context, "toybricks_progress_dialog_tv_speed"));
		toybricks_progress_dialog_tv_progress = (TextView) findViewById(ResourcesUtils.getId(context, "toybricks_progress_dialog_tv_progress"));
		mViewUpdateHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					int progress = toybricks_progress_dialog_pg_progressbar.getProgress();
					toybricks_progress_dialog_tv_speed.setText(getSpeed()+"kb/s");
					int max = toybricks_progress_dialog_pg_progressbar.getMax();
			        double dProgress = (double)progress/(double)(1024 * 1024);
			        double dMax = (double)max/(double)(1024 * 1024);
					if (mProgressNumberFormat != null) {
						String format = mProgressNumberFormat;
						toybricks_progress_dialog_tv_progress.setText(String.format(format, dProgress,dMax));
					} else {
						toybricks_progress_dialog_tv_progress.setText("");
					}
					break;

				}

			}

		};
		onProgressChanged();
		if (mMessage != null) {
			setMessage(mMessage);
		}
		if (mMax > 0) {
			setMax(mMax);
		}
		if (mProgressVal > 0) {
			setProgress(mProgressVal);
		}
	}

	private void initFormats() {
		mProgressNumberFormat = "%1.2fM/%2.2fM";
		mProgressPercentFormat = NumberFormat.getPercentInstance();
		mProgressPercentFormat.setMaximumFractionDigits(0);
	}

	private void onProgressChanged() {
		mViewUpdateHandler.sendEmptyMessage(0);

	}

	public void setProgressStyle(int style) {
		// mProgressStyle = style;
	}
	public int getSpeed(){
		return speed;
	}
	public void setSpeed(int speed){
		if(Constant.NET_CHANGE){
			toybricks_progress_dialog_tv_speed.setText(speed+"kb/s");
		}else{
			this.speed=speed;	
		}

	}
	public void setTitle(String tilte){
		toybricks_progress_dialog_tv_title.setText(tilte);
	}
	public int getMax() {
		if (toybricks_progress_dialog_pg_progressbar != null) {
			return toybricks_progress_dialog_pg_progressbar.getMax();
		}
		return mMax;
	}

	public void setMax(int max) {
		if (toybricks_progress_dialog_pg_progressbar != null) {
			toybricks_progress_dialog_pg_progressbar.setMax(max);
			onProgressChanged();
		} else {
			mMax = max;
		}
	}

	public void setIndeterminate(boolean indeterminate) {
		if (toybricks_progress_dialog_pg_progressbar != null) {
			toybricks_progress_dialog_pg_progressbar.setIndeterminate(indeterminate);
		}
	}

	public void setProgress(int value) {
		if (mHasStarted) {
			toybricks_progress_dialog_pg_progressbar.setProgress(value);
			onProgressChanged();
		} else {
			mProgressVal = value;
		}
	}

	@Override
	public void setMessage(CharSequence message) {
		if (toybricks_progress_dialog_tv_title != null) {
			toybricks_progress_dialog_tv_title.setText(message);
		} else {
			mMessage = message;
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mHasStarted = true;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mHasStarted = false;
	}

}