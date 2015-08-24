package com.yunyan.toybricks.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.yunyan.toybricks.ToyBricksApplication;
import com.yunyan.toybricks.utils.CommonUtil;
import com.yunyan.toybricks.utils.Constant;
import com.yunyan.toybricks.utils.MemoryUtils;
import com.yunyan.toybricks.utils.NetUtils;
import com.yunyan.toybricks.utils.NetworkUtil;
import com.yunyan.toybricks.utils.ResourcesUtils;
import com.yunyan.toybricks.utils.ThreadPoolManager;
import com.yunyan.toybricks.utils.XmlDB;
import com.yunyan.toybricks.vo.RequestVo;

public abstract class ToyBricksBaseActivity extends Activity implements OnClickListener{

    private ThreadPoolManager threadPoolManager;
    protected ProgressDialog progressDialog;
    private ToyBricksApplication application;
    protected Context context;

    public ToyBricksBaseActivity() {
        threadPoolManager = ThreadPoolManager.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        application=(ToyBricksApplication) getApplication();
        application.addActvity(this);
        context = getApplicationContext();
        initView();
    }
    

    private void initView(){
        loadViewLayout();
        findViewById();
        setListener();
        processLogic();
        isSDAvailableSize();
    }

    protected abstract void loadViewLayout();

    protected abstract void findViewById();

    protected abstract void setListener();
    /**
     * 向后台请求数据
     */
    protected abstract void processLogic();

    public abstract interface DataCallback<T> {
        public abstract void processData(T paramObject, boolean paramBoolean);
    }

    /**
     * 显示提示框
     */
    protected void showProgressDialog() {
        if ((!isFinishing()) && (this.progressDialog == null)) {
            this.progressDialog = new ProgressDialog(this);
        }
        this.progressDialog.setTitle(getString(ResourcesUtils.getString(context, "loadTitle")));
        this.progressDialog.setMessage(getString(ResourcesUtils.getString(context, "LoadContent")));
        this.progressDialog.show();
    }

    /**
     * 关闭提示框
     */
    protected void closeProgressDialog() {
        if (this.progressDialog != null)
            this.progressDialog.dismiss();
    }

    protected void getDataFromServer(RequestVo reqVo, DataCallback callBack) {
        showProgressDialog();//打开加载对话框
        BaseHandler handler = new BaseHandler(this, callBack, reqVo);
        BaseTask taskThread = new BaseTask(this, reqVo, handler);
        this.threadPoolManager.addTask(taskThread);
    }
    public void isSDAvailableSize(){
    	if(MemoryUtils.isSDAvailableSize(context)){
    		XmlDB.getInstance(context).saveKey("sdcard", true);
    	}else{
    		XmlDB.getInstance(context).saveKey("sdcard", false);
    	}
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
            closeProgressDialog();
            if (msg.what == Constant.SUCCESS) {
                if (msg.obj == null) {
                    CommonUtil.showInfoDialog(context, getString(ResourcesUtils.getString(context, "net_error")));
                } else {
                    callBack.processData(msg.obj, true);
                }
            } else if (msg.what == Constant.NET_FAILED) {
                CommonUtil.showInfoDialog(context, getString(ResourcesUtils.getString(context, "net_error")));
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
                if (NetworkUtil.getConnectivityStatus(context)==1||NetworkUtil.getConnectivityStatus(context)==2) {
                    obj = NetUtils.get(reqVo);
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

    @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		if(XmlDB.getInstance(context).getKeyBooleanValue("go_stop", false)){
			XmlDB.getInstance(context).saveKey("ishoutai", true);
		}else{
			XmlDB.getInstance(context).saveKey("ishoutai", false);
		}
	}

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        application.removeActvity(this);
        context = null;
        threadPoolManager = null;
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        application = null;
    }
}
