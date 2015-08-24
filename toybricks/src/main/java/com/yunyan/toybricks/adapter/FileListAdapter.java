package com.yunyan.toybricks.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yunyan.toybricks.R;
import com.yunyan.toybricks.bean.FileInfo;
import com.yunyan.toybricks.downloader.service.DownloadService;
import com.yunyan.toybricks.downloader.util.Const;
import com.yunyan.toybricks.utils.Constant;
import com.yunyan.toybricks.utils.FileUtils;
import com.yunyan.toybricks.utils.ResourcesUtils;
import com.yunyan.toybricks.utils.XmlDB;
import com.yunyan.toybricks.view.ToyBricksQRActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * 下载文件列表适配器
 *
 */
public class FileListAdapter extends CommonAdapter<FileInfo> {

	/**
	 * 构造函数
	 */
	public FileListAdapter(Context context, List<FileInfo> datas,int itemLayoutResId) {
		super(context, datas, itemLayoutResId);

	}

	@Override
	public void convert(final ViewHolder viewHolder, final FileInfo fileInfo) {

		changeTag(fileInfo, true);
		update(fileInfo);
		Picasso.with(context).load(fileInfo.getIcon()).into((ImageView) viewHolder.getView(ResourcesUtils.getId(context,"toybricks_listview_item_icon")));
		((TextView)viewHolder.getView(ResourcesUtils.getId(context,"toybricks_listview_item_des"))).setText(fileInfo.getDescription());

		if(XmlDB.getInstance(context).getKeyString(fileInfo.getFileName()+"Tag", "null").equals("0")){
			//验证 选择
			if(XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getFileName()+"_checkQr",false)){
				if(XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getFileName()+"_download",false)){
					Picasso.with(context).load(XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getFileName()+"_choose",false) ? ResourcesUtils.getDrawable(context, "toybricks_choose_y"):ResourcesUtils.getDrawable(context, "toybricks_choose_n")).into((ImageView) viewHolder.getView(ResourcesUtils.getId(context,"toybricks_listview_item_btn")));
				}else{
					Picasso.with(context).load(XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getFileName()+"_download",false) ? ResourcesUtils.getDrawable(context, "toybricks_down_y"):ResourcesUtils.getDrawable(context, "toybricks_down_n")).into((ImageView) viewHolder.getView(ResourcesUtils.getId(context,"toybricks_listview_item_btn")));
				}
			}else{
				Picasso.with(context).load(XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getFileName()+"_checkQr",false) ? ResourcesUtils.getDrawable(context, "toybricks_qrcheck_y"):ResourcesUtils.getDrawable(context, "toybricks_qrcheck_n")).into((ImageView) viewHolder.getView(ResourcesUtils.getId(context,"toybricks_listview_item_btn")));
			}
		}else if(XmlDB.getInstance(context).getKeyString(fileInfo.getFileName()+"Tag", "null").equals("1")){
			//下载 选择
			if(XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getFileName()+"_download",false)){
				Picasso.with(context).load(XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getFileName()+"_choose",false) ? ResourcesUtils.getDrawable(context, "toybricks_choose_y"):ResourcesUtils.getDrawable(context, "toybricks_choose_n")).into((ImageView) viewHolder.getView(ResourcesUtils.getId(context,"toybricks_listview_item_btn")));
			}else{
				Picasso.with(context).load(XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getFileName()+"_download",false) ? ResourcesUtils.getDrawable(context, "toybricks_down_y"):ResourcesUtils.getDrawable(context, "toybricks_down_n")).into((ImageView) viewHolder.getView(ResourcesUtils.getId(context,"toybricks_listview_item_btn")));
			}
		}else if(XmlDB.getInstance(context).getKeyString(fileInfo.getFileName()+"Tag", "null").equals("2")){
			//锁定
			Picasso.with(context).load(XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getFileName()+"_lock",false) ? ResourcesUtils.getDrawable(context, "toybricks_lock_y"):ResourcesUtils.getDrawable(context, "toybricks_lock_y")).into((ImageView) viewHolder.getView(ResourcesUtils.getId(context,"toybricks_listview_item_btn")));
		}else if(XmlDB.getInstance(context).getKeyString(fileInfo.getFileName()+"Tag", "null").equals("3")){
			//更新
			Picasso.with(context).load(XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getFileName()+"_update",false) ? ResourcesUtils.getDrawable(context, "toybricks_update_y"):ResourcesUtils.getDrawable(context, "toybricks_update_n")).into((ImageView) viewHolder.getView(ResourcesUtils.getId(context,"toybricks_listview_item_btn")));
		}else if(XmlDB.getInstance(context).getKeyString(fileInfo.getFileName()+"Tag", "null").equals("4")){
			//其他
			Picasso.with(context).load(XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getFileName()+"_other",false) ? ResourcesUtils.getDrawable(context, "toybricks_other_y"):ResourcesUtils.getDrawable(context, "toybricks_other_n")).into((ImageView) viewHolder.getView(ResourcesUtils.getId(context,"toybricks_listview_item_btn")));
		}
		viewHolder.getView(R.id.toybricks_listview_item_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Constant.downloadflag==false){
					if (XmlDB.getInstance(context).getKeyIntValue(fileInfo.getDisplayname() + "Tag", 0) == 0) {
						//验证  选择 下载=============================================================
						if(XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getDisplayname()+"_checkQr",false)){
							if(XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getDisplayname()+"_download",false)){
								chooseAction(fileInfo);
							}else{
								downAction(fileInfo);
							}
						}else{
							checkQrAction(fileInfo);
						}
					} else if (XmlDB.getInstance(context).getKeyIntValue(fileInfo.getDisplayname() + "Tag", 0) == 1) {//下载 选择
						//下载 选择====================================================================
						if(XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getDisplayname()+"_download",false)){
							chooseAction(fileInfo);
						}else{
							downAction(fileInfo);
						}
					} else if (XmlDB.getInstance(context).getKeyIntValue(fileInfo.getDisplayname() + "Tag", 0) == 2) {//锁定
						//锁定
						Toast.makeText(context, "通过需验证的木丸子即可解锁", Toast.LENGTH_SHORT).show();
					} else if (XmlDB.getInstance(context).getKeyIntValue(fileInfo.getDisplayname() + "Tag", 0) == 3) {//更新
						downAction(fileInfo);
					} else if (XmlDB.getInstance(context).getKeyIntValue(fileInfo.getDisplayname() + "Tag", 0) == 4) {//其他
						Toast.makeText(context, "未知动作", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}
	//改变tag
	protected void changeTag(FileInfo fileInfo,boolean flag){
		if(flag){
				if(fileInfo.getToyBrick().equals("2")){
					XmlDB.getInstance(context).saveKey(fileInfo.getDisplayname()+"Tag", "1");
				}else{
					XmlDB.getInstance(context).saveKey(fileInfo.getDisplayname() + "Tag", fileInfo.getToyBrick());
				}
		}else{
				XmlDB.getInstance(context).saveKey(fileInfo.getDisplayname()+"Tag", fileInfo.getToyBrick());
		}
	}
	//更新
	protected void update(FileInfo fileInfo) {
		try {
			File file=new File(Constant.TOYBRICKS_PATH+fileInfo.getDisplayname()+File.separator+"Version.txt");
			if (Integer.parseInt(fileInfo.getVersion()) > Integer.parseInt(FileUtils.readFileAsString(file))){
				XmlDB.getInstance(context).saveKey(fileInfo.getDisplayname()+"Tag", "3");
				XmlDB.getInstance(context).saveKey(fileInfo.getDisplayname()+"_update", true);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	//选择
	protected void chooseAction(FileInfo fileInfo) {
		if (XmlDB.getInstance(context).getKeyIntValue("toybrick", 0) == 1) {
			if (XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getDisplayname() + "_choose", false)) {
				XmlDB.getInstance(context).saveKey("toybrick", XmlDB.getInstance(context).getKeyIntValue("toybrick", 0) - 1);
				XmlDB.getInstance(context).saveKey(fileInfo.getDisplayname() + "_choose", false);
			} else {
				Toast.makeText(context, "只选一期哦", Toast.LENGTH_SHORT).show();
			}
		} else if (XmlDB.getInstance(context).getKeyIntValue("toybrick", 0) == 0) {
			if (XmlDB.getInstance(context).getKeyBooleanValue(fileInfo.getDisplayname() + "_choose", false)) {
				XmlDB.getInstance(context).saveKey(fileInfo.getDisplayname() + "_choose", false);
			} else {
				XmlDB.getInstance(context).saveKey("toybrick", XmlDB.getInstance(context).getKeyIntValue("toybrick", 0) + 1);
				XmlDB.getInstance(context).saveKey(fileInfo.getDisplayname() + "_choose", true);
			}
		}
	}
		//验证
	protected void checkQrAction(FileInfo fileInfo) {
		Intent intent=new Intent();
		intent.putExtra("ToybricksName", fileInfo.getName());
		intent.putExtra("Displayname", fileInfo.getDisplayname());
		intent.setClass(context, ToyBricksQRActivity.class);
		context.startActivity(intent);
	}

	private void downAction(FileInfo fileInfo) {
		Intent intent = new Intent(context, DownloadService.class);
		intent.setAction(DownloadService.ACTION_START);
		intent.putExtra(Const.FILE_INFO_KEY, fileInfo);
		// 启动service，注意，不要忘记在XML里注册service
		context.startService(intent);
	}

	// =========================================

	/**
	 * 更新该下载文件的下载进度
	 *
	 * @param id
	 *            下载文件的ID
	 * @param progress
	 */
	public void updateProgress(int id, int progress) {
		FileInfo fileInfo = this.datas.get(id);
		fileInfo.setFinished(progress);
		Log.e(FileListAdapter.class.getSimpleName(),"正在下载："+id+" 下载进度："+progress);
		// 该方法可以在修改适配器绑定的数组后不用重新刷新activity，通知activity更新ListView
		// 调用notifyDataSetChanged函数后，getView回调函数会重新被调用一遍
		this.notifyDataSetChanged();
	}
	public  void downFinish(FileInfo fileInfo){
		XmlDB.getInstance(context).saveKey(fileInfo.getDisplayname()+"_download",true);
	}
}
