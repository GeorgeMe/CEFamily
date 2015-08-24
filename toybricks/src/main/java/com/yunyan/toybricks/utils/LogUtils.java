/**
 * Project Name:CoCo
 * File Name:LogUtils.java
 * Package Name:com.dream.coco.utils
 * Date:2014年10月1日下午10:37:45
 * Copyright (c) 2014, qizhi_china@163.com All Rights Reserved.
 *
*/
package com.yunyan.toybricks.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;
/**
 * 
 * @ClassName LogUtils 
 * @Author    George
 * @Des       LogUtils这个类的描述：
 *            有可控开关的日志调试,可将日志文件输出(LOG_SWITCH：日志文件总开关,LOG_WRITE_TO_FILE：日志写入文件开关)
 * @Date      2014年10月2日 上午2:03:01 
 * @Since     JDK 1.7
 * @Version   1.0
 */
@SuppressLint("SimpleDateFormat")
public class LogUtils {

	private static Boolean LOG_SWITCH=true;        // 日志文件总开关
	private static Boolean LOG_WRITE_TO_FILE=true; // 日志写入文件开关
	private static char LOG_TYPE='v';              // 输入日志类型，w代表只输出告警信息等，v代表输出所有信息
	private static String LOG_PATH_SDCARD_DIR="/kamengapplog/";  // 日志文件路径
	private static int SDCARD_LOG_FILE_SAVE_DAYS = 0;// 日志文件的最多保存天数
	private static String LOG_FILENAME = "_log.txt";  // 本类输出的日志文件名称
	private static SimpleDateFormat LOG_SDF_M = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 用于日志的输出
	private static SimpleDateFormat LOG_SDF_F = new SimpleDateFormat("yyyy_MM_dd");// 用于日志文件名

	/*-------(String tag, Object msg)---------*/
	public static void w(String tag, Object msg) {
		log(tag, msg.toString(), 'w');// 警告信息
	}

	public static void e(String tag, Object msg) {
		log(tag, msg.toString(), 'e');// 错误信息
	}

	public static void d(String tag, Object msg) {
		log(tag, msg.toString(), 'd');// 调试信息
	}

	public static void i(String tag, Object msg) {
		log(tag, msg.toString(), 'i');// 信息输出
	}

	public static void v(String tag, Object msg) {
		log(tag, msg.toString(), 'v');// 输出全部
	}

	/*------(String tag, String text)--------*/
	public static void w(String tag, String text) {
		log(tag, text, 'w');// 警告信息
	}
	
	public static void e(String tag, String text) {
		log(tag, text, 'e');// 错误信息
	}
	
	public static void d(String tag, String text) {
		log(tag, text, 'd');// 调试信息
	}
	
	public static void i(String tag, String text) {
		log(tag, text, 'i');// 信息输出
	}
	
	public static void v(String tag, String text) {
		log(tag, text, 'v');// 输出全部
	}

	/**
	 * 
	 * @author George
	 * log()的功能描述：
	 * 根据tag, msg和等级，把日志写入文件
	 * @param tag
	 * @param msg
	 * @param level
	 * 
	 */
	private static void log(String tag, String msg, char level) {
		if (LOG_SWITCH) {
			if ('e' == level && ('e' == LOG_TYPE || 'v' == LOG_TYPE)) {
				Log.e(tag, msg); // 输出错误信息
			} else if ('w' == level && ('w' == LOG_TYPE || 'v' == LOG_TYPE)) {
				Log.w(tag, msg); // 输出警告信息
			} else if ('d' == level && ('d' == LOG_TYPE || 'v' == LOG_TYPE)) {
				Log.d(tag, msg); // 输出调试信息
			} else if ('i' == level && ('d' == LOG_TYPE || 'v' == LOG_TYPE)) {
				Log.i(tag, msg); // 输出信息输出
			} else {
				Log.v(tag, msg); // 输出输出全部
			}
			if (LOG_WRITE_TO_FILE){
				writeLogtoFile(String.valueOf(level), tag, msg);//把日志写入文件
			}
		}
	}
	/**
	 * 
	 * @author George
	 * writeLogtoFile()的功能描述：
	 * 打开日志文件并写入日志
	 * @param logtype
	 * @param tag
	 * @param text
	 * 
	 */
	private static void writeLogtoFile(String logtype, String tag, String text) {// 新建或打开日志文件
		String temp="     " + logtype + "     " + tag + "     " + text+ "     ";
		Date c_logtime = new Date();
		String WriteLogFiel = LOG_SDF_F.format(c_logtime);//得到日志文件名日期部分(2014-10-1-log.txt)
		String WriteLogMessage = LOG_SDF_M.format(c_logtime) + temp;// + getLineInfo() 代码行等相关信息
		File file = new File(getLogPath(), WriteLogFiel + LOG_FILENAME);
		try {
			FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
			BufferedWriter bufWriter = new BufferedWriter(filerWriter);
			bufWriter.write(WriteLogMessage);
			bufWriter.newLine();
			bufWriter.close();
			filerWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author George
	 * delFile()的功能描述：
	 * 删除指定的日志文件
	 * 
	 */
	public static void delFile() {
		String DelLogFiel = LOG_SDF_F.format(getDateBefore());
		File file = new File(getLogPath(), DelLogFiel + LOG_FILENAME);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 
	 * @author George
	 * getDateBefore()的功能描述：
	 * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
	 * @return
	 * 
	 */
	private static Date getDateBefore() {
		Date nowtime = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowtime);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - SDCARD_LOG_FILE_SAVE_DAYS);
		return now.getTime();
	}	
	/**
	 * 
	 * @author George
	 * isFileExist()的功能描述：
	 * 判断sd卡上的文件夹是否存在
	 * @param logpath
	 * @return
	 */
	private static boolean isFileExist(String logpath) {
		File file = new File(logpath);
		return file.exists();
	}
	/**
	 * 
	 * @author George
	 * createSDDir()的功能描述：
	 * 在sd卡上创建目录。mkdir只能创建一级目录 ,mkdirs可以创建多级目录
	 * @param logpath
	 * @return
	 */
	private static File createSDDir(String logpath) {
		File dir = new File(logpath);
		dir.mkdirs();
		return dir;
	}	
	
	/**
	 * 
	 * @author George
	 * getLogPath()的功能描述：
	 * 得到日志文件保存路径(注：默认SD卡存在并把日志文件存在SD卡中。没有做SD卡不存在的判断)
	 * @return
	 * 
	 */
	private static String getLogPath(){
		String LogPath=Environment.getExternalStorageDirectory().getAbsolutePath()+LOG_PATH_SDCARD_DIR;
		if(isFileExist(LogPath)){
			return LogPath;
		}else{
			createSDDir(LogPath);
			return LogPath;
		}
		
	}
	
}
