package com.yunyan.mall.activitys;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.yunyan.mall.R;
import com.yunyan.mall.adapters.GoodsAdapter;
import com.yunyan.mall.entities.Goods;
import com.yunyan.mall.utils.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;

public class MallMainActivity extends Activity implements RecyclerViewClickListener, View.OnClickListener {
    private List<Goods> mGoodsList;
    private GoodsAdapter mGoodsAdapter;
    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mall_activity_main);
        initGoodsData();
        mRecyclerView=(RecyclerView)findViewById(R.id.activity_goods_recycler);
        mGoodsAdapter= new GoodsAdapter(mGoodsList);
        mGoodsAdapter.setRecyclerListListener(this);
        mRecyclerView.setAdapter(mGoodsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void initGoodsData(){
        mGoodsList=new ArrayList<Goods>();
        for (int i=0;i<=10;i++){
            Goods goods=new Goods();
            goods.setGoods_Name("第" + i + "张");
            goods.setGoods_Img("http://10.0.0.17:8080/YunYan/images/" +i+".jpg");
            mGoodsList.add(goods);
            Goods goods2=new Goods();
            goods2.setGoods_Name("第" + i + "张");
            goods2.setGoods_Img("http://10.0.0.17:8080/YunYan/images/" +i+".jpg");
            mGoodsList.add(goods2);
            Goods goods3=new Goods();
            goods3.setGoods_Name("第" + i + "张");
            goods3.setGoods_Img("http://10.0.0.17:8080/YunYan/images/" +i+".jpg");
            mGoodsList.add(goods3);
            Goods goods4=new Goods();
            goods4.setGoods_Name("第" + i + "张");
            goods4.setGoods_Img("http://10.0.0.17:8080/YunYan/images/" +i+".jpg");
            mGoodsList.add(goods4);
            Goods goods5=new Goods();
            goods5.setGoods_Name("第" + i + "张");
            goods5.setGoods_Img("http://10.0.0.17:8080/YunYan/images/" +i+".jpg");
            mGoodsList.add(goods5);
            Goods goods6=new Goods();
            goods6.setGoods_Name("第" + i + "张");
            goods6.setGoods_Img("http://10.0.0.17:8080/YunYan/images/" +i+".jpg");
            mGoodsList.add(goods6);
            Goods goods7=new Goods();
            goods7.setGoods_Name("第" + i + "张");
            goods7.setGoods_Img("http://10.0.0.17:8080/YunYan/images/" +i+".jpg");
            mGoodsList.add(goods7);
            Goods goods8=new Goods();
            goods8.setGoods_Name("第" + i + "张");
            goods8.setGoods_Img("http://10.0.0.17:8080/YunYan/images/" +i+".jpg");
            mGoodsList.add(goods8);
            Goods goods9=new Goods();
            goods9.setGoods_Name("第" + i + "张");
            goods9.setGoods_Img("http://10.0.0.17:8080/YunYan/images/" +i+".jpg");
            mGoodsList.add(goods9);
            Goods goods10=new Goods();
            goods10.setGoods_Name("第" + i + "张");
            goods10.setGoods_Img("http://10.0.0.17:8080/YunYan/images/" +i+".jpg");
            mGoodsList.add(goods10);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onClick(View v, int position, float x, float y) {

    }
}
