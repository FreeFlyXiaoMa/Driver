package com.junjun.drivertest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.junjun.bean.BasicDriverBean;
import com.junjun.bean.CustomBean;
import com.junjun.drivertest.R;
import com.junjun.drivertest.global.Data;
import com.junjun.drivertest.iohandler.MyIoHandler;
import com.junjun.drivertest.iohandler.ReCustom;
import com.junjun.drivertest.iohandler.ReDriver;

import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static ArrayList<HashMap<String, Object>> listOrder = new ArrayList<>();
    private ListView lvOrder;
    private SimpleAdapter adapter;
    //初始化全局变量
    private IoSession session;
    private String phoneDriver;

    private TextView tvDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_order);
        lvOrder = (ListView) findViewById(R.id.lv_order);
        lvOrder.setOnItemClickListener(this);

        tvDefault=findViewById(R.id.tv_default);
        initGlobal();
       // isListEmpty();//判断订单列表是否为空
        adapter = new SimpleAdapter(OrderActivity.this, listOrder, R.layout.list_item,
                    new String[]{"startName", "endName", "price"},
                    new int[]{R.id.tv_start, R.id.tv_end, R.id.tv_price});
        lvOrder.setAdapter(adapter);

    }


    private void initGlobal(){
        phoneDriver = ((Data)getApplication()).getPhone();
        session = ((Data)getApplication()).getSession();
        MyIoHandler ioHandler = (MyIoHandler)session.getHandler();
        ioHandler.setReCustomListener(new ReCustom() {
            @Override
            public void showCustom(CustomBean custom) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable("custom", custom);
                message.setData(bundle);
                handlerCustomBean.sendMessage(message);
            }
        });
        ioHandler.setReDriverListener(new ReDriver() {
            @Override
            public void deleteCustom(BasicDriverBean driver) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable("driver", driver);
                message.setData(bundle);
                handlerDriverBean.sendMessage(message);
            }
        });
        new Thread(){
            @Override
            public void run() {
                session.write(1);
            }
        }.start();
    }

    //处理接收到的订单信息的Handler
    private HandlerCustomBean handlerCustomBean = new HandlerCustomBean();
    private class HandlerCustomBean extends Handler {
        @Override
        public void handleMessage(Message msg) {
            CustomBean custom = (CustomBean)msg.getData().getSerializable("custom");
            HashMap<String, Object> map = new HashMap<>();
            /*if (custom.isEvaluated()){
                return;
            }*/
            assert custom != null;
            map.put("phoneCustom", custom.getPhoneCustom());
            map.put("currentLat",custom.getCurrentLat());
            map.put("currentLong",custom.getCurrentLong());
            map.put("startName", "起点："+custom.getStartName());
            map.put("startLat", custom.getStartLat());
            map.put("startLong", custom.getStartLong());
            map.put("endName", "终点："+custom.getEndName());
            map.put("endLat", custom.getEndLat());
            map.put("endLong", custom.getEndLong());
            map.put("price", "￥"+custom.getPrice());
           /* if (isFirstTime){
                listOrder.add(map);
                isFirstTime=false;
            }
            if (listOrder.size()>1){
                HashMap<String,Object> mapFirst=listOrder.get(1);
                if (mapFirst.get("endLat")!= map.get("endLat")){
                    listOrder.add(map);
                }

            }*/
            /*if (listOrder.size()>0){
                Iterator<HashMap<String,Object>> it=listOrder.iterator();
                while (it.hasNext()){
                    HashMap<String,Object> mapValue=it.next();
                    if (mapValue.get("endLat")!= map.get("endLat")){//如果listOrder中的订单不同于map中的订单，则将map添加进去
                        listOrder.add(map);
                    }
                }

                for ( HashMap<String, Object> mapValue:listOrder){
                    if (mapValue.get("endLat")==map.get("endLat")){//如果listOrder中已经有了该条订单，则不添加到listOrder集合中

                    }else {
                        listOrder.add(map);
                        listAdd
                    }
                }
            }*/
            if (listOrder.size()==0){
                listOrder.add(map);
                adapter.notifyDataSetChanged();
            }
            //isListEmpty();

        }
    }

    private static ArrayList<HashMap<String, Object>> delList = new ArrayList<>();//在对List进行遍历时，避免直接删除list中数据
    //处理接收到的订单被抢信息的Handler
    private HandlerDriverBean handlerDriverBean = new HandlerDriverBean();
    private class HandlerDriverBean extends Handler {
        @Override
        public void handleMessage(Message msg) {
            BasicDriverBean driver = (BasicDriverBean)msg.getData().getSerializable("driver");
            for (HashMap<String, Object> map : listOrder){
                assert driver != null;
                if (map.get("phoneCustom").equals(driver.getPhoneCustom())){
                    delList.add(map);
                }
            }
            listOrder.removeAll(delList);
            //isListEmpty();
            adapter.notifyDataSetChanged();
        }
    }

    //判断订单列表是否为空
    private void isListEmpty(){
        Log.e("OrderActivity","判断订单列表是否为空");
        if (listOrder.size()>0){
            tvDefault.setVisibility(View.VISIBLE);
        }else {
            tvDefault.setVisibility(View.INVISIBLE);
        }
    }

    //点击列表项的处理函数
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //将type设为1，设置为预接单模式
       // SharedPreferences.Editor editor= (SharedPreferences.Editor) getApplicationContext().getSharedPreferences("TAG",
               // Context.MODE_PRIVATE).edit();
       // editor.putInt("type",1);

        Intent intent = new Intent(OrderActivity.this, MapActivity.class);
        intent.putExtra("custom", (HashMap<String, Object>)parent.getItemAtPosition(position));
        Log.e("OrderActivity","跳转到MapActivity");
        startActivity(intent);
        this.finish();
    }
}
