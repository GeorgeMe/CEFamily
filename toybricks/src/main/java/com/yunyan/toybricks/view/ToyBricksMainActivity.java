package com.yunyan.toybricks.view;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.unity3d.player.UnityPlayer;
import com.yunyan.toybricks.ToyBricksApplication;
import com.yunyan.toybricks.adapter.ToyBricksAdvertisementAdapter;
import com.yunyan.toybricks.adapter.ToyBricksProductAdapter;
import com.yunyan.toybricks.bean.SD;
import com.yunyan.toybricks.bean.ToyBricks;
import com.yunyan.toybricks.parser.ToyBricksParser;
import com.yunyan.toybricks.utils.CommonUtil;
import com.yunyan.toybricks.utils.Constant;
import com.yunyan.toybricks.utils.DateUtils;
import com.yunyan.toybricks.utils.DeviceUtils;
import com.yunyan.toybricks.utils.FileUtils;
import com.yunyan.toybricks.utils.MemoryUtils;
import com.yunyan.toybricks.utils.NetworkChangeReceiver;
import com.yunyan.toybricks.utils.NetworkUtil;
import com.yunyan.toybricks.utils.ResourcesUtils;
import com.yunyan.toybricks.utils.UnityGetAndroidUI;
import com.yunyan.toybricks.utils.XmlDB;
import com.yunyan.toybricks.vo.RequestVo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @ClassName MainActivity 
 * @Author    ZoZo
 * @Des       MainActivity这个类的描述：
 *
 * @Date      2015年1月17日 下午3:46:04 
 * @Since     JDK 1.7
 * @Version
 */
@SuppressWarnings("deprecation")
public class ToyBricksMainActivity extends ToyBricksBaseActivity implements AdapterView.OnItemSelectedListener{
	
    private ImageView toybricks_topbar_setting,toybricks_topbar_play;
    private SwipeMenuListView mToyBricksListView;
    private Gallery mGallery;
    private List<ImageView> mSlideViews;
    private boolean isPlay;
    private ToyBricksAdvertisementAdapter mToyBricksAdvertisementAdapter;
    private ToyBricksProductAdapter mToyBricksProductAdapter;
    private long exitTime = 0;
    private ToyBricksApplication application;

	private PushAgent mPushAgent;
	
