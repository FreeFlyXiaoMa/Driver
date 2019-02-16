package com.junjun.drivertest.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.junjun.drivertest.R;
import com.junjun.drivertest.adapter.MyJourneyAdapter;
import com.junjun.drivertest.entity.JourneyEntity;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2018/7/6 0006.
 */

public class HistoryList extends AppCompatActivity implements View.OnClickListener {

    private TextView tvBack;
    private ListView lvJourney;

    private TextView mSelectAll;
    private RelativeLayout mDeleteRl;
    private List<JourneyEntity> dataList;
    private MyJourneyAdapter adapter;

    private boolean mIsDeleteModel=false; //当前listView是否处于删除模式，默认是否
    private boolean mIsSelectAll=false; //是否选中删除全部


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Connector.getDatabase();
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_journey);
        tvBack=findViewById(R.id.tv_back);
        lvJourney=findViewById(R.id.lv_journey);
        //mEmpty_ll=findViewById(R.id.empty_ll);
        mSelectAll=findViewById(R.id.tv_select_all);
        mDeleteRl=findViewById(R.id.main_delete_rl);

        //添加点击事件
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mSelectAll.setOnClickListener(this);
        mDeleteRl.setOnClickListener(this);
        //lvJourney的点击事件
        lvJourney.setOnItemClickListener(new MyOnItemClickListener());
        lvJourney.setOnItemLongClickListener(new MyOnItemLongClickListener());

        initData();
    }

    private void initData(){
        mSelectAll.setVisibility(View.VISIBLE);
        if (adapter == null){
            findList();
            adapter=new MyJourneyAdapter(this,dataList);
            lvJourney.setAdapter(adapter);
            //lvJourney.setEmptyView(mEmpty_ll);
            adapter.notifyDataSetChanged();
        }else {
           // adapter.setMyJourney(dataList);
            adapter.notifyDataSetChanged();
        }

    }

    /**
     * 执行数据库查找，找出所有的条目
     */
    private void findList() {
        dataList= DataSupport.findAll(JourneyEntity.class);
        Log.e("HistoryList","执行数据库查询操作" );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_select_all:
                //mDeleteRl.setVisibility(View.VISIBLE);
                if (mIsSelectAll){
                    clearCheckItem();
                }else {
                    addDeleteAllChoices();
                }
                changeSelectText();
                break;
            case R.id.main_delete_rl:
                deleteCheckItem();//删除选中的item
                hideDeleteMode();
                break;
            default:
                break;
        }
    }

    //条目的点击事件
    private  class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            if (mIsDeleteModel){
                addDeleteChoice(position);
            }else {
                JourneyEntity journeyEntity=dataList.get(position);
                Toast.makeText(HistoryList.this,"点击了id为"+journeyEntity.getId()+"的信息",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * item的长点击事件
     */
    private class MyOnItemLongClickListener implements OnItemLongClickListener{
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
           if (!mIsDeleteModel){
               showDeleteMode(position);//将所有条目添加到删除模式
               Log.e("HistoryList","条目的长点击时间，第一次默认所有条目全选");
           }
            return true;
        }
    }

    /**
     * 添加删除item
     */
    private void addDeleteChoice(int position){
        JourneyEntity journeyEntity=dataList.get(position);
        if (journeyEntity.isChecked()){
            journeyEntity.setChecked(false);
        }else {
            journeyEntity.setChecked(true);

        }
        //adapter.setMyJourney(dataList);
        adapter.showDeleteCheckBox();
        adapter.notifyDataSetChanged();
        mIsDeleteModel=true;
    }

    /**
     * 展示编辑模式
     */
    private void showDeleteMode(int position){
        mSelectAll.setVisibility(View.VISIBLE);
        mDeleteRl.setVisibility(View.VISIBLE);
        JourneyEntity journeyEntity=dataList.get(position);
        journeyEntity.setChecked(true);
        adapter.setMyJourney(dataList);
        adapter.showDeleteCheckBox();
        mIsDeleteModel=true;
    }


    /**
     * 隐藏删除模式，回到正常显示的listView
     */
    private void hideDeleteMode(){
        mSelectAll.setVisibility(View.GONE);
        //mDeleteRl.setVisibility(View.GONE);
        Iterator<JourneyEntity> iterator=dataList.iterator();
        while (iterator.hasNext()){
            JourneyEntity journeyEntity=iterator.next();
            journeyEntity.setChecked(false);

        }
        adapter.hideDeleteCheckBox();
        initData();
        mIsDeleteModel=false;
    }

    /**
     * 全部选中
     */
    private void addDeleteAllChoices(){
        Iterator<JourneyEntity> iterator=dataList.iterator();
        while (iterator.hasNext()){
            JourneyEntity journeyEntity=iterator.next();
            journeyEntity.setChecked(true);
        }
        adapter.setMyJourney(dataList);
        adapter.showDeleteCheckBox();
        mIsDeleteModel=true;
        mIsSelectAll=true;
    }

    /**
     * 删除
     */
    private void deleteCheckItem(){
        Iterator<JourneyEntity> iterator=dataList.iterator();
        int count=0;
        while (iterator.hasNext()){
            JourneyEntity journeyEntity=iterator.next();
            if (journeyEntity.isChecked()){
                iterator.remove();
                count++;
            }
        }
        Toast.makeText(this,"删除了"+count+"条数据",Toast.LENGTH_SHORT).show();

    }


    /**
     * 清空全部
     */
    private void clearCheckItem(){
        Iterator<JourneyEntity> iterator=dataList.iterator();
        while (iterator.hasNext()){
            JourneyEntity journeyEntity=iterator.next();
            journeyEntity.setChecked(false);
        }
        adapter.setMyJourney(dataList);
        adapter.showDeleteCheckBox();
        mIsDeleteModel=true;
        mIsSelectAll=false;
    }

    /**
     * 处于删除模式时，改变标题的文字，全部选中-->>全部删除  全部删除-->全部选中
     */
    private void changeSelectText(){
        if (mIsSelectAll){
            mSelectAll.setText("清空全选");
        }else {
            mSelectAll.setText("全部选中");
        }
    }

    /**
     * 点击事件的处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (adapter == null){
            return super.onKeyDown(keyCode, event);
        }
        //当按返回键，同时处于删除模式时
        if (keyCode == KeyEvent.KEYCODE_BACK && mIsDeleteModel){
            hideDeleteMode();//隐藏删除模式，改为正常模式
            mSelectAll.setText("编辑");
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }



}
