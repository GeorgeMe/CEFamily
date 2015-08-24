package com.yunyan.toybricks.vo;
import java.util.HashMap;

import com.yunyan.toybricks.parser.BaseParser;

import android.content.Context;

/**
 * Created by George on 2015/3/27.
 */
public class RequestVo {
    public int requestUrl;
    public Context context;
    public HashMap<String, String> requestDataMap;
    public BaseParser<?> jsonParser;

    public RequestVo() {
    }

    public RequestVo(int requestUrl, Context context, HashMap<String, String> requestDataMap, BaseParser<?> jsonParser) {
        super();
        this.requestUrl = requestUrl;
        this.context = context;
        this.requestDataMap = requestDataMap;
        this.jsonParser = jsonParser;
    }
}
