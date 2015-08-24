package com.yunyan.toybricks.view;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.yunyan.toybricks.bean.QRResult;
import com.yunyan.toybricks.parser.QRResultParser;
import com.yunyan.toybricks.utils.AESUtils;
import com.yunyan.toybricks.utils.Constant;
import com.yunyan.toybricks.utils.DeviceUtils;
import com.yunyan.toybricks.utils.FileUtils;
import com.yunyan.toybricks.utils.MD5;
import com.yunyan.toybricks.utils.NetUtils;
import com.yunyan.toybricks.utils.NetworkUtil;
import com.yunyan.toybricks.utils.ResourcesUtils;
import com.yunyan.toybricks.utils.ThreadPoolManager;
import com.yunyan.toybricks.utils.XmlDB;
import com.yunyan.toybricks.vo.RequestVo;
import com.zxing.android.Intents;
import com.zxing.android.MessageIDs;
import com.zxing.android.camera.CameraManager;
import com.zxing.android.decoding.CaptureActivityHandler;
import com.zxing.android.decoding.InactivityTimer;
import com.zxing.android.view.ViewfinderView;

/**
 * Created by George on 2015/4/20.
 */
public class ToyBricksQRActivity extends Activity implements Callback {
	
    private ThreadPoolManager threadPoolManager;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private SurfaceView surfaceView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;
    private CameraManager cameraManager;
    private Context context;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context=getApplicationContext();
        initView();
        threadPoolManager = ThreadPoolManager.getInstance();
        PushAgent.getInstance(getApplicationContext()).onAppStart();//应用统计
    }
    private void initView(){
        loadViewLayout();
        findViewById();
    }


	protected void loadViewLayout() {
        setContentView(ResourcesUtils.getLayout(getApplicationContext(), "toybricks_activity_capture"));
    }


    protected void findViewById() {
        surfaceView = (SurfaceView) findViewById(ResourcesUtils.getId(getApplicationContext(), "surfaceview"));

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.
        cameraManager = new CameraManager(getApplication().getApplicationContext());
        viewfinderView = (ViewfinderView) findViewById(ResourcesUtils.getId(getApplicationContext(), "viewfinderview"));
        viewfinderView.setCameraManager(cameraManager);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
        Intent intent = getIntent();
        if(intent!=null){
        	if(intent.hasExtra(Intents.Scan.CAMERA_ID)){
                int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1);
                if (cameraId >= 0) {
                  cameraManager.setManualCameraId(cameraId);
                }
        	}
        }
        XmlDB.getInstance(getApplicationContext()).saveKey("go_stop", true);
		MobclickAgent.onResume(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
		MobclickAgent.onPause(getApplicationContext());;
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            cameraManager.openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            try {
                AssetFileDescriptor fileDescriptor = getAssets().openFd("qrbeep.ogg");
                this.mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),fileDescriptor.getLength());
                this.mediaPlayer.setVolume(0.1F, 0.1F);
                this.mediaPlayer.prepare();
            } catch (IOException e) {
                this.mediaPlayer = null;
            }
        }
    }
    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        @Override
		public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }
    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        showResult(obj, barcode);
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(MessageIDs.restart_preview, delayMS);
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }
    /**
     *
     * @param rawResult
     * @param barcode
     */
    private void showResult(Result rawResult, Bitmap barcode) {
    	//TODO 将扫描结果发送给Unity
        HashMap<String, String> requestDataMap = new HashMap<String, String>();
        requestDataMap.put("erweima",rawResult.getText().toString());
		if(!TextUtils.isEmpty(DeviceUtils.getDeviceInfo(getApplicationContext()))){
			requestDataMap.put("mac", DeviceUtils.getDeviceInfo(getApplicationContext()));
		}
        requestDataMap.put("banben", getIntent().getStringExtra("ToybricksName"));
        //TODO 发送二维码校验请求
        QRResultParser resultParser=new QRResultParser();
        RequestVo vo = new RequestVo(ResourcesUtils.getString(getApplicationContext(), "toybricks_app_qrcheck"), this, requestDataMap, resultParser);
        getDataFromServer(vo,new DataCallback<QRResult>() {

			@Override
			public void processData(QRResult paramObject, boolean paramBoolean) {
				try {
					// TODO Auto-generated method stub
					if(!paramObject.getCishu().equals("0") && paramObject.getCishu()!=null && Integer.parseInt(paramObject.getCishu().trim())>0){
						//保存注册信息
						if(!TextUtils.isEmpty(DeviceUtils.getDeviceInfo(getApplicationContext()))){
							Toast.makeText(ToyBricksQRActivity.this,"验证成功，剩余可激活设备"+paramObject.getCishu()+"台",Toast.LENGTH_LONG).show();
							String temp=DeviceUtils.getDeviceInfo(getApplicationContext())+paramObject.getBanben()+paramObject.getCishu()+paramObject.getShijian()+paramObject.getZhuangtai();
							FileUtils.writeFile(AESUtils.encrypt(DeviceUtils.getDeviceInfo(getApplicationContext()), temp), new File(Constant.TOYBRICKS_SEAPATH,MD5.digest(getIntent().getStringExtra("ToybricksName"))));
							MobclickAgent.onEvent(context, "DeviceInfo : "+DeviceUtils.getDeviceInfo(context));
							XmlDB.getInstance(context).saveKey("AES", true);
							XmlDB.getInstance(context).saveKey(getIntent().getStringExtra("Displayname")+"_checkQr", true);
							//发送验证结果给Unity
							setResult(RESULT_OK);
							finish();
						}else{
							XmlDB.getInstance(context).saveKey(getIntent().getStringExtra("Displayname")+"_checkQr", false);
	    					Toast.makeText(context,"验证异常，问问客服", Toast.LENGTH_LONG).show();
	    					MobclickAgent.onEvent(context, "DeviceInfo not Found ");
	    					finish();
	                    }

					}else if(paramObject.getZhuangtai().equals("Error_over")){
						XmlDB.getInstance(context).saveKey(getIntent().getStringExtra("Displayname")+"_checkQr", false);
						Toast.makeText(context, "二维码注册设备数已超限,验证失败，详询客服", Toast.LENGTH_LONG).show();
						MobclickAgent.onEvent(context, "Error :"+paramObject.getZhuangtai());
						setResult(RESULT_CANCELED);
						finish();
					}else if(paramObject.getZhuangtai().equals("Error_banben")){
						XmlDB.getInstance(context).saveKey(getIntent().getStringExtra("Displayname")+"_checkQr", false);
						Toast.makeText(context, "该二维码不对哦，核对一下哈，详询客服", Toast.LENGTH_LONG).show();
						MobclickAgent.onEvent(context, "Error :"+paramObject.getZhuangtai());
						setResult(RESULT_CANCELED);
						finish();
					}else if(paramObject.getZhuangtai().equals("Error_erweima")){
						XmlDB.getInstance(context).saveKey(getIntent().getStringExtra("Displayname")+"_checkQr", false);
						Toast.makeText(context, "咦，这不是我们的二维码，详询客服", Toast.LENGTH_LONG).show();
						MobclickAgent.onEvent(context, "Error :"+paramObject.getZhuangtai());
						setResult(RESULT_CANCELED);
						finish();
					}
				} catch (NumberFormatException e) {
					Toast.makeText(context,"验证异常，问问客服,异常编号1",Toast.LENGTH_SHORT).show();
					MobclickAgent.onEvent(context, "Exception :"+e.getMessage());
					// TODO: handle exception
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Toast.makeText(context,"验证异常，问问客服,异常编号2",Toast.LENGTH_SHORT).show();
					MobclickAgent.onEvent(context, "Exception :"+e.getMessage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(context,"验证异常，问问客服,异常编号3",Toast.LENGTH_SHORT).show();
					MobclickAgent.onEvent(context, "Exception :"+e.getMessage());
				}

			}
		});
    	
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	Toast.makeText(context, "二维码验证取消", Toast.LENGTH_LONG).show();
        	//MessageIDs.camera_open_flag=false;
            setResult(RESULT_CANCELED);
            finish();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
            return true;
        }else if(keyCode==KeyEvent.KEYCODE_HOME){
        	finish();
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    
    public abstract interface DataCallback<T> {
        public abstract void processData(T paramObject, boolean paramBoolean);
    }
    protected void getDataFromServer(RequestVo reqVo, DataCallback callBack) {
        //showProgressDialog();//打开加载对话框
        BaseHandler handler = new BaseHandler(this, callBack, reqVo);
        BaseTask taskThread = new BaseTask(this, reqVo, handler);
        this.threadPoolManager.addTask(taskThread);
    }

    class BaseHandler extends Handler {
        private Context context;
        private DataCallback callBack;
        private RequestVo reqVo;

        public BaseHandler(Context context, DataCallback callBack, RequestVo reqVo) {
            this.context = context;
            this.callBack = callBack;
            this.reqVo = reqVo;
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO
            //关闭加载对话框
           // closeProgressDialog();
            if (msg.what == Constant.SUCCESS) {
                if (msg.obj == null) {
                	Toast.makeText(context, "请求异常,没有得到数据,验证失败，详询客服", Toast.LENGTH_LONG).show();
                	XmlDB.getInstance(getApplicationContext()).saveKey(getIntent().getStringExtra("toybricksName")+"_Q",false);
                	ToyBricksQRActivity.this.setResult(RESULT_CANCELED);
                	ToyBricksQRActivity.this.finish();
                } else {
                    callBack.processData(msg.obj, true);
                }
            } else if (msg.what == Constant.NET_FAILED) {
            	Toast.makeText(context, "网络异常,请先连接Internet！再试", Toast.LENGTH_LONG).show();
            	XmlDB.getInstance(getApplicationContext()).saveKey(getIntent().getStringExtra("toybricksName")+"_Q",false);
            	ToyBricksQRActivity.this.setResult(RESULT_CANCELED);
            	ToyBricksQRActivity.this.finish();
            }
           
        }

    }

    class BaseTask implements Runnable {
        private Context context;
        private RequestVo reqVo;
        private Handler handler;

        public BaseTask(Context context, RequestVo reqVo, Handler handler) {
            this.context = context;
            this.reqVo = reqVo;
            this.handler = handler;
        }

        @Override
        public void run(){
            Object obj = null;
            Message msg = Message.obtain();
            try {
                if (NetworkUtil.getConnectivityStatus(getApplicationContext())==1||NetworkUtil.getConnectivityStatus(getApplicationContext())==2) {
                    obj = NetUtils.post(reqVo);
                    msg.what = Constant.SUCCESS;
                    msg.obj = obj;
                    handler.sendMessage(msg);
                } else {
                    msg.what = Constant.NET_FAILED;
                    msg.obj = obj;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
