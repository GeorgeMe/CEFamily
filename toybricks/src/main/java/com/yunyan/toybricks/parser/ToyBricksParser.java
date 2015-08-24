package com.yunyan.toybricks.parser;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;

import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunyan.toybricks.bean.ToyBricks;
import com.yunyan.toybricks.utils.FileUtils;

public class ToyBricksParser extends BaseParser<ToyBricks> {

	@Override
	public ToyBricks parseJSON(String paramString) throws JSONException,IOException {
        if (!TextUtils.isEmpty(checkResponse(paramString))){
            Gson gson=new Gson();
            saveResponse(paramString);
            return gson.fromJson(checkResponse(paramString),new TypeToken<ToyBricks>() {}.getType());
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
                //Logger.d("卡萌数据处理成json：",result);
                return result;
            } else {
                return null;
            }

        }
	}

	@Override
	public void saveResponse(String paramString) throws JSONException,IOException {
		// TODO Auto-generated method stub
        File file=new File(Environment.getExternalStorageDirectory() +"/.toybricks/"+"toybricks.json");
        FileUtils.writeFile(checkResponse(paramString), file);
	}
	

}
