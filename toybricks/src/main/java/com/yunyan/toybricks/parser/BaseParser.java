package com.yunyan.toybricks.parser;

import org.json.JSONException;
import java.io.IOException;

/**
 * Created by George on 2015/3/27.
 */
public abstract class BaseParser<T> {

    public abstract T parseJSON(String paramString) throws JSONException,IOException ;

    /**
     *
     * @param paramString
     * @throws org.json.JSONException
     */
    public abstract String checkResponse(String paramString) throws JSONException;

    public abstract void saveResponse(String paramString)  throws JSONException,IOException;
}
