package com.yunyan.toybricks.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunyan.toybricks.bean.ToyBricksProduct;
import com.yunyan.toybricks.component.CommonProgressDialog;
import com.yunyan.toybricks.utils.AESUtils;
import com.yunyan.toybricks.utils.Constant;
import com.yunyan.toybricks.utils.DeviceUtils;
import com.yunyan.toybricks.utils.DownLoadTask;
import com.yunyan.toybricks.utils.FileUtils;
import com.yunyan.toybricks.utils.MD5;
import com.yunyan.toybricks.utils.NetworkUtil;
import com.yunyan.toybricks.utils.ResourcesUtils;
import com.yunyan.toybricks.utils.ThreadPoolManager;
import com.yunyan.toybricks.utils.XmlDB;
import com.yunyan.toybricks.utils.ZipUtil;
import com.yunyan.toybricks.view.ToyBricksMainActivity;
import com.yunyan.toybricks.view.ToyBricksQRActivity;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by George on 2015/3/27.
 */
public class ToyBricksProductAdapter extends ImageAsyncLoaderAdpter<ToyBricksProduct> implements DownLoadTask.DownlaodListener{

	    private ToyBricksMainActivity mContext;
	    private List<ToyBricksProduct> mToyBricksProductList;
	    private ProgressDialog mProgressDialog=null;
	    public Handler handler;

	    long _time = System.currentTimeMillis();
	    int b_length=0;   
	    private int progressVaue=0;
	    private int progresstotal=0;
	    private CommonProgressDialog mDialog=null;
	    private DownLoadTask downLoadTask =null;
	    public ToyBricksProductAdapter(ToyBricksMainActivity mContext, AbsListView absListView,List<ToyBricksProduct> mToyBricksProductList){
	        super(mContext, absListView, mToyBricksProductList);
	        this.mContext=mContext;
	        this.mToyBricksProductList=mToyBricksProductList;
	    }
	    
	    @Override
	    public int getCount() {
	        return mToyBricksProductList.size();
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }
	    
	    @Override
	    public void onImageLoadFinish(Integer position, Drawable drawable) {
	        View view = mListView.findViewWithTag(position);
	        if (view != null) {
	            ImageView iv = (ImageView) view.findViewById(ResourcesUtils.getId(mContext, "toybricks_listview_item_icon"));
	            iv.setImageDrawable(drawable);
	        }
	    }

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
	        ViewHoder mViewHoder=null;
	        if (convertView==null){
	            mViewHoder = new ViewHoder();
	            convertView=inflate(ResourcesUtils.getLayout(mContext, "toybricks_listview_item"),null);
	            mViewHoder.toybricks_listview_item_icon = (ImageView)convertView.findViewById(ResourcesUtils.getId(mContext, "toybricks_listview_item_icon"));
	            mViewHoder.toybricks_listview_item_des=(TextView)convertView.findViewById(ResourcesUtils.getId(mContext, "toybricks_listview_item_des"));
	            mViewHoder.toybricks_listview_item_btn=(ImageView)convertView.findViewById(ResourcesUtils.getId(mContext, "toybricks_listview_item_btn"));
	            convertView.setTag(ResourcesUtils.getLayout(mContext, "toybricks_listview_item"),mViewHoder);
	        }else {
	            mViewHoder=(ViewHoder)convertView.getTag(ResourcesUtils.getLayout(mContext, "toybricks_listview_item"));
	        } 
	        mViewHoder.toybricks_listview_item_des.setText(getItem(position).getDescription());
	        loadImage(position, getItem(position).getIcon());
	        convertView.setTag(position);
	        
