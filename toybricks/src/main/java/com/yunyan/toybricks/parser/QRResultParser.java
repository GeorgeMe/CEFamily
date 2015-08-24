package com.yunyan.toybricks.parser;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.yunyan.toybricks.bean.QRResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by George on 2015/4/1.
 */
public class QRResultParser extends BaseParser<QRResult> {
    @Override
    public QRResult parseJSON(String paramString) throws JSONException, IOException  {
        if (!TextUtils.isEmpty(checkResponse(paramString))){
            Gson gson=new Gson();
            return gson.fromJson(checkResponse(paramString),QRResult.class);
        }
        return null;
    }

    @Override
    public String checkResponse(String paramString) throws JSONException {
        if (paramString == null) {
            return null;
        } else {
        	JSONObject jsonObject = new JSONObject(paramString);
        	String qr_json = jsonObject.getString("qr_json");
            if (qr_json != null && !qr_json.equals("error")) {
                return qr_json;
            } else {
                return null;
            }
        }
    }

    @Override
    public void saveResponse(String paramString) throws JSONException, IOException {
    }
}
