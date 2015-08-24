package com.yunyan.toybricks.utils;

import java.io.File;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

public class MemoryUtils {
	
	/**
	 * 获得SD卡总大小
	 * 
	 * @return
	 */
	public static String getSDTotalSize(Context context) {
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return Formatter.formatFileSize(context, blockSize * totalBlocks);
		}else{
			return null;
		}
		 
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String getSDAvailableSize(Context context) {
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			if(android.os.Build.VERSION.SDK_INT>17){
				return Formatter.formatFileSize(context, (stat.getAvailableBytes()));
			}else{
				return Formatter.formatFileSize(context, blockSize * availableBlocks);
			}
		}else{
			return null;
		}

	}
	/**
	 * 获得sd卡剩余容量，即可用大小
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	public static boolean isSDAvailableSize(Context context) {
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			if(android.os.Build.VERSION.SDK_INT>17){
				return stat.getAvailableBytes()>(150*1024*1024);
			}else{
				return (blockSize * availableBlocks)>(150*1024*1024);
			}
		}else{
			return false;
		}

	}
	/**
	 * 获得机身内存总大小
	 * 
	 * @return
	 */
	public static String getRomTotalSize(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize * totalBlocks);
	}

	/**
	 * 获得机身可用内存
	 * 
	 * @return
	 */
	public static String getRomAvailableSize(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(context, blockSize * availableBlocks);
	}

}
