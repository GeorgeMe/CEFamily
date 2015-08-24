/**
 * Project Name:CeCe
 * File Name:AboutActivity.java
 * Package Name:com.yunyan.kameng
 * Date:2014年11月3日下午2:52:25
 * Copyright (c) 2014, qizhi_china@163.com All Rights Reserved.
 *
*/

package com.yunyan.toybricks.view;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;
import com.yunyan.toybricks.utils.Constant;
import com.yunyan.toybricks.utils.ResourcesUtils;
import com.yunyan.toybricks.utils.XmlDB;


/**
 * ClassName:AboutActivity 
 * Date:     2014年11月3日 下午2:52:25 
 * @author   ZoZo
 * @version  版本号
 * @since    JDK 1.7
 * @see 	 
 */
public class ToyBricksAboutActivity extends ToyBricksBaseActivity  implements OnClickListener {

    private RelativeLayout toybricks_about_qq, toybricks_about_tel, toybricks_about_taobao,  toybricks_about_version;//toybricks_about_site,
    private TextView toybricks_icon_version,toybricks_update_version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // initView();
        PushAgent.getInstance(context).onAppStart();//应用统计
    }

    @Override
    protected void loadViewLayout() {
        setContentView(ResourcesUtils.getLayout(context,"toybricks_activity_about"));
    }

    @Override
    protected void findViewById() {
        toybricks_about_qq = (RelativeLayout) this.findViewById(ResourcesUtils.getId(context,"toybricks_about_qq"));
        toybricks_about_tel = (RelativeLayout) this.findViewById(ResourcesUtils.getId(context,"toybricks_about_tel"));
        toybricks_about_taobao = (RelativeLayout) this.findViewById(ResourcesUtils.getId(context,"toybricks_about_taobao"));
       // toybricks_about_site = (RelativeLayout) this.findViewById(ResourcesUtils.getId(context,"toybricks_about_site"));
        toybricks_about_version = (RelativeLayout) this.findViewById(ResourcesUtils.getId(context,"toybricks_about_version"));
        toybricks_icon_version = (TextView)this.findViewById(ResourcesUtils.getId(context,"toybricks_icon_version"));
        toybricks_update_version =(TextView)this.findViewById(ResourcesUtils.getId(context,"toybricks_update_version"));
    }

    @Override
    protected void setListener() {
        toybricks_about_qq.setOnClickListener(this);
        toybricks_about_tel.setOnClickListener(this);
        toybricks_about_taobao.setOnClickListener(this);
       // toybricks_about_site.setOnClickListener(this);
        toybricks_about_version.setOnClickListener(this);
    }

    @Override
    protected void processLogic() {
    	toybricks_icon_version.setText("木丸子"+ getVersion());
    	toybricks_update_version.setText("当前版本" + getVersion());
    }

    @Override
    public void onClick(View view) {
        if (view==toybricks_about_qq){

        }else if (view==toybricks_about_tel){
            Intent tel = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +Constant.WE_TEL));
            PackageManager pm_tel = context.getPackageManager();
            List<ResolveInfo> list_tel = pm_tel.queryIntentActivities(tel, 0);
            if(list_tel.size()>0){
                startActivity(tel);
            }else{
                Toast.makeText(context, "equipment or application is not ready,detailed consultation customer service.", Toast.LENGTH_SHORT).show();
            }
        }else if (view==toybricks_about_taobao){
            Intent taobao = new Intent();
            taobao.setData(Uri.parse(Constant.WE_TAOBAO));
            taobao.setAction(Intent.ACTION_VIEW);
            PackageManager pm_taobao = context.getPackageManager();
            List<ResolveInfo> list_taobao = pm_taobao.queryIntentActivities(taobao, 0);
            if(list_taobao.size()>0){
                startActivity(taobao);
            }else{
                Toast.makeText(context, "equipment or application is not ready,detailed consultation customer service.", Toast.LENGTH_SHORT).show();
            }
        }
/*        else if (view==toybricks_about_site){
            Intent site = new Intent();
            site.setData(Uri.parse(Constant.WE_WEB));
            site.setAction(Intent.ACTION_VIEW);
            PackageManager pm_site = context.getPackageManager();
            List<ResolveInfo> list_site = pm_site.queryIntentActivities(site, 0);
            if(list_site.size()>0){
                startActivity(site);
            }else{
                Toast.makeText(context, "equipment or application is not ready,detailed consultation customer service.", Toast.LENGTH_SHORT).show();
            }
        }
        */
        else if (view==toybricks_about_version){
            Toast.makeText(context,"the latest version.",Toast.LENGTH_SHORT).show();
			UmengUpdateAgent.setUpdateOnlyWifi(false);
			UmengUpdateAgent.forceUpdate(context);//手动更新
			MobclickAgent.onEvent(context, "clickupdate");	
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(context);
        XmlDB.getInstance(context).saveKey("go_stop", true);
    }
	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
	    try {
	        PackageManager manager = this.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	        String version = info.versionName;
	        return version;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return null;
	}

}
