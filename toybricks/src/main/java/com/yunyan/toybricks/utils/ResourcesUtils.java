/**
 * Project Name:CeCe
 * File Name:ResourcesUtils.java
 * Package Name:com.yunyan.kameng
 * Date:2014年11月6日上午11:09:43
 * Copyright (c) 2014, qizhi_china@163.com All Rights Reserved.
 *
*/

package com.yunyan.toybricks.utils;

import android.content.Context;
import android.content.res.Resources;

/**
 * ClassName:ResourcesUtils 
 * Date:     2014年11月6日 上午11:09:43 
 * @author   ZoZo
 * @version  版本号
 * @since    JDK 1.7
 * @see 	 
 */
public class ResourcesUtils {
	
	private static Resources resources;
	
	public static int getLayout(Context context,String name){
		resources=context.getResources();
		int layout=resources.getIdentifier(name, "layout", context.getPackageName());
		return layout;
	}
	
	public static int getId(Context context,String name){
		resources=context.getResources();
		int id=resources.getIdentifier(name, "id", context.getPackageName());
		return id;
	}
	
	
	public static int getDrawable(Context context,String name){
		resources=context.getResources();
		int drawable=resources.getIdentifier(name, "drawable", context.getPackageName());
		return drawable;
	}
	
	
	public static int getDimen(Context context,String name){
		resources=context.getResources();
		int dimen=resources.getIdentifier(name, "dimen", context.getPackageName());
		return dimen;
	}
	
	
	public static int getColor(Context context,String name){
		resources=context.getResources();
		int color=resources.getIdentifier(name, "color", context.getPackageName());
		return color;
	}
	
	public static int getAnim(Context context,String name){
		resources=context.getResources();
		int anim=resources.getIdentifier(name, "anim", context.getPackageName());
		return anim;
	}
	
	public static int getRaw(Context context,String name){
		resources=context.getResources();
		int raw=resources.getIdentifier(name, "raw", context.getPackageName());
		return raw;
	}	
	public static int getString(Context context,String name){
		resources=context.getResources();
		int raw=resources.getIdentifier(name, "string", context.getPackageName());
		return raw;
	}	
}

