package com.yunyan.toybricks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.umeng.common.message.Log;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengDownloadResourceService;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.message.proguard.i;
import com.yunyan.toybricks.utils.ResourcesUtils;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

public class ToyBricksApplication extends Application{
    /** 缓存路径 */
    private static String cacheDir;
    private List<Activity> records = new ArrayList<Activity>();
    
	private static final String TAG = ToyBricksApplication.class.getName();
	private PushAgent mPushAgent;
	private static int a = 64;
	
	@Override
	public void onCreate() {
		
		initCacheDirPath();
		initDataDirPath();
		initAdsDirPath();
		mPushAgent = PushAgent.getInstance(this);
		mPushAgent.setDebugMode(false);
		/**
		 * 该Handler是在IntentService中被调用，故 1.
		 * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK 2.
		 * IntentService里的onHandleIntent方法是并不处于主线程中，因此，如果需调用到主线程，需如下所示;
		 * 或者可以直接启动Service
		 * */
		UmengMessageHandler messageHandler = new UmengMessageHandler() {
			@Override
			public void dealWithCustomMessage(final Context context,final UMessage msg) {
				new Handler(getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						getNotification(context, msg);// 推出到任务栏
						UTrack.getInstance(context).trackMsgClick(msg);// 统计消息被点击或者处理
						UTrack.getInstance(context).trackMsgDismissed(msg);// 统计消息被忽略
					}
				});
			}

			@Override
			public Notification getNotification(Context context, UMessage msg) {
				switch (msg.builder_id) {
				case 1:
					NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
					RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), ResourcesUtils.getLayout(context, "notification_view"));
					myNotificationView.setTextViewText(ResourcesUtils.getId(context,"notification_title"), msg.title);
					myNotificationView.setTextViewText(ResourcesUtils.getId(context, "notification_text"),msg.text);
					myNotificationView.setImageViewBitmap(ResourcesUtils.getId(context, "notification_large_icon"),getLargeIcon(context, msg));
					myNotificationView.setImageViewResource(ResourcesUtils.getId(context, "notification_small_icon"),getSmallIconId(context, msg));
					builder.setContent(myNotificationView);
					Notification mNotification = builder.build();
					// 由于Android
					// v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
					mNotification.contentView = myNotificationView;
					return mNotification;
				default:
					// 默认为0，若填写的builder_id并不存在，也使用默认。
					return super.getNotification(context, msg);
				}
			}

			@Override
			public int getSmallIconId(Context context, UMessage umessage) {
				int i = -1;
				try {
					if (!TextUtils.isEmpty(umessage.icon))
						i = ResourcesUtils.getDrawable(context, umessage.icon);
					if (i < 0)
						i = ResourcesUtils.getDrawable(context,"umeng_push_notification_default_small_icon");
					if (i < 0) {
						i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.icon;
					}
					if (i < 0)
						Log.a(TAG,"Cann't find appropriate icon for notification, please make sure you have specified an icon for this notification or the app has defined an icon.");
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				return i;
			}

			@Override
			public Bitmap getLargeIcon(Context context, UMessage umessage) {
				Bitmap bitmap = null;
				try {
					if (umessage.isLargeIconFromInternet()) {
						String s = (new StringBuilder(String.valueOf(UmengDownloadResourceService.getMessageResourceFolder(context,umessage)))).append(umessage.img.hashCode()).toString();
						bitmap = BitmapFactory.decodeFile(s);
					}
					if (bitmap == null) {
						int i = -1;
						if (!TextUtils.isEmpty(umessage.largeIcon))
							i = ResourcesUtils.getDrawable(context,umessage.largeIcon);
						if (i < 0)
							i = ResourcesUtils.getDrawable(context,"umeng_push_notification_default_large_icon");
						if (i > 0)
							bitmap = BitmapFactory.decodeResource(context.getResources(), i);
					}
					if (bitmap != null) {
						int j;
						if (android.os.Build.VERSION.SDK_INT >= 11)
							j = (int) context.getResources().getDimension(17104902);
						else
							j = i.a(a);
						Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, j,j, true);
						return bitmap1;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				return null;

			}

			@Override
			public Uri getSound(Context context, UMessage umessage) {
				String s = null;
				try {
					if (umessage.isSoundFromInternet()) {
						s = (new StringBuilder(String.valueOf(UmengDownloadResourceService.getMessageResourceFolder(context,umessage)))).append(umessage.sound.hashCode()).toString();
						if (!(new File(s)).exists())
							s = null;
					}
					if (s == null) {
						int i = -1;
						if (!TextUtils.isEmpty(umessage.sound))
							i = ResourcesUtils.getRaw(context, umessage.sound);
						if (i < 0)
							i = ResourcesUtils.getRaw(context,"umeng_push_notification_default_sound");
						if (i > 0)
							s = (new StringBuilder("android.resource://")).append(context.getPackageName()).append("/").append(i).toString();
					}
					if (s != null) {
						Uri uri = Uri.parse(s);
						return uri;
					}
				} catch (Throwable throwable) {
				}
				return null;

			}

		};
		mPushAgent.setMessageHandler(messageHandler);

		/**
		 * 该Handler是在BroadcastReceiver中被调用，故
		 * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
		 * */
		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
			@Override
			public void dealWithCustomAction(Context context, UMessage msg) {
				
			}
		};
		
		mPushAgent.setNotificationClickHandler(notificationClickHandler);		

	}

    public static String getCacheDirPath() {
        return cacheDir;
    }

    private void initCacheDirPath() {
        File f;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        	f = new File(Environment.getExternalStorageDirectory() + "/.toybricks/");
            if (!f.exists()) {
                f.mkdir();
            }
        } else {
            f = getApplicationContext().getCacheDir();
        }
        cacheDir = f.getAbsolutePath();
    }
    private void initDataDirPath(){
        File f;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        	f = new File(Environment.getExternalStorageDirectory() + "/toybricks/image/");
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        
    }
    private void initAdsDirPath(){
    	this.getApplicationContext().getExternalCacheDir();
    	File f;
    	if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		f = new File(Environment.getExternalStorageDirectory() + "/.toybricks/ads/");
    		if (!f.exists()) {
    			f.mkdirs();
    		}
    	}
    }
    public void addActvity(Activity activity) {
        records.add(activity);
    }

    public void removeActvity(Activity activity) {
        records.remove(activity);
    }

    public void exit() {
        for (Activity activity : records) {
            activity.finish();
        }
    }

    public int getCurrentActivitySize() {
        return records.size();
    }
}
