package com.yunyan.toybricks.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.yunyan.toybricks.utils.Constant;
import com.yunyan.toybricks.utils.ResourcesUtils;

public class ToyBricksSharePopupWindow extends PopupWindow implements OnClickListener {
	
    private UMSocialService mController = UMServiceFactory.getUMSocialService(Constant.DESCRIPTOR);
    private Activity mActivity;
    private ImageButton ib_qq,ib_tx_wb,ib_sina,ib_qzone,ib_wechat,ib_wechat_circle;
    Bitmap bitmap;
    public ToyBricksSharePopupWindow(Activity activity,Bitmap bitmap) {
        super(activity);
        this.mActivity = activity;
        this.bitmap=bitmap;
        initView(activity);
        setShareContent();
    }

    @SuppressWarnings("deprecation")
    private void initView(Context context) {
    	
        View rootView = LayoutInflater.from(context).inflate(ResourcesUtils.getLayout(mActivity, "toybricks_share_popupwindow"), null);
        rootView.findViewById(ResourcesUtils.getId(mActivity, "qq")).setOnClickListener(this);
        rootView.findViewById(ResourcesUtils.getId(mActivity, "tx_wb")).setOnClickListener(this);
        rootView.findViewById(ResourcesUtils.getId(mActivity, "sina")).setOnClickListener(this);
        rootView.findViewById(ResourcesUtils.getId(mActivity, "qzone")).setOnClickListener(this);
        rootView.findViewById(ResourcesUtils.getId(mActivity, "wechat")).setOnClickListener(this);
        rootView.findViewById(ResourcesUtils.getId(mActivity, "wechat_circle")).setOnClickListener(this);
        setContentView(rootView);
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        
        ib_qq=(ImageButton) rootView.findViewById(ResourcesUtils.getId(mActivity, "qq"));
        ib_tx_wb=(ImageButton) rootView.findViewById(ResourcesUtils.getId(mActivity, "tx_wb"));
        ib_sina=(ImageButton) rootView.findViewById(ResourcesUtils.getId(mActivity, "sina"));
        ib_qzone=(ImageButton) rootView.findViewById(ResourcesUtils.getId(mActivity, "qzone"));
        ib_wechat=(ImageButton) rootView.findViewById(ResourcesUtils.getId(mActivity, "wechat"));
        ib_wechat_circle=(ImageButton) rootView.findViewById(ResourcesUtils.getId(mActivity, "wechat_circle"));
        
    }

	@Override
	public void onClick(View v) {
		if (v == ib_qq) {
			performShare(SHARE_MEDIA.QQ);//返回有问题
            dismiss();
		} else if (v == ib_tx_wb) {
			performShare(SHARE_MEDIA.TENCENT);
            dismiss();
		} else if (v == ib_sina) {
			performShare(SHARE_MEDIA.SINA);
            dismiss();
		} else if (v == ib_qzone) {
			performShare(SHARE_MEDIA.QZONE);//返回有问题
            dismiss();
		} else if (v == ib_wechat) {
			performShare(SHARE_MEDIA.WEIXIN);
            dismiss();
		} else if (v == ib_wechat_circle) {
			performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
            dismiss();
		}
	}
    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    private void setShareContent() {
    	//Fill 签名：   2f42b22c8fff34f14395a8b10f2597b2  微信 sina微博
        UMImage fillImage = new UMImage(mActivity, bitmap);  
    	
    	//1、 设置微信分享的内容
    	
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareMedia(fillImage);
        mController.setShareMedia(weixinContent);

        //2、 设置微信朋友圈分享的内容
        
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareImage(fillImage);
        mController.setShareMedia(circleMedia);
        
        //3、 设置QQ空间分享内容
        
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(Constant.WE_SHARECONTENT+Constant.WE_WEB);
        qzone.setTargetUrl(Constant.WE_WEB);
        qzone.setTitle(Constant.WE_TITLE);
        qzone.setShareImage(fillImage);
        mController.setShareMedia(qzone);
        
        //4、 设置QQ分享内容
        
        QQShareContent qqShareContent = new QQShareContent();

        qqShareContent.setShareImage(fillImage);
        mController.setShareMedia(qqShareContent);
    	
        //5、6、两个微博分享的内容
    	mController.setShareContent(Constant.WE_SHARECONTENT+Constant.WE_WEB);
    	//5、6、两个微博分享的图片
    	mController.setShareMedia(fillImage);
        
    }
    private void performShare(SHARE_MEDIA platform) {
    	
        mController.postShare(mActivity, platform, new SnsPostListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                String showText = platform.toString();
                
                if (eCode == StatusCode.ST_CODE_SUCCESSED) {
                    showText += "平台分享成功";
                } else if (eCode == StatusCode.ST_CODE_ERROR_CANCEL) {
                    showText += "平台分享取消";
                } else{
                	showText += "平台分享失败";
                }
                Toast.makeText(mActivity, showText, Toast.LENGTH_SHORT).show();
            }
            
        });
        
    }
    
}
