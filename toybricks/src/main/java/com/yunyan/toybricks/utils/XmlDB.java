package com.yunyan.toybricks.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import java.io.File;
import java.lang.reflect.Field;
/**
 * 
 * @ClassName XmlDB 
 * @Author    ZoZo
 * @Des       XmlDB这个类的描述：
 *	
 * @Date      2015年2月3日 下午7:33:49 
 * @Since     JDK 1.7
 * @Version
 */
public class XmlDB {

	private Context context;
	private SharedPreferences mSharePrefs = null;
	private SharedPreferences.Editor editor;
	/**
	 * Preferences Name that we use.
	 */
	public static final String Pref_Name = "yunyan_toybricks_config";

	/**
	 * Holds the single instance that is shared by the process.
	 */
	private static XmlDB sInstance;

	/**
	 * Return the single SharedPreferences instance.
	 */
	public static XmlDB getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new XmlDB(context);
			sInstance.open();
		}
		return sInstance;
	}

	public XmlDB(Context context) {
		this.context = context;
	}

	/**
	 * Get a sharePreference instance.
	 */
	public void open() {
		mSharePrefs = context.getSharedPreferences(Pref_Name, 0);
	}

	public void close() {
		sInstance = null;
		mSharePrefs = null;
	}

	/**
	 * Save the String-String key-values in sharePreference file.
	 * 

	 */
	public void saveKey(String mKey, String mValue) {
		if (mSharePrefs != null) {
			editor = mSharePrefs.edit();
			editor.putString(mKey, mValue);
			editor.commit();
		}
	}

	public String getKeyString(String mKey, String mDefValue) {
		String mStr = null;
		if (mSharePrefs != null) {
			mStr = mSharePrefs.getString(mKey, mDefValue);
		}
		return mStr;
	}

	public String getKeyStringValue(String mKey, String mDefValue) {
		String mStr = null;
		if (mSharePrefs != null) {
			mStr = mSharePrefs.getString(mKey, mDefValue);
		}
		return mStr;
	}

	public int getKeyIntValue(String mKey, int mDefValue) {
		int mInt = 0;
		if (mSharePrefs != null) {
			mInt = mSharePrefs.getInt(mKey, mDefValue);
		}
		return mInt;
	}

	public boolean getKeyBooleanValue(String mKey, boolean mDefValue) {
		boolean mBool = false;
		if (mSharePrefs != null) {
			mBool = mSharePrefs.getBoolean(mKey, mDefValue);
		}
		return mBool;
	}

	public Float getKeyFloatValue(String mKey, int mDefValue) {
		Float mFloat = null;
		if (mSharePrefs != null) {
			mFloat = mSharePrefs.getFloat(mKey, mDefValue);
		}
		return mFloat;
	}

	public long getKeyLongValue(String mKey, long mDefValue) {
		long mFloat = 0L;
		if (mSharePrefs != null) {
			mFloat = mSharePrefs.getLong(mKey, mDefValue);
		}
		return mFloat;
	}

	/** 保存整型的键值对到配置文件当中. */
	public void saveKey(String mKey, int mValue) {
		if (mSharePrefs != null) {
			editor = mSharePrefs.edit();
			editor.putInt(mKey, mValue);
			editor.commit();
		}
	}

	/** 保存boolean类型的键值对到配置文件当中. */
	public void saveKey(String mKey, boolean mValue) {
		if (mSharePrefs != null) {
			editor = mSharePrefs.edit();
			editor.putBoolean(mKey, mValue);
			editor.commit();
		}
	}

	/** 保存float类型的键值对到配置文件当中. */
	public void saveKey(String mKey, Float mValue) {
		if (mSharePrefs != null) {
			editor = mSharePrefs.edit();
			editor.putFloat(mKey, mValue);
			editor.commit();
		}
	}

	/** 保存long类型的键值对到配置文件当中 */
	public void saveKey(String mKey, long mValue) {
		if (mSharePrefs != null) {
			editor = mSharePrefs.edit();
			editor.putLong(mKey, mValue);
			editor.commit();
		}
	}

	public void removeKey(String key) {
		if (mSharePrefs != null) {
			editor = mSharePrefs.edit();
			editor.remove(key);
			editor.commit();
		}
	}
	/**
	 * 清除 sf 文件的所有数据， 注意  在ondestory 方法中使用，不要再同一个页面中使用
	 *                    在同一个页面中不能销毁所有的数据    
	 * @Title: clearALlData 
	 * @Description: TODO
	 * @return: void
	 */
	public void clearALlData(){
		if (mSharePrefs != null) {
			 mSharePrefs.edit().clear().commit();
		}
	}
	@Override
	protected void finalize() throws Throwable {
		sInstance.close();
		super.finalize();
	}
	private void savePreToSDcard(Context context) {  
        try {  
            Field field;  
            // 获取ContextWrapper对象中的mBase变量。该变量保存了ContextImpl对象  
            field = ContextWrapper.class.getDeclaredField("mBase");  
            field.setAccessible(true);  
            // 获取mBase变量  
            Object obj = field.get(this);  
            // 获取ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径  
            field = obj.getClass().getDeclaredField("mPreferencesDir");  
            field.setAccessible(true);  
            // 创建自定义路径  
            File file = context.getExternalFilesDir("xml");  
            // 修改mPreferencesDir变量的值  
            field.set(obj, file);  
            SharedPreferences mySharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);  
            SharedPreferences.Editor editor = mySharedPreferences.edit();  
            editor.putString("name", "20130310");  
            editor.commit();  
        } catch (NoSuchFieldException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IllegalArgumentException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IllegalAccessException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }
}