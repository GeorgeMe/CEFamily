package com.yunyan.toybricks.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.unity3d.player.UnityPlayer;
import com.yunyan.toybricks.utils.Constant;
import com.yunyan.toybricks.utils.DateUtils;
import com.yunyan.toybricks.utils.ResourcesUtils;
import com.yunyan.toybricks.utils.UnityGetAndroidUI;
import com.yunyan.toybricks.utils.XmlDB;

public class ToyBricksShareActivity extends ToyBricksBaseActivity {

	private ImageView toybricks_screenshot, toybricks_back, toybricks_save, toybricks_share,toybricks_refresh;
	private UMSocialService mController = UMServiceFactory.getUMSocialService(Constant.DESCRIPTOR);
	private Bitmap bitmap;
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1320:
				bitmap=BitmapFactory.decodeFile(XmlDB.getInstance(context).getKeyString("path", context.getFilesDir().getAbsolutePath()+"/toybricks.png"));
				if(bitmap==null){
					Toast.makeText(context, "哎！截图加载失败,刷新一下看看", Toast.LENGTH_LONG).show();
					toybricks_refresh.setVisibility(View.VISIBLE);
					toybricks_screenshot.setImageResource(ResourcesUtils.getDrawable(context, "toybricks_app_icon"));
				}else{
					toybricks_screenshot.setImageBitmap(bitmap);	
				}
				break;

