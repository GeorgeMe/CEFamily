package com.yunyan.toybricks.downloader.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yunyan.toybricks.bean.FileInfo;
import com.yunyan.toybricks.bean.ThreadInfo;
import com.yunyan.toybricks.downloader.db.IThreadDAO;
import com.yunyan.toybricks.downloader.db.ImpThreadDAO;
import com.yunyan.toybricks.downloader.util.BufferedRandomAccessFile;
import com.yunyan.toybricks.downloader.util.Const;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 下载任务类，启动多线程进行文件的下载
 *
 * @author gzc
 *
 */
public class DownloadTask {

	private Context mContext = null;
	private FileInfo mFileInfo = null;
	private IThreadDAO mDAO = null;
	private int mFinished;
	// 下载该文件的线程的数量
	private int mThreadCount;
	private List<DownloadThread> mDownloadThreads = new ArrayList<DownloadThread>();

	/**
	 * 线程池
	 */
	public static final ExecutorService sExecutorService = Executors.newCachedThreadPool();

	/**
	 * 是否停止下载
	 */
	public boolean isPause = false;

	public DownloadTask(Context context, FileInfo fileInfo, int threadCount) {
		this.mContext = context;
		this.mFileInfo = fileInfo;
		Log.e("DownloadTask构造函数","文件ID=" + mFileInfo.getId());

		mDAO = new ImpThreadDAO(context);
		mFinished = 0;
		mThreadCount = threadCount;
	}

	/**
	 * 开启多个线程下载该文件
	 */
	public void download() {
		// 读取数据库该下载文件URL的所有线程信息
		List<ThreadInfo> threadInfos = mDAO.getThreads(mFileInfo.getUrl());
		// 如果数据库中没有该下载文件URL的线程下载信息，初始化线程下载信息
		if (0 == threadInfos.size()) {
			// 获得每个线程的下载文件字节范围
			int length = mFileInfo.getLength() / mThreadCount;
			for (int i = 0; i < mThreadCount; i++) {
				ThreadInfo threadInfo = new ThreadInfo(i, mFileInfo.getUrl(), i * length, (i + 1) * length - 1, 0);
				// 最后一个线程除不尽的情况，直接是下载完文件
				if (i == mThreadCount - 1) {
					threadInfo.setEnd(mFileInfo.getLength());
				}
				// 添加到集合中
				threadInfos.add(threadInfo);
				// 向数据库插入线程信息
				mDAO.insertThread(threadInfo);
			}
		}

		// 开启多个线程下载该文件
		for(int i = 0; i < threadInfos.size(); i++){
			DownloadThread downloadThread = new DownloadThread(threadInfos.get(i));
			mDownloadThreads.add(downloadThread);
//			downloadThread.start();
			// 从线程池中启动线程
			sExecutorService.execute(downloadThread);
		}
	}

	/**
	 * 检查是否所有线程下载完毕，若是则整个文件下载完成，发送广播通知UI更新，该文件的下载任务完成
	 */
	private synchronized void checkAllThreadsFinished(){
		boolean allFinished = true;
		// 遍历检查是否所有线程下载完成
		for(DownloadThread downloadThread : this.mDownloadThreads){
			if(!downloadThread.mIsDownloadFinished){
				allFinished = false;
				break;
			}
		}
		if(allFinished){
			Intent intent = new Intent(DownloadService.ACTION_FINISH);
			intent.putExtra(Const.FILE_INFO_KEY, this.mFileInfo);
			// 发送广播通知UI更新，该文件的下载任务完成
			this.mContext.sendBroadcast(intent);
			Log.e("DownloadTask", mFileInfo.getFileName() + "下载完成，发送广播通知UI更新");
			// 根据下载文件URL删掉对应所有的下载线程数据库信息
			mDAO.deleteThread(mFileInfo.getUrl());
		}
	}


	// ========================================

	/**
	 * 内部类，下载线程
	 *
	 * @author gzc
	 *
	 */
	class DownloadThread extends Thread {

		private ThreadInfo mThreadInfo = null;
		private boolean mIsDownloadFinished;

