package com.yunyan.toybricks.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;






import java.util.List;

import com.yunyan.toybricks.bean.ToyBricksAdvertisement;
import com.yunyan.toybricks.utils.Constant;
import com.yunyan.toybricks.utils.ResourcesUtils;
import com.yunyan.toybricks.utils.SyncImageLoader;

/**
 * Created by George on 2015/3/27.
 */
@SuppressWarnings("deprecation")
public class ToyBricksAdvertisementAdapter  extends BaseAdapter implements SyncImageLoader.OnImageLoadListener{

    private Context context;
    private List<ToyBricksAdvertisement> mToyBricksAdvertisemenList;
    private SyncImageLoader syncImageLoader;
    private Drawable[] drawables;

    public ToyBricksAdvertisementAdapter(Context context,List<ToyBricksAdvertisement> mToyBricksAdvertisemenList){
        this.context=context;
        this.mToyBricksAdvertisemenList=mToyBricksAdvertisemenList;
        syncImageLoader=new SyncImageLoader();
        int size=mToyBricksAdvertisemenList.size();
        drawables=new Drawable[size];
        syncImageLoader.setLoadLimit(0, size);
        for (int i = 0; i < size; i++) {
            syncImageLoader.loadImage(i, mToyBricksAdvertisemenList.get(i).getURL(), this);
        }
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mToyBricksAdvertisemenList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // TODO Auto-generated method stub
        ImageView imageView;
        if(convertView==null){
            imageView=new ImageView(context);
        }else{
            imageView=(ImageView) convertView;
        }
        Drawable drawable=drawables[position%mToyBricksAdvertisemenList.size()];
        if(drawable==null){
            imageView.setImageResource(ResourcesUtils.getDrawable(context, "ailer_list_item_loading"));
        }else{
            Gallery.LayoutParams layoutParams=new Gallery.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) ;
            imageView.setLayoutParams(layoutParams);
            imageView.setImageDrawable(drawable);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

				if(mToyBricksAdvertisemenList.get(position%mToyBricksAdvertisemenList.size()).getWangzhi()!=null&&mToyBricksAdvertisemenList.get(position%mToyBricksAdvertisemenList.size()).getWangzhi()!=""){
	                Intent intent = new Intent();
	                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
	                intent.setData(Uri.parse(mToyBricksAdvertisemenList.get(position%mToyBricksAdvertisemenList.size()).getWangzhi()));
	                intent.setAction(Intent.ACTION_VIEW);       
					PackageManager pm = context.getPackageManager();
					List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
					if(list.size()>0){
						if(Constant.downloadflag){
							Toast.makeText(context, "有任务在下载哟，等一下先", Toast.LENGTH_SHORT).show();
						}else{
							context.startActivity(intent); 
						}
					}else{
						Toast.makeText(context, "设备或应用程序未就绪,详询客服", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(context, "设备或应用程序未就绪,详询客服", Toast.LENGTH_SHORT).show();
				}
            }
        });
        return imageView;
    }

    @Override
    public void onImageLoad(Integer t, Drawable drawable) {
        // TODO Auto-generated method stub
        this.drawables[t]=drawable;
        notifyDataSetChanged();
    }

    @Override
    public void onError(Integer t) {
        // TODO Auto-generated method stub
        Toast.makeText(context, "图片加载失败", Toast.LENGTH_SHORT).show();
    }

}
