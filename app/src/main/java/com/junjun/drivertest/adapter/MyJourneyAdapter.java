package com.junjun.drivertest.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.junjun.drivertest.R;
import com.junjun.drivertest.entity.JourneyEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/10 0010.
 */

public class MyJourneyAdapter extends BaseAdapter {

    private List<JourneyEntity> listValue=new ArrayList<JourneyEntity>();
    private Context mContext;

    private boolean mIsShowDeleteCheckBox;


        public MyJourneyAdapter(Context context, List<JourneyEntity> list){
            this.mContext=context;
            this.listValue=list;
        }

    /**
     * 添加list数据，并传递
     * @param
     */
        public void setMyJourney(List<JourneyEntity> list){
            this.listValue=list;
        }

    @Override
    public int getCount() {
        return listValue.size();
    }

    @Override
    public Object getItem(int position) {
        return listValue.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        ViewHolder holder=null;
        if (convertView == null){
            convertView=inflater.inflate(R.layout.journey_item,null);
            holder=new ViewHolder();
            holder.journeyTime=convertView.findViewById(R.id.journey_time);
            holder.journeyStartAddress=convertView.findViewById(R.id.journey_start_address);
            holder.journeyEndAddress=convertView.findViewById(R.id.journey_end_address);
            holder.cb=convertView.findViewById(R.id.item_cb);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }

        //判断是否显示 复选框
        if (mIsShowDeleteCheckBox){
            holder.cb.setVisibility(View.VISIBLE);
            holder.cb.setChecked(listValue.get(position).isChecked());

        }else {
            holder.cb.setVisibility(View.GONE);
        }

        //获取第position处，存放的信息
        JourneyEntity data=listValue.get(position);
        String time=data.getJourneyTime();
        String startLocation=data.getStartAddress();
        String endLocation=data.getEndAddress();
        Log.e("MyJourneyAdapter","时间："+time+"，起点："+startLocation +",终点："+endLocation);

        holder.journeyTime.setText(time.toString());
        holder.journeyStartAddress.setText(startLocation.toString());
        holder.journeyEndAddress.setText(endLocation.toString());
        //DataSupport.saveAll(listValue);
        // Log.e("MyJourneyAdapter","数据库保存,数据条目为"+listValue.size());
        return convertView;
    }


    /**
     * 选择显示复选框按钮
     */
    public void showDeleteCheckBox(){
        mIsShowDeleteCheckBox=true;
        notifyDataSetChanged();//刷新ListView的UI
    }

    /**
     * 隐藏复选框
     */
    public void hideDeleteCheckBox(){
        mIsShowDeleteCheckBox=false;
    }

    class ViewHolder{
        TextView journeyTime;
        TextView journeyStartAddress;
        TextView journeyEndAddress;
        CheckBox cb;
    }

}
