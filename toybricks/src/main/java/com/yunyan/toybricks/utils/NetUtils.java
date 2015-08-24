package com.yunyan.toybricks.utils;

import com.yunyan.toybricks.vo.RequestVo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by George on 2015/3/27.
 */

public class NetUtils {

    private static final String TAG = "NetUtils";

    public static Object post(RequestVo vo){
        DefaultHttpClient client=new DefaultHttpClient();
        String url = vo.context.getString(vo.requestUrl);
        //LogUtils.d(TAG, "Post " + url);
        HttpPost post = new HttpPost(url);
        Object obj = null;
        try {
            if (vo.requestDataMap != null) {
                HashMap<String, String> map = vo.requestDataMap;
                ArrayList<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                    pairList.add(pair);
                }
                HttpEntity entity = new UrlEncodedFormEntity(pairList, "UTF-8");
                
                post.setEntity(entity);
               // LogUtils.d(TAG, "Pt " + post.getURI().toString() +"  "+post.getEntity().toString() );
            }
            //TODO 读取本地json
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
               // LogUtils.d(TAG, result);
                try {
                       obj = vo.jsonParser.parseJSON(result);
                } catch (JSONException e) {
                   // Logger.e(TAG, e.getLocalizedMessage(), e);
                }
                return obj;
            }
        } catch (ClientProtocolException e) {
            //Logger.e(TAG, e.getLocalizedMessage(), e);
        } catch (IOException e) {
           // Logger.e(TAG, e.getLocalizedMessage(), e);
        }
        return null;
    }

    public static Object get(RequestVo vo) {
        DefaultHttpClient client = new DefaultHttpClient();
        String url = vo.context.getString(vo.requestUrl);
       // LogUtils.d(TAG, "Get " + url);
        HttpGet get = new HttpGet(url);
        Object obj = null;
        try {
            //TODO 读取本地json
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                //LogUtils.d(TAG, result);
                try {
                    obj = vo.jsonParser.parseJSON(result);
                } catch (JSONException e) {
                   // Logger.e(TAG, e.getLocalizedMessage(), e);
                }
                return obj;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