			default:
				break;
			}
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(context).onAppStart();//应用统计
	}

	@Override
	protected void loadViewLayout() {
		// TODO Auto-generated method stub
		setContentView(ResourcesUtils.getLayout(context, "toybricks_activity_share"));
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		toybricks_screenshot = (ImageView) this.findViewById(ResourcesUtils.getId(context, "toybricks_screenshot"));
		toybricks_back = (ImageView) this.findViewById(ResourcesUtils.getId(context, "toybricks_back"));
		toybricks_save = (ImageView) this.findViewById(ResourcesUtils.getId(context, "toybricks_save"));
		toybricks_share = (ImageView) this.findViewById(ResourcesUtils.getId(context, "toybricks_share"));
		toybricks_refresh=(ImageView)this.findViewById(ResourcesUtils.getId(context, "toybricks_refresh"));
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		toybricks_back.setOnClickListener(this);
		toybricks_save.setOnClickListener(this);
		toybricks_share.setOnClickListener(this);
		toybricks_screenshot.setOnClickListener(this);
		toybricks_refresh.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		if (view == toybricks_back) {
			if(UnityPlayer.currentActivity!=null){
				UnityPlayer.UnitySendMessage("Camera", "setbuttonon", "On");
				UnityPlayer.UnitySendMessage("Camera", "showSaveSucces", "success");	
			}else{
				UnityGetAndroidUI.instance().UnitySendMessage("Camera", "setbuttonon", "On");
				UnityGetAndroidUI.instance().UnitySendMessage("Camera", "showSaveSucces", "success");
			}

			File file=new File(XmlDB.getInstance(context).getKeyString("path", context.getFilesDir().getAbsolutePath()+"/toybricks.png"));
			if(file.exists()){
				file.delete();
				file.deleteOnExit();
			}
			finish();
			MobclickAgent.onEvent(context, "toybricks_back");	
		} else if (view == toybricks_save) {
			try {
			if(bitmap==null){
				Toast.makeText(context, "亲,截图没有找到哦,问问客服", Toast.LENGTH_LONG).show();
				if(UnityPlayer.currentActivity!=null){
					UnityPlayer.UnitySendMessage("Camera", "setbuttonon", "On");
					UnityPlayer.UnitySendMessage("Camera", "showSaveSucces", "success");	
				}else{
					UnityGetAndroidUI.instance().UnitySendMessage("Camera", "setbuttonon", "On");
					UnityGetAndroidUI.instance().UnitySendMessage("Camera", "showSaveSucces", "success");
				}

				finish();
			}else{
				saveMyBitmap(bitmap);
			}
			} catch (NullPointerException e) {
				// TODO: handle exception
				Toast.makeText(context, "亲,截图没有找到哦,问问客服", Toast.LENGTH_LONG).show();
			}
			MobclickAgent.onEvent(context, "toybricks_save");	
		} else if (view == toybricks_share) {
			try {
				if(bitmap==null){
					bitmap=BitmapFactory.decodeResource(getResources(), ResourcesUtils.getDrawable(context, "toybricks_app_icon"));
					ToyBricksSharePopupWindow shareBoard = new ToyBricksSharePopupWindow(ToyBricksShareActivity.this,bitmap);
					shareBoard.showAtLocation(ToyBricksShareActivity.this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
				}else{
					ToyBricksSharePopupWindow shareBoard = new ToyBricksSharePopupWindow(ToyBricksShareActivity.this,bitmap);
					shareBoard.showAtLocation(ToyBricksShareActivity.this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
				}
			} catch (NullPointerException e) {
				// TODO: handle exception
				Toast.makeText(context, "亲,截图没有找到哦,问问客服", Toast.LENGTH_LONG).show();
			}
			MobclickAgent.onEvent(context, "toybricks_share");	
		}else if(view==toybricks_refresh){
			toybricks_refresh.setVisibility(View.GONE);
			bitmap=BitmapFactory.decodeFile(XmlDB.getInstance(context).getKeyString("path", context.getFilesDir().getAbsolutePath()+"/toybricks.png"));
			if(bitmap==null){
				Toast.makeText(context, "哎！截图不不成功,问问客服", Toast.LENGTH_LONG).show();
				toybricks_screenshot.setImageResource(ResourcesUtils.getDrawable(context, "toybricks_app_icon"));
			}else{
				toybricks_screenshot.setImageBitmap(bitmap);	
			}
		}else if(view==toybricks_screenshot){
			toybricks_refresh.setVisibility(View.GONE);
			bitmap=BitmapFactory.decodeFile(XmlDB.getInstance(context).getKeyString("path", context.getFilesDir().getAbsolutePath()+"/toybricks.png"));
			if(bitmap==null){
				Toast.makeText(context, "哎！截图不不成功,问问客服", Toast.LENGTH_LONG).show();
				toybricks_screenshot.setImageResource(ResourcesUtils.getDrawable(context, "toybricks_app_icon"));
			}else{
				toybricks_screenshot.setImageBitmap(bitmap);	
			}
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK&& event.getAction() == KeyEvent.ACTION_DOWN){
			if(UnityPlayer.currentActivity!=null){
				UnityPlayer.UnitySendMessage("Camera", "setbuttonon", "On");
				UnityPlayer.UnitySendMessage("Camera", "showSaveSucces", "success");	
			}else{
				UnityGetAndroidUI.instance().UnitySendMessage("Camera", "setbuttonon", "On");
				UnityGetAndroidUI.instance().UnitySendMessage("Camera", "showSaveSucces", "success");
			}
			File file=new File(XmlDB.getInstance(context).getKeyString("path", context.getFilesDir().getAbsolutePath()+"/toybricks.png"));
			if(file.exists()){
				file.delete();
				file.deleteOnExit();
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(UnityPlayer.currentActivity!=null){
			UnityPlayer.UnitySendMessage("Camera", "setbuttonon", "On");
			UnityPlayer.UnitySendMessage("Camera", "showSaveSucces", "success");	
		}else{
			UnityGetAndroidUI.instance().UnitySendMessage("Camera", "setbuttonon", "On");
			UnityGetAndroidUI.instance().UnitySendMessage("Camera", "showSaveSucces", "success");
		}

	}

	@Override
	public void onResume() {
		super.onResume();
        XmlDB.getInstance(context).saveKey("go_stop", false);
		MobclickAgent.onResume(context);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(bitmap!=null){
			bitmap=null;
		}
		if(progressDialog!=null){
			progressDialog=null;
		}
		toybricks_screenshot=null; 
		toybricks_back=null; 
		toybricks_save=null; 
		toybricks_share=null;
	}

	@Override
	protected void processLogic() {
			configPlatforms();
			try {
				Thread.sleep(500);
				toybricks_refresh.setVisibility(View.GONE);
				handler.sendEmptyMessage(1320);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	/**
	 * 配置分享平台参数
	 */
	private void configPlatforms() {
		// 添加新浪SSO授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加腾讯微博SSO授权
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		// 添加QQ、QZone平台
		addQQQZonePlatform();
		// 添加微信、微信朋友圈平台
		addWXPlatform();
	}

	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	private void addWXPlatform() {
		// 注意：在微信授权的时候，必须传递appSecret
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = "wxa04e922933444a98";
		String appSecret = "2a5e6c3f9689781adb4d60d43474eebc";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(ToyBricksShareActivity.this, appId,appSecret);
		wxHandler.addToSocialSDK();

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(ToyBricksShareActivity.this,appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	/**
	 * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
	 *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
	 *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
	 *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
	 * @return
	 */
	private void addQQQZonePlatform() {
		String appId = "1104667819";
		String appKey = "3Q3BkSuoDBt8rQx1";
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(ToyBricksShareActivity.this,appId, appKey);
		qqSsoHandler.addToSocialSDK();

		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(ToyBricksShareActivity.this, appId, appKey);
		qZoneSsoHandler.addToSocialSDK();
	}
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**使用SSO授权必须添加如下代码 */
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
	
	public void saveMyBitmap( Bitmap mBitmap) {
		String temp ="";
		try {
				File f = new File(Constant.TOYBRICKS_PATH +"/image/TOYBRICKS_"+DateUtils.format24TimePNG(DateUtils.getCurrentTime())+".png");
				temp=f.getAbsolutePath();
				FileOutputStream fOut = null;
				f.createNewFile();
				fOut = new FileOutputStream(f);
				mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
				fOut.flush();
				fOut.close();
				//LogUtils.e("本地：", temp);
		} catch(OutOfMemoryError o){
			Toast.makeText(context, "发生OOM,问问客服"+o.getMessage(), Toast.LENGTH_LONG).show();
		}catch (FileNotFoundException e) {
			Toast.makeText(context, "在保存图片时出错"+e.getMessage(), Toast.LENGTH_LONG).show();
		} catch (IOException i) {
			Toast.makeText(context, "在保存图片时出错"+i.getMessage(), Toast.LENGTH_LONG).show();
		}finally{
			if(temp!=null){
				fileScan(temp);
				temp=null;
			}
			if(UnityPlayer.currentActivity!=null){
				UnityPlayer.UnitySendMessage("Camera", "setbuttonon", "On");
				UnityPlayer.UnitySendMessage("Camera", "showSaveSucces", "success");	
			}else{
				UnityGetAndroidUI.instance().UnitySendMessage("Camera", "setbuttonon", "On");
				UnityGetAndroidUI.instance().UnitySendMessage("Camera", "showSaveSucces", "success");
			}
			File file=new File(XmlDB.getInstance(context).getKeyString("path", context.getFilesDir().getAbsolutePath()+"/toybricks.png"));
			if(file.exists()){
				file.delete();
				file.deleteOnExit();
			}
			finish();
		}
	}
	
    /**
     * 
     * @author ZoZo
     * fileScan()的功能描述：
     * 单个文件更新到媒体库
     * @param filepath 文件全路径(包括到文件后缀名)
     */
	private void fileScan(String filepath){
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filepath)));
    }
	
	
}
