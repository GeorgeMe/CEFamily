package com.yunyan.mall.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yunyan.mall.R;
import com.yunyan.mall.entities.Goods;
import com.yunyan.mall.utils.RecyclerViewClickListener;

import java.util.List;

/**
 * Created by George on 2015/8/14.
 */
public class GoodsAdapter extends RecyclerView.Adapter<GoodsHolder> {
    private Context context;
    private List<Goods> mGoodsList;
    private RecyclerViewClickListener mRecyclerClickListener;

    public GoodsAdapter(List<Goods> mGoodsList){
        this.mGoodsList=mGoodsList;
    }
    public void setRecyclerListListener(RecyclerViewClickListener mRecyclerClickListener) {
        this.mRecyclerClickListener = mRecyclerClickListener;
    }

    @Override
    public int getItemCount() {
        return mGoodsList.size();
    }

    @Override
    public void onBindViewHolder(GoodsHolder goodsHolder, final int i) {
        Goods seletedgoods=mGoodsList.get(i);
        goodsHolder.goods_name.setText(seletedgoods.getGoods_Name());
        Picasso.with(context)
                .load(seletedgoods.getGoods_Img())
                .fit().centerCrop()
                .into(goodsHolder.goods_img, new Callback() {
                    @Override
                    public void onSuccess() {

                        //mGoodsList.get(i).setMovieReady(true);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public GoodsHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View rowView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_goods,viewGroup,false);
        this.context=viewGroup.getContext();
        return new GoodsHolder(rowView,mRecyclerClickListener);
    }
}

class GoodsHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

    private final RecyclerViewClickListener onClickListener;
    TextView goods_name;
    ImageView goods_img;
    public GoodsHolder(View itemView, RecyclerViewClickListener onClickListener){
        super(itemView);
        goods_name = (TextView) itemView.findViewById(R.id.item_goods_name);
        goods_img = (ImageView) itemView.findViewById(R.id.item_goods_img);
        goods_img.setDrawingCacheEnabled(true);
        goods_img.setOnTouchListener(this);
        this.onClickListener = onClickListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && event.getAction() != MotionEvent.ACTION_MOVE) {

            onClickListener.onClick(v, getPosition(), event.getX(), event.getY());
        }
        return true;
    }
}