	        //资源包更新
	        if(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_ToyBricksFile", false)){
	        	update(position);
	        }
			chenckLocalFile(position);
	        //tag标记分置
/*
	        if(XmlDB.getInstance(mContext).getKeyBooleanValue("AES", false)){
	        	changeTag(getCount(),true);
	        }else{
	        	changeTag(getCount(),false);
	        }
*/
	        changeTag(getCount(),true);
	        if(XmlDB.getInstance(mContext).getKeyString(getItem(position).getDisplayname()+"Tag", "null").equals("0")){
	        	//验证 选择
	        	if(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_checkQr",false)){
	            	if(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_download",false)){
	            		mViewHoder.toybricks_listview_item_btn.setImageResource(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_choose",false) ? ResourcesUtils.getDrawable(mContext, "toybricks_choose_y"):ResourcesUtils.getDrawable(mContext, "toybricks_choose_n"));
	            	}else{
	            		mViewHoder.toybricks_listview_item_btn.setImageResource(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_download",false) ? ResourcesUtils.getDrawable(mContext, "toybricks_down_y"):ResourcesUtils.getDrawable(mContext, "toybricks_down_n"));
	            	}
	        	}else{
	        		mViewHoder.toybricks_listview_item_btn.setImageResource(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_checkQr",false) ? ResourcesUtils.getDrawable(mContext, "toybricks_qrcheck_y"):ResourcesUtils.getDrawable(mContext, "toybricks_qrcheck_n"));
	        	}
	        }else if(XmlDB.getInstance(mContext).getKeyString(getItem(position).getDisplayname()+"Tag", "null").equals("1")){
	        	//下载 选择
	        	if(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_download",false)){
	        		mViewHoder.toybricks_listview_item_btn.setImageResource(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_choose",false) ? ResourcesUtils.getDrawable(mContext, "toybricks_choose_y"):ResourcesUtils.getDrawable(mContext, "toybricks_choose_n"));
	        	}else{
	        		mViewHoder.toybricks_listview_item_btn.setImageResource(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_download",false) ? ResourcesUtils.getDrawable(mContext, "toybricks_down_y"):ResourcesUtils.getDrawable(mContext, "toybricks_down_n"));
	        	}
	        }else if(XmlDB.getInstance(mContext).getKeyString(getItem(position).getDisplayname()+"Tag", "null").equals("2")){
	        	//锁定
	        	mViewHoder.toybricks_listview_item_btn.setImageResource(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_lock",false) ? ResourcesUtils.getDrawable(mContext, "toybricks_lock_y"):ResourcesUtils.getDrawable(mContext, "toybricks_lock_y"));
	        }else if(XmlDB.getInstance(mContext).getKeyString(getItem(position).getDisplayname()+"Tag", "null").equals("3")){
	        	//更新
	        	mViewHoder.toybricks_listview_item_btn.setImageResource(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_update",false) ? ResourcesUtils.getDrawable(mContext, "toybricks_update_y"):ResourcesUtils.getDrawable(mContext, "toybricks_update_n"));
	        }else if(XmlDB.getInstance(mContext).getKeyString(getItem(position).getDisplayname()+"Tag", "null").equals("4")){
	        	//其他
	        	mViewHoder.toybricks_listview_item_btn.setImageResource(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_other",false) ? ResourcesUtils.getDrawable(mContext, "toybricks_other_y"):ResourcesUtils.getDrawable(mContext, "toybricks_other_n"));
	        }
	        mViewHoder.toybricks_listview_item_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
	          		if (Constant.downloadflag==false){  
			        if(XmlDB.getInstance(mContext).getKeyString(getItem(position).getDisplayname()+"Tag", "null").equals("0")){
			        	//验证  选择=============================================================
			        	if(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_checkQr",false)){
			        		if(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_download",false)){
			        			choose(position);
			        		}else{
			        			if(NetworkUtil.getConnectivityStatus(mContext)==1){
				        			download(position);
				        			handler.sendEmptyMessage(Constant.D_START);
	    	                        Toast.makeText(mContext,"正在初始化下载，请稍后。",Toast.LENGTH_SHORT).show();	
			        			}else if(NetworkUtil.getConnectivityStatus(mContext)==2){
			            			try {
			            				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);                    		
			        					builder.setTitle("温馨提示").setMessage("当前是移动网络，确定要使用流量下载吗?")
			        					.setCancelable(false)
			        					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			        						@Override
			        						public void onClick(DialogInterface dialog, int id) {
			        							if(XmlDB.getInstance(mContext).getKeyBooleanValue("sdcard", false)){
			        			        			download(position);
			        			        			handler.sendEmptyMessage(Constant.D_START);
			            	                        Toast.makeText(mContext,"正在初始化下载，请稍后。",Toast.LENGTH_SHORT).show();
			        							}else{
			        								Toast.makeText(mContext,"SD卡空间不足，下载已取消，详询客服",Toast.LENGTH_LONG).show();
			        								mContext.sendMsgToMe();
			        							}
			        						}
			        					})
			        					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			        						@Override
			        						public void onClick(DialogInterface dialog, int id) {
			        							dialog.cancel();
			        						}
			        					}).create().show();	            		
			                    		
			        				} catch (IllegalArgumentException e) {
			        					// TODO: handle exception
			        					Toast.makeText(mContext, "IllegalArgumentException", Toast.LENGTH_LONG).show();
			        				}				
			        			}

			        		}
			        	}else{
			        		checkQr(position);
			        	}
			        }else if(XmlDB.getInstance(mContext).getKeyString(getItem(position).getDisplayname()+"Tag", "null").equals("1")){
			        	//下载 选择====================================================================
			        	if(XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(position).getDisplayname()+"_download",false)){
			        		choose(position);
			        	}else{
		        			if(NetworkUtil.getConnectivityStatus(mContext)==1){
			        			download(position);
			        			handler.sendEmptyMessage(Constant.D_START);
		                        Toast.makeText(mContext,"正在初始化下载，请稍后。",Toast.LENGTH_SHORT).show();	
		        			}else if(NetworkUtil.getConnectivityStatus(mContext)==2){
		            			try {
		            				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);                    		
		        					builder.setTitle("温馨提示").setMessage("当前是移动网络，确定要使用流量下载吗?")
		        					.setCancelable(false)
		        					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		        						@Override
		        						public void onClick(DialogInterface dialog, int id) {
		        							if(XmlDB.getInstance(mContext).getKeyBooleanValue("sdcard", false)){
		        			        			download(position);
		        			        			handler.sendEmptyMessage(Constant.D_START);
		            	                        Toast.makeText(mContext,"正在初始化下载，请稍后。",Toast.LENGTH_SHORT).show();
		        							}else{
		        								Toast.makeText(mContext,"SD卡空间不足，下载已取消，详询客服",Toast.LENGTH_LONG).show();
		        								mContext.sendMsgToMe();
		        							}
		        						}
		        					})
		        					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
		        						@Override
		        						public void onClick(DialogInterface dialog, int id) {
		        							dialog.cancel();
		        						}
		        					}).create().show();	            		
		                    		
		        				} catch (IllegalArgumentException e) {
		        					// TODO: handle exception
		        					Toast.makeText(mContext, "IllegalArgumentException", Toast.LENGTH_LONG).show();
		        				}				
		        			}
			        	}
			        }else if(XmlDB.getInstance(mContext).getKeyString(getItem(position).getDisplayname()+"Tag", "null").equals("2")){
			        	//锁定
			        	Toast.makeText(mContext, "通过需验证的木丸子即可解锁", Toast.LENGTH_SHORT).show();
			        }else if(XmlDB.getInstance(mContext).getKeyString(getItem(position).getDisplayname()+"Tag", "null").equals("3")){
			        	//更新========================================================
	        			if(NetworkUtil.getConnectivityStatus(mContext)==1){
		        			download(position);
		        			handler.sendEmptyMessage(Constant.D_START);
	                        Toast.makeText(mContext,"正在初始化下载，请稍后。",Toast.LENGTH_SHORT).show();	
	        			}else if(NetworkUtil.getConnectivityStatus(mContext)==2){
	            			try {
	            				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);                    		
	        					builder.setTitle("温馨提示").setMessage("当前是移动网络，确定要使用流量下载吗?")
	        					.setCancelable(false)
	        					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
	        						@Override
	        						public void onClick(DialogInterface dialog, int id) {
	        							if(XmlDB.getInstance(mContext).getKeyBooleanValue("sdcard", false)){
	        			        			download(position);
	        			        			handler.sendEmptyMessage(Constant.D_START);
	            	                        Toast.makeText(mContext,"正在初始化下载，请稍后。",Toast.LENGTH_SHORT).show();
	        							}else{
	        								Toast.makeText(mContext,"SD卡空间不足，下载已取消，详询客服",Toast.LENGTH_LONG).show();
	        								mContext.sendMsgToMe();
	        							}
	        						}
	        					})
	        					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
	        						@Override
	        						public void onClick(DialogInterface dialog, int id) {
	        							dialog.cancel();
	        						}
	        					}).create().show();	            		
	                    		
	        				} catch (IllegalArgumentException e) {
	        					// TODO: handle exception
	        					Toast.makeText(mContext, "IllegalArgumentException", Toast.LENGTH_LONG).show();
	        				}				
	        			}
			        }else if(XmlDB.getInstance(mContext).getKeyString(getItem(position).getDisplayname()+"Tag", "null").equals("4")){
			        	//其他
			        	other(position);
			        }
			        Constant.toybricks=position;		
	          		}else{
	          			showDownProgressDialog();
	          			Toast.makeText(mContext, "有任务在下载，请稍后", Toast.LENGTH_SHORT).show();
	          		}
				}
			});
	        
	        handler = new Handler() {
	            @Override
	            public void handleMessage(Message msg) {
	                switch (msg.what){
	                	case Constant.D_START://启动 下载
	                        showDownProgressDialog();
	                        Constant.downloadflag=true;
	                	break;
	                    case Constant.D_HANDLING://下载中
	                		Bundle x=msg.getData();
	                	    mDialog.setMax(progresstotal);
	                	    mDialog.setProgress(progressVaue);
	                	    mDialog.setSpeed(x.getInt("sudu"));
	                        break;
	                    case Constant.D_COMPLETED://下载完成
	                    	if(progressVaue<10*1024*1024){
	                    		unZip2(Constant.toybricks);
	                    	}else{
	                    		unZip(Constant.toybricks);
	                    	}
							XmlDB.getInstance(mContext).saveKey(getItem(Constant.toybricks).getDisplayname() + "_choose", false);
	                        progressVaue=0;
	                        progresstotal=0;
	                        //TODO 下载完成UI刷新
	                        if(mDialog!=null){
	                        	mDialog.dismiss();
	                        	mDialog=null;
	                        }
	                		if(downLoadTask!=null){
	                			downLoadTask.cancel();
	                			downLoadTask=null;	
	                		}
	                        break;
	                    case Constant.D_ERROR://下载错误
	                        Bundle downLoadError = msg.getData();
	                        if(downLoadError.getInt("downLoadError")==1){
	                        	Toast.makeText(mContext, "文件路径错误", Toast.LENGTH_LONG).show();
	                        }else if(downLoadError.getInt("downLoadError")==2){
	                        	Toast.makeText(mContext, "下载连接异常", Toast.LENGTH_LONG).show();
	                        }else if(downLoadError.getInt("downLoadError")==3){
	                        	Toast.makeText(mContext, "下载连接超时", Toast.LENGTH_LONG).show();
	                        }else if(downLoadError.getInt("downLoadError")==10){
	                        	Toast.makeText(mContext, "下载连接超时", Toast.LENGTH_LONG).show();
	                        }else if(downLoadError.getInt("downLoadError")==11){
	                        	Toast.makeText(mContext, "网路过慢，无法下载", Toast.LENGTH_LONG).show();
	                        }else if(downLoadError.getInt("downLoadError")==0){
	                        	Toast.makeText(mContext, "未知的下载错误", Toast.LENGTH_LONG).show();
	                        }else{
	                            Toast.makeText(mContext,"网路异常，下载失败",Toast.LENGTH_LONG).show();
	                        }
	                        if(mDialog!=null){
	                        	mDialog.dismiss();
	                        	mDialog=null;
	                        }
	                		if(downLoadTask!=null){
	                			downLoadTask.cancel();
	                			downLoadTask=null;	
	                		}
	                        progressVaue=0;
	                        progresstotal=0;
	                        Constant.downloadflag=false;
	                        XmlDB.getInstance(mContext).saveKey(getItem(Constant.toybricks).getDisplayname()+"Tag", "1");
	                        Constant.toybricks=-1;
	                        notifyDataSetChanged();
	                        break;
	                    case Constant.Z_START://解压启动
	                    	showProgressDialog(Constant.toybricks);
	                    	break;
	                    case Constant.Z_HANDLING://解压中
	                        Bundle b = msg.getData();
	                        mProgressDialog.setProgress(b.getInt(Constant.PERCENT));
	                        break;
	                    case Constant.Z_COMPLETED://解压完成
	                    	if(mProgressDialog!=null){
	                    		mProgressDialog.dismiss();  
	                    		mProgressDialog=null;
	                    	}
	                    	Constant.downloadflag=false;
	                    	XmlDB.getInstance(mContext).saveKey(getItem(Constant.toybricks).getDisplayname()+"_download",true);
	                        XmlDB.getInstance(mContext).saveKey(getItem(Constant.toybricks).getDisplayname()+"_ToyBricksFile",true);
	                        Constant.toybricks=-1;
	                        //TODO 解压完成UI刷新
	                        notifyDataSetChanged();
	                        break;
	                    case Constant.Z_ERROR://解压错误
	                    	if(mProgressDialog!=null){
	                    		mProgressDialog.dismiss();  
	                    		mProgressDialog=null;
	                    	}
	                    	Constant.downloadflag=false;
	                    	 Constant.toybricks=-1;
	                        Toast.makeText(mContext, "解压错误", Toast.LENGTH_SHORT).show();
	                        notifyDataSetChanged();
	                        break;                        
	                }
	            }
	        };        
			return convertView;
		}
		protected void chenckLocalFile(int i){
			try {
				File file=new File(Constant.TOYBRICKS_PATH+getItem(i).getDisplayname()+File.separator+"Version.txt");
				if (file.exists()){
					File aes=new File(Constant.TOYBRICKS_SEAPATH, MD5.digest(getItem(i).getDisplayname()));
					if(AESUtils.decrypt(DeviceUtils.getDeviceInfo(mContext), FileUtils.readFileAsString(file)).substring(0,DeviceUtils.getDeviceInfo(mContext).length()).equals(DeviceUtils.getDeviceInfo(mContext))){
						XmlDB.getInstance(mContext).saveKey(getItem(i).getDisplayname()+"Tag","1");
					}
				}
			}catch (NullPointerException e) {

			}catch (IOException e){

			}catch (Exception e){

			}
		}
		protected void checkChoose(int i){

		}
		//解压进度条
		protected void showProgressDialog(int i) {
	    	if(mProgressDialog==null){
	            mProgressDialog = new ProgressDialog(mContext);
	            mProgressDialog.setCancelable(false);
	            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	            mProgressDialog.setMessage(getItem(i).getDisplayname()+" 正在解压中...");
	            mProgressDialog.show();
	    	}
	    }
		//下载进度条
	    protected void showDownProgressDialog(){
			if(mDialog==null){
			    mDialog = new CommonProgressDialog(mContext);
			    mDialog.setMessage("正在下载");
			    mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			    mDialog.setOnCancelListener(new OnCancelListener() {
			      @Override
			      public void onCancel(DialogInterface dialog) {
			    	  
			      }
			    });
			    mDialog.show();	
			}else{
				mDialog.show();	
			}
		  }	
	    //大于10兆的资源包解压
		protected void unZip(int i){
	        try {          	
	            ZipUtil.unZipFileWithProgress(Constant.TOYBRICKS_PATH, new File(Constant.TOYBRICKS_PATH, getItem(i).getDisplayname()+".zip"), handler, true);
	        } catch (ZipException e) {
	        	Toast.makeText(mContext, "解压错误", Toast.LENGTH_SHORT).show();
	        }
	    }
		//小于10兆的资源包解压
	    protected void unZip2(int i){
			try {
				ZipFile zipFile= new ZipFile(new File(Constant.TOYBRICKS_PATH, getItem(i).getDisplayname()+".zip"));
				zipFile.extractAll(Constant.TOYBRICKS_PATH);
			} catch (ZipException e) {
				// TODO Auto-generated catch block
				Toast.makeText(mContext, "解压错误", Toast.LENGTH_SHORT).show();
			}finally{
				File file=new File(Constant.TOYBRICKS_PATH, getItem(i).getDisplayname()+".zip");
				if(file.exists()){
					file.delete();
					file.deleteOnExit();
				}
				handler.sendEmptyMessage(Constant.Z_COMPLETED);
			}
	    }	
	    //改变tag
	    protected void changeTag(int i,boolean flag){
	    	if(flag){
	    		for (int j = 0; j < i; j++) {
	    			if(getItem(j).getToyBrick().equals("2")){
	    				XmlDB.getInstance(mContext).saveKey(getItem(j).getDisplayname()+"Tag", "1");
	    			}else{
	    				XmlDB.getInstance(mContext).saveKey(getItem(j).getDisplayname()+"Tag", getItem(j).getToyBrick());
	    			}
	    		}
	    	}else{
	    		for (int j = 0; j < i; j++) {
	    			XmlDB.getInstance(mContext).saveKey(getItem(j).getDisplayname()+"Tag", getItem(j).getToyBrick());
	    		}
	    	}
		}

		//其他
		protected void other(int i) {
			// TODO Auto-generated method stub
			XmlDB.getInstance(mContext).saveKey(getItem(i).getDisplayname()+"Tag", 4);
		}
		//更新
		protected void update(int i) {
			// TODO Auto-generated method stub
	        try {
	            File file=new File(Constant.TOYBRICKS_PATH+getItem(i).getDisplayname()+File.separator+"Version.txt");
	            if (Integer.parseInt(getItem(i).getVersion()) > Integer.parseInt(FileUtils.readFileAsString(file))){
	            	XmlDB.getInstance(mContext).saveKey(getItem(i).getDisplayname()+"Tag", "3");
	            	XmlDB.getInstance(mContext).saveKey(getItem(i).getDisplayname()+"_update", true);
	            }
	    	} catch (FileNotFoundException e) {
	    		e.printStackTrace();
	        }catch (IOException e){
	        	e.printStackTrace();
	        }		
		}
		//选择
		protected void choose(int i) {
				if (XmlDB.getInstance(mContext).getKeyBooleanValue(getItem(i).getDisplayname() + "_choose", false)) {
					if (XmlDB.getInstance(mContext).getKeyIntValue("toybricks", 0) > 0) {
						XmlDB.getInstance(mContext).saveKey("toybricks",XmlDB.getInstance(mContext).getKeyIntValue("toybricks", 0) - 1);
						XmlDB.getInstance(mContext).saveKey(getItem(i).getDisplayname() + "_choose", false);
					}
				} else {
					if (XmlDB.getInstance(mContext).getKeyIntValue("toybricks", 0) < 1) {
						XmlDB.getInstance(mContext).saveKey("toybricks",XmlDB.getInstance(mContext).getKeyIntValue("toybricks", 0) + 1);
						XmlDB.getInstance(mContext).saveKey(getItem(i).getDisplayname() + "_choose", true);
					} else {
						Toast.makeText(mContext, "只选一期哦", Toast.LENGTH_SHORT).show();
					}
				}
				notifyDataSetChanged();	
		}
		//下载
		protected void download(int i) {
			// TODO Auto-generated method stub
			if(downLoadTask==null){
		    	File file = new File(Constant.TOYBRICKS_PATH, getItem(i).getDisplayname()+".zip");
		        downLoadTask = new DownLoadTask(getItem(i).getLujing(), file.getAbsolutePath(),5);
		        downLoadTask.setListener(this);
		        ThreadPoolManager.getInstance().addTask(downLoadTask);	
			}

		}
		public void closedownload() {
			if(downLoadTask!=null){
				downLoadTask.cancel();
				downLoadTask=null;	
			}
		}
		//验证
		protected void checkQr(int i) {
			// TODO Auto-generated method stub
			//mContext.handler.sendEmptyMessage(2015);	
	    	Intent intent=new Intent();
	    	intent.putExtra("ToybricksName", getItem(i).getName());
	    	intent.putExtra("Displayname", getItem(i).getDisplayname());
	    	intent.setClass(mContext, ToyBricksQRActivity.class);
	    	mContext.startActivityForResult(intent, Constant.QR_REQUESTCODE);
		}
		
		//--------------------------------------------------------------------------------------------------------	
				//下载过程监控
		//--------------------------------------------------------------------------------------------------------	
		@Override
		public void update(int total, int len, int threadid) {
			// TODO Auto-generated method stub
	    	progresstotal=total;
	        progressVaue = progressVaue+len;
	    	if(System.currentTimeMillis()-_time>1000){
	    		if(b_length<10240){
	    			Bundle bundle=new Bundle();
	    			Message message=new Message();
	    			bundle.putInt("sudu", (b_length/1024));
	    			message.setData(bundle);
	    			message.what=Constant.D_HANDLING;
	    			handler.sendMessage(message);
	    			b_length = 0;
	        		_time = System.currentTimeMillis();
	        		
	    		}else{
	    			Bundle bundle=new Bundle();
	    			Message message=new Message();
	    			bundle.putInt("sudu", (b_length/1024));
	    			message.setData(bundle);
	    			message.what=Constant.D_HANDLING;
	    			handler.sendMessage(message);
	        		b_length = 0;
	        		_time = System.currentTimeMillis();
	    		}
	    	}else{
	    		b_length+=len;
	    	}  		
		}
		
		@Override
		public void downLoadFinish(int totalSucess) {
			// TODO Auto-generated method stub
	        if (totalSucess==5){
	            handler.sendEmptyMessage(Constant.D_COMPLETED);
	        }		
		}
		
		@Override
		public void downLoadError(int type) {
			// TODO Auto-generated method stub
			Bundle bundle=new Bundle();
			Message message=new Message();
			bundle.putInt("downLoadError",type);
			message.setData(bundle);
			message.what=Constant.D_ERROR;
			handler.sendMessage(message);		
		}
	    
	    @Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub
			super.notifyDataSetChanged();
			if(XmlDB.getInstance(mContext).getKeyBooleanValue("QROK", false)){
				download(Constant.toybricks);
				handler.sendEmptyMessage(Constant.D_START);
				XmlDB.getInstance(mContext).saveKey("QROK", false);
			}
		}
		
		static class ViewHoder{
	        ImageView toybricks_listview_item_icon;
	        TextView  toybricks_listview_item_des;
	        ImageView toybricks_listview_item_btn;
	    }

	}
