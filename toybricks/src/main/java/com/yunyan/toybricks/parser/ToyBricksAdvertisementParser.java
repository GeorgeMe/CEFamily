package com.yunyan.toybricks.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunyan.toybricks.bean.ToyBricksAdvertisement;
import com.yunyan.toybricks.utils.FileUtils;

/**
 * Created by George on 2015/3/27.
 */
public class ToyBricksAdvertisementParser extends BaseParser<ArrayList<ToyBricksAdvertisement>> {
    @Override
    public ArrayList<ToyBricksAdvertisement> parseJSON(String paramString) throws JSONException ,IOException {
        if (!TextUtils.isEmpty(checkResponse(paramString))){
            Gson gson=new Gson();
            saveResponse(paramString);
            return gson.fromJson(checkResponse(paramString),new TypeToken<ArrayList<ToyBricksAdvertisement>>() {}.getType());
        }
        return null;
    }

    @Override
    public String checkResponse(String paramString) throws JSONException {
        if (paramString == null) {
            return null;
        } else {
            String result = paramString;
            if (result != null && !result.equals("error")) {
            	//Logger.d("广告返回数据处理成json：",result);
                return result;
            } else {
                return null;
            }

        }
    }

    @Override
    public void saveResponse(String paramString) throws JSONException,IOException {
        File file=new File(Environment.getExternalStorageDirectory() +"/.toybricks/"+"advertisement.json");
        FileUtils.writeFile(checkResponse(paramString),file);
    }
}