		public DownloadThread(ThreadInfo threadInfo) {
			super();
			this.mThreadInfo = threadInfo;
			mIsDownloadFinished = false;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null;
			RandomAccessFile raf = null;
			InputStream is = null;
			BufferedInputStream bis = null;
			try {
				URL url = new URL(mThreadInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(3 * 1000);
				conn.setRequestMethod("GET");
				// 设置下载位置，该线程下载文件的从多少字节开始，到多少字节为止
				int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
				// 设置该线程的下载范围
				conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd());

				Log.e("DownloadThread", mThreadInfo.toString());

				// 设置写入位置
				File file = new File(com.yunyan.toybricks.downloader.service.DownloadService.DOWNLOAD_PATH,mFileInfo.getFileName());
				// 使用自定义的，继承RandomAccessFile类的带缓存区的BufferedRandomAccessFile类，提高IO速度。
				raf = new BufferedRandomAccessFile(file, "rwd");	//new RandomAccessFile(file, "rwd");
				// 跳过多少字节
				raf.seek(start);

				mFinished += mThreadInfo.getFinished();

				// 开始下载，这里不能是200，因为这里是对文件的Range下载
				if (206 == conn.getResponseCode()) {
					Intent intent = new Intent(com.yunyan.toybricks.downloader.service.DownloadService.ACTION_UPDATE);

					// 从网络读取文件的数据
					is = conn.getInputStream();
					bis = new BufferedInputStream(is);
					byte[] buffer = new byte[8 * 1024];
					int len = -1;
					long time = System.currentTimeMillis();

					while ((len = bis.read(buffer, 0, buffer.length)) > 0) {
						// 写入本地文件
						raf.write(buffer, 0, len);
						// 累加整个文件下载完成的进度
						mFinished += len;
						// 累加每个线程完成的进度
						mThreadInfo.setFinished(mThreadInfo.getFinished() + len);

						// 每格1秒才发一次广播，更新UI
						if (System.currentTimeMillis() - time > (1 * 1000) ) {
							// 把下载进度(百分比)，发送广播给activity更新进度条UI
							intent.putExtra(Const.FINISHED_KEY, 100 * mFinished / mFileInfo.getLength());
							intent.putExtra(Const.ID_KEY, mFileInfo.getId());
							// 发送广播
							mContext.sendBroadcast(intent);
							Log.e("DownloadTask", "发送广播,下载文件ID="+mFileInfo.getId() + "下载进度为"+100 * mFinished / mFileInfo.getLength());
						}
						// 模拟网速很慢
						Thread.sleep(500);

						if (isPause) {
							// 在下载暂停时，保存下载进度到数据库
							mDAO.updateThread(mThreadInfo.getUrl(),mThreadInfo.getId(),mThreadInfo.getFinished());
							Log.e("DownloadThread","保存下载进度：" + mThreadInfo.toString());
							// 跳出循环，结束函数，结束线程，来暂停下载
							return; // break;
						}
					} // end while

					// while执行完毕，说明该线程的下载任务已经完成
					mIsDownloadFinished = true;

//					检查是否所有线程下载完毕
					checkAllThreadsFinished();
				} else {
					Intent intent = new Intent(com.yunyan.toybricks.downloader.service.DownloadService.ACTION_EXCEPTION);
					intent.putExtra(Const.EXCEPTION_KEY, "CONNECT_OPEN_FAILD");
					intent.putExtra(Const.ID_KEY, mFileInfo.getId());
					// 发送广播
					mContext.sendBroadcast(intent);
					Log.e("DownloadThread", "打开网页失败");
				}

			}catch (Throwable e) {
				if (e instanceof ConnectException) {
					Intent intent = new Intent(com.yunyan.toybricks.downloader.service.DownloadService.ACTION_EXCEPTION);
					intent.putExtra(Const.EXCEPTION_KEY, "ERROR_CONNECTEXCEPTION");
					intent.putExtra(Const.ID_KEY, mFileInfo.getId());
					// 发送广播
					mContext.sendBroadcast(intent);
					//Log.e(TAG, "连接异常", e);
				} else if (e instanceof FileNotFoundException) {
					Intent intent = new Intent(com.yunyan.toybricks.downloader.service.DownloadService.ACTION_EXCEPTION);
					intent.putExtra(Const.EXCEPTION_KEY, "ERROR_FILENOTFOUND");
					intent.putExtra(Const.ID_KEY, mFileInfo.getId());
					// 发送广播
					mContext.sendBroadcast(intent);
					//Log.e(TAG, "文件路径错误", e);
				} else if (e instanceof SocketTimeoutException) {
					Intent intent = new Intent(com.yunyan.toybricks.downloader.service.DownloadService.ACTION_EXCEPTION);
					intent.putExtra(Const.EXCEPTION_KEY, "ERROR_SOCKETTIMEOUT");
					intent.putExtra(Const.ID_KEY, mFileInfo.getId());
					// 发送广播
					mContext.sendBroadcast(intent);
					//Log.e(TAG, "连接超时", e);
				} else if (e instanceof Exception){
					Intent intent = new Intent(com.yunyan.toybricks.downloader.service.DownloadService.ACTION_EXCEPTION);
					intent.putExtra(Const.EXCEPTION_KEY, "UNKNOWN ERROR");
					intent.putExtra(Const.ID_KEY, mFileInfo.getId());
					// 发送广播
					mContext.sendBroadcast(intent);
					//Log.e(TAG, "未知错误", e);
				}
			}finally {
				// 释放资源
				try {
					if (null != bis) {
						bis.close();
					}
					if (null != is) {
						is.close();
					}
					if (null != raf) {
						raf.close();
					}
				} catch (IOException e) {
					Intent intent = new Intent(com.yunyan.toybricks.downloader.service.DownloadService.ACTION_EXCEPTION);
					intent.putExtra(Const.EXCEPTION_KEY, e.getMessage());
					intent.putExtra(Const.ID_KEY, mFileInfo.getId());
					// 发送广播
					mContext.sendBroadcast(intent);
					e.printStackTrace();
				}
				conn.disconnect();
			}
		} // end run( )

	} // end class DownloadThread

} // end class DownloadTask
