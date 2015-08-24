package com.yunyan.toybricks.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.File;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;

/**
 * Created by George on 2015/4/3.
 */
public class ZipUtil {

    private static final String password = "password";

    public static void unZipFileWithProgress(final String extractPath, final File zipFile, final Handler handler,final boolean isDeleteZip) throws ZipException{
    	
        File extract=new File(extractPath);
        if (extract.isDirectory()&&!extract.exists()){
            extract.mkdirs();
        }
        final ZipFile file=new ZipFile(zipFile);
        file.setFileNameCharset("GBK");
        if (!file.isValidZipFile()){
            throw new ZipException("ZipException");
        }
        if (file.isEncrypted()){
            file.setPassword(password);
        }
        
        final ProgressMonitor progressMonitor=file.getProgressMonitor();
        final Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle=null;
                Message message=null;
                try {
                    int precentDone = 0;
                    boolean zip=false;
                    if (handler == null){
                        return;
                    }
                    handler.sendEmptyMessage(Constant.Z_START);
                    while (true){
                        Thread.sleep(50);
                        precentDone=progressMonitor.getPercentDone();
                        bundle=new Bundle();
                        bundle.putInt(Constant.PERCENT,precentDone);
                        message=new Message();
                        message.what=Constant.Z_HANDLING;
                        message.setData(bundle);
                        handler.sendMessage(message);
                        if (precentDone>=90){
                        	zip=true;
                        }
                        if(zip){
                        	if(precentDone==0){
                                handler.sendEmptyMessage(Constant.Z_COMPLETED);
                                zip=false;
                                break;
                        	}
                        }
                      //  Logger.d("解压进度：", "---："+precentDone);
                    }
                }catch (InterruptedException e){
                    bundle=new Bundle();
                    bundle.putString(Constant.ERROR_COM,e.getMessage());
                    message=new Message();
                    message.what=Constant.Z_ERROR;
                    message.setData(bundle);
                    handler.sendMessage(message);
                    e.printStackTrace();
                }finally {
                    if (isDeleteZip){
                        zipFile.delete();
                    }
                }
            }
        });
        thread.start();
        file.setRunInThread(true);
        file.extractAll(extractPath);
    }
}