	private NetworkChangeReceiver myReceiver;
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if  (!isPlay)
                return ;
            mGallery.setSelection(mGallery.getSelectedItemPosition() + 1);
            handler.postDelayed(this, 4000);
            //Logger.d(TAG, "下一张");
        }
    };

    public Handler handler = new Handler();

	public void sendMsgToMe(){
		SD sd=new SD();
		sd.setDeviceName(DeviceUtils.getDeviceName());
		sd.setDeviceSDK(""+DeviceUtils.getDeviceSDK());
		sd.setsDAvailableSize(MemoryUtils.getSDAvailableSize(context));
		sd.setReportTime(DateUtils.format24Time(DateUtils.getCurrentTime()));
		Gson gson=new Gson();
		String temp=gson.toJson(sd);
		MobclickAgent.reportError(context, temp);
	}	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSwipeMenuListView();
		application=new ToyBricksApplication();
		mPushAgent = PushAgent.getInstance(context);
		mPushAgent.enable();
		// 消息推送
		mPushAgent.onAppStart();		
		//统计
		MobclickAgent.updateOnlineConfig(context);
		registerReceiver();
	}

	private void initSwipeMenuListView() {
		// 左滑删除菜单
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(context);
				// set item background255,215,0  #ffd700
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0x8b,0x45, 0x13)));
				// set item width
				deleteItem.setWidth(dp2px(90));//
				// set a icon
				deleteItem.setIcon(ResourcesUtils.getDrawable(context, "toybricks_swipemenu_delete"));
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};

		// set creator
		mToyBricksListView.setMenuCreator(creator);

		// step 2. listener item click event
		mToyBricksListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(int position,SwipeMenu menu, int index) {
						switch (index) {
						case 0:
							File file = new File(Constant.TOYBRICKS_PATH+ mToyBricksProductAdapter.getItem(position).getDisplayname());
							if (file.exists()) {
								deleteFiles(position);
							} else {
								Toast.makeText(context, "The file does not exist",Toast.LENGTH_SHORT).show();
								mToyBricksProductAdapter.notifyDataSetChanged();
							}
							break;
						}
						return false;
					}
				});

		// set SwipeListener
		mToyBricksListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

					@Override
					public void onSwipeStart(int position) {
						// swipe start
					}

					@Override
					public void onSwipeEnd(int position) {
						// swipe end
					}
				});

	}

	@Override
	public void onResume() {
		super.onResume();
        isPlay = true;
        runnable.run();
        XmlDB.getInstance(context).saveKey("go_stop", true);
		MobclickAgent.onResume(context);
	}
	
	@Override
	public void onPause() {
		super.onPause();
        isPlay = false;
		MobclickAgent.onPause(context);
/*		try {
			if(Constant.downloadflag){
				XmlDB.getInstance(context).saveKey(mToyBricksProductAdapter.getItem(Constant.toybricks).getDisplayname()+"_download", false);
				mToyBricksProductAdapter.closedownload();
				Constant.downloadflag=false;
			}	
		} catch (NullPointerException e) {
			// TODO: handle exception
		}*/

	}
	@Override
	protected void onDestroy() {
		unregisterReceiver();
		try {
			if(Constant.downloadflag){
				XmlDB.getInstance(context).saveKey(mToyBricksProductAdapter.getItem(Constant.toybricks).getDisplayname()+"_download", false);
				mToyBricksProductAdapter.closedownload();
				Constant.downloadflag=false;
			}
		} catch (NullPointerException e) {
			// TODO: handle exception
		}

		super.onDestroy();
	}
	private  void registerReceiver(){
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver=new NetworkChangeReceiver();
        this.registerReceiver(myReceiver, filter);
    }
	
	private  void unregisterReceiver(){
        this.unregisterReceiver(myReceiver);
    }
  
	//返回键监听
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				// TODO 退出QCR
				if(!(UnityGetAndroidUI.instance().getActivity()==null)){
					UnityGetAndroidUI.instance().getActivity().finish(); 
				}
				application.exit();
				finish();
			}
		}

		return false;
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==Constant.QR_REQUESTCODE){
			if(resultCode==RESULT_OK){
				XmlDB.getInstance(context).saveKey("QROK", true);
			}else if(resultCode==RESULT_CANCELED){
				XmlDB.getInstance(context).saveKey("QROK", false);
			}else if(resultCode==2015){
				XmlDB.getInstance(context).saveKey("QROK", false);
			}
			mToyBricksProductAdapter.notifyDataSetChanged();
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,getResources().getDisplayMetrics());
	}

	public void deleteFiles(int i) {		
		if (deleteDir(Constant.TOYBRICKS_PATH+mToyBricksProductAdapter.getItem(i).getDisplayname())) {
			File file = new File(Constant.TOYBRICKS_PATH+mToyBricksProductAdapter.getItem(i).getDisplayname());
			if (!file.exists()) {
				if (XmlDB.getInstance(context).getKeyBooleanValue(mToyBricksProductAdapter.getItem(i).getDisplayname() + "_choose", false)) {
					XmlDB.getInstance(context).saveKey("toybricks",XmlDB.getInstance(context).getKeyIntValue("toybricks", 0) - 1);
					XmlDB.getInstance(context).removeKey(mToyBricksProductAdapter.getItem(i).getDisplayname()+"_choose");
				}				
	            XmlDB.getInstance(context).saveKey(mToyBricksProductAdapter.getItem(i).getDisplayname()+"_download",false);
	            XmlDB.getInstance(context).saveKey(mToyBricksProductAdapter.getItem(i).getDisplayname()+"_ToyBricksFile", false);
	            mToyBricksProductAdapter.notifyDataSetChanged();
			}
			folderScan(Constant.TOYBRICKS_PATH+mToyBricksProductAdapter.getItem(i).getDisplayname());
			isSDAvailableSize();
		}
	}
  
	// 删除文件夹和文件夹里面的文件
	public boolean deleteDir(String path) {
		Boolean delte=false;
		try {
			File dir = new File(path);
			if (dir == null || !dir.exists() || !dir.isDirectory())
				delte = true;
			for (File file : dir.listFiles()) {
				if (file.isFile())
					file.delete();// 删除所有文件
				else if (file.isDirectory())
					deleteDir(file.getAbsolutePath());// 递规的方式删除文件夹
			}
			delte = dir.delete();	// 删除目录本身	
		} catch (Exception e) {
			Toast.makeText(context, "删除失败  存在问题", Toast.LENGTH_SHORT).show();
		}
		return delte;
	}

	@Override
	protected void loadViewLayout() {
		// TODO Auto-generated method stub
		setContentView(ResourcesUtils.getLayout(context, "toybricks_activity_main"));

	}
	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
        mToyBricksListView = (SwipeMenuListView) findViewById(ResourcesUtils.getId(context, "toybricks_swipemenulistview"));
        toybricks_topbar_setting=(ImageView)findViewById(ResourcesUtils.getId(context, "toybricks_topbar_setting"));
        toybricks_topbar_play=(ImageView)findViewById(ResourcesUtils.getId(context, "toybricks_topbar_play"));
        mGallery = (Gallery) findViewById(ResourcesUtils.getId(context, "gallery"));
        mSlideViews = new ArrayList<ImageView>();
        mSlideViews.add((ImageView) findViewById(ResourcesUtils.getId(context, "imgPoint0")));
        mSlideViews.add((ImageView) findViewById(ResourcesUtils.getId(context, "imgPoint1")));
        mSlideViews.add((ImageView) findViewById(ResourcesUtils.getId(context, "imgPoint2")));
	}
	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
        mGallery.setOnItemSelectedListener(this);
        toybricks_topbar_setting.setOnClickListener(this);
        toybricks_topbar_play.setOnClickListener(this);
	}
	@Override
	protected void processLogic() {
		// TODO Auto-generated method stub
        //服务器和本地读取数据
        if(NetworkUtil.getConnectivityStatus(context)==1||NetworkUtil.getConnectivityStatus(context)==2){
        	serverData();
        }else{
        	File file=new File(Constant.TOYBRICKS_CACHEPATH+"product.json");
        	if(file.length()<100){
        		//Toast.makeText(context, "数据资源不足,检查网络是否正常", Toast.LENGTH_LONG).show();
        		CommonUtil.showInfoDialog(this,getString(ResourcesUtils.getString(context, "net_error")));
        	}else{
        		 loadLoaclData(); 
        	}
        }
	
	}

	// server data
	private void serverData() {
		RequestVo toybricks = new RequestVo(ResourcesUtils.getString(context, "toybricks_app_toybricks"), this, null,new ToyBricksParser());
		super.getDataFromServer(toybricks, new DataCallback<ToyBricks>() {

			@Override
			public void processData(ToyBricks paramObject,boolean paramBoolean) {
				
				if(paramObject.getAdvertisements()!=null){
					mToyBricksAdvertisementAdapter = new ToyBricksAdvertisementAdapter(ToyBricksMainActivity.this, paramObject.getAdvertisements());
					mGallery.setAdapter(mToyBricksAdvertisementAdapter);
				}
				if(paramObject.getProducts()!=null){
					mToyBricksProductAdapter = new ToyBricksProductAdapter(ToyBricksMainActivity.this, mToyBricksListView,paramObject.getProducts());
					mToyBricksListView.setAdapter(mToyBricksProductAdapter);
					if (!(XmlDB.getInstance(context).getKeyIntValue("toybrickssize", 0) == paramObject.getProducts().size())) {
						XmlDB.getInstance(context).saveKey("toybrickssize",paramObject.getProducts().size());
					}
				}
				
			}
		});
/*		
		RequestVo advertisement = new RequestVo(ResourcesUtils.getString(
				context, "toybricks_app_advertisement"), this, null,
				new ToyBricksAdvertisementParser());
		super.getDataFromServer(advertisement,
				new DataCallback<ArrayList<ToyBricksAdvertisement>>() {
					@Override
					public void processData(
							ArrayList<ToyBricksAdvertisement> paramObject,
							boolean paramBoolean) {
						// TODO advertisement广告位
						mToyBricksAdvertisementAdapter = new ToyBricksAdvertisementAdapter(
								ToyBricksMainActivity.this, paramObject);
						mGallery.setAdapter(mToyBricksAdvertisementAdapter);
					}
				});

		RequestVo product = new RequestVo(ResourcesUtils.getString(context,
				"toybricks_app_product"), this, null,
				new ToyBricksProductParser());
		super.getDataFromServer(product,
				new DataCallback<ArrayList<ToyBricksProduct>>() {

					@Override
					public void processData(
							ArrayList<ToyBricksProduct> paramObject,
							boolean paramBoolean) {
						// TODO product产品位
						mToyBricksProductAdapter = new ToyBricksProductAdapter(
								ToyBricksMainActivity.this, mToyBricksListView,
								paramObject);
						mToyBricksListView.setAdapter(mToyBricksProductAdapter);
						if (!(XmlDB.getInstance(context).getKeyIntValue(
								"toybrickssize", 0) == paramObject.size())) {
							XmlDB.getInstance(context).saveKey("toybrickssize",
									paramObject.size());
						}
					}
				});*/

	}

	//loacl data 
	private void loadLoaclData() {
		try {
			File toybricks=new File(Environment.getExternalStorageDirectory() +"/.toybricks/"+"toybricks.json");
			String  mToyBricks= FileUtils.readFileAsString(toybricks);
			Gson gson=new Gson();
			if(mToyBricks.length()>100){
				ToyBricks paramObject=gson.fromJson(mToyBricks,new TypeToken<ToyBricks>() {}.getType());
				if(paramObject.getAdvertisements()!=null){
					mToyBricksAdvertisementAdapter = new ToyBricksAdvertisementAdapter(ToyBricksMainActivity.this, paramObject.getAdvertisements());
					mGallery.setAdapter(mToyBricksAdvertisementAdapter);
				}
				if(paramObject.getProducts()!=null){
					mToyBricksProductAdapter = new ToyBricksProductAdapter(ToyBricksMainActivity.this, mToyBricksListView,paramObject.getProducts());
					mToyBricksListView.setAdapter(mToyBricksProductAdapter);
					if (!(XmlDB.getInstance(context).getKeyIntValue("toybrickssize", 0) == paramObject.getProducts().size())) {
						XmlDB.getInstance(context).saveKey("toybrickssize",paramObject.getProducts().size());
					}
				}
			}
/*		    File advertisement=new File(Environment.getExternalStorageDirectory() +"/.toybricks/"+"advertisement.json");
		    File product=new File(Environment.getExternalStorageDirectory() +"/.toybricks/"+"product.json");
		   
		    String  mAdvertisement= FileUtils.readFileAsString(advertisement);
		    String  mProduct=FileUtils.readFileAsString(product);
		    if (advertisement.length()>30&&product.length()>30){
		    	ArrayList<ToyBricksAdvertisement> paramObjectAdvertisement=gson.fromJson(mAdvertisement,new TypeToken<ArrayList<ToyBricksAdvertisement>>() {}.getType());
		    	ArrayList<ToyBricksProduct> paramObjectProduct = gson.fromJson(mProduct, new TypeToken<ArrayList<ToyBricksProduct>>() {}.getType());
		        mToyBricksAdvertisementAdapter = new ToyBricksAdvertisementAdapter(ToyBricksMainActivity.this, paramObjectAdvertisement);
		        mToyBricksProductAdapter = new ToyBricksProductAdapter(ToyBricksMainActivity.this, mToyBricksListView, paramObjectProduct);
		        mGallery.setAdapter(mToyBricksAdvertisementAdapter);
		        mToyBricksListView.setAdapter(mToyBricksProductAdapter);
		        if(!(XmlDB.getInstance(context).getKeyIntValue("toybrickssize", 0)==paramObjectProduct.size())){
		        	XmlDB.getInstance(context).saveKey("toybrickssize", paramObjectProduct.size());
		        }
		    }*/else {
		    	serverData();
		    }
		}catch (IOException e){

		}
	}
	
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		// TODO Auto-generated method stub
        int size = mSlideViews.size();
        for (int i = 0; i < size; i++) {
            int j = arg2 % size;
            ImageView imageView = mSlideViews.get(i);
            if (j == i)
                imageView.setBackgroundResource(ResourcesUtils.getDrawable(context, "slide_adv_selected"));
            else
                imageView.setBackgroundResource(ResourcesUtils.getDrawable(context, "slide_adv_normal"));
        }
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		if(view==toybricks_topbar_setting){
			startActivity(new Intent(ToyBricksMainActivity.this, ToyBricksAboutActivity.class));
			MobclickAgent.onEvent(context, "setting");	
		}else if(view == toybricks_topbar_play){
			if(Constant.downloadflag){
				 Toast.makeText(context,"当前有木丸子下载更新任务，请稍后再试。",Toast.LENGTH_SHORT).show();
			}else{
				try {
					int i=0;
					int toybrickssize=XmlDB.getInstance(context).getKeyIntValue("toybrickssize", 0);
					if(toybrickssize!=0){
					    	for (int j = 0; j < toybrickssize; j++) {			
					    		if(XmlDB.getInstance(context).getKeyBooleanValue(mToyBricksProductAdapter.getItem(j).getDisplayname()+"_choose", false)){   
					    			if(UnityPlayer.currentActivity==null){
					    				UnityGetAndroidUI.instance().UnitySendMessage("ARCamera", "loadshuju", Constant.TOYBRICKS_PATH+mToyBricksProductAdapter.getItem(j).getDisplayname()+"/ditu/"+mToyBricksProductAdapter.getItem(j).getDisplayname());
					    				i++;
					    			}else{
					    				UnityPlayer.UnitySendMessage("ARCamera", "loadshuju", Constant.TOYBRICKS_PATH+mToyBricksProductAdapter.getItem(j).getDisplayname()+"/ditu/"+mToyBricksProductAdapter.getItem(j).getDisplayname());
					    				i++;
					    			}
					    		}
							}	
					}
			    	if(i!=0&&i<=3){
			    		 XmlDB.getInstance(context).saveKey("go_stop", false);
			    		startActivity(new Intent(ToyBricksMainActivity.this, com.unity3d.player.UnityPlayerNativeActivity.class));
			    	}else{
			    		Toast.makeText(context,"你还没有选中木丸子哦", Toast.LENGTH_LONG).show();
			    	}
			    	MobclickAgent.onEvent(context, "play");
					} catch (UnsatisfiedLinkError e) {
						Toast.makeText(context, "Native method not found : UnitySendMessage", Toast.LENGTH_SHORT).show();
					} catch (NullPointerException e) {
						// TODO: handle exception
						Toast.makeText(context, "NullPointerException", Toast.LENGTH_SHORT).show();
					}
			}

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
    /**
     * 
     * @author ZoZo
     * folderScan()的功能描述：
     * 指定文件夹里面特定类型的文件更新到媒体库
     * @param dirpath 文件夹
     */
    private void folderScan(String dirpath){
    	try {
            File file = new File(dirpath);
            if(file.exists() && file.isDirectory()){
                File[] array = file.listFiles();
                if(array.length>0){
                    for(int i=0;i<array.length;i++){
                        File f = array[i];
                        if(f.isFile()){//FILE TYPE
                            fileScan(f.getAbsolutePath());
                        }
                        else {//FOLDER TYPE
                            folderScan(f.getAbsolutePath());
                        }
                    }
                }
            }			
		} catch (Exception e) {
			Toast.makeText(context, "文件刷新失败咯", Toast.LENGTH_LONG).show();
		}

    }	

}
