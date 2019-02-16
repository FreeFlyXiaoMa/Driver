package com.junjun.drivertest.activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.junjun.bean.CustomBean;
import com.junjun.drivertest.R;
import com.junjun.drivertest.global.Data;
import com.junjun.drivertest.iohandler.MyIoHandler;
import com.junjun.drivertest.iohandler.ReCustom;

import org.apache.mina.core.session.IoSession;

public class FirstMapActivity extends AppCompatActivity implements View.OnClickListener,SensorEventListener{

    private Button btn_check_order;//查看订单
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    BitmapDescriptor mCurrentMarker = null;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;

    private LocationClient mLocationClient;
    private Double mLastX = 0.0;
    private int mCurrentDirection = 0;
    private boolean isFirstLoc = true;    //是否第一次定位
    private float mCurrentAccuracy;
    private double mCurrentLat = 0.0;
    private double mCurrentLng = 0.0;

    private MyLocationData locData;
    private SensorManager mSensorManager;

    private SlidingMenu mSlidingMenu;//侧边栏
    private Button btnPersonalData;//个人资料
    private TextView tvOrders;//订单
    private Button btnMessage;//消息

    private ImageView myLocation;//重定位按钮
    private TextView tvWallet;

    MyLocationListener myListener=new MyLocationListener();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());//百度地图初始化
        ActionBar bar=getSupportActionBar();//隐藏标题栏
        bar.hide();
        setContentView(R.layout.activity_first_map);

        initSlidingMenu();
        findViewById();
        mSensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);//获取手机传感器服务
        initMap();
        /*循环接收乘客端发来的消息*/
       // dealWithCustomBean();
    }

    /**
     * 初始化布局
     */
    private void findViewById(){
        btn_check_order=(Button) findViewById(R.id.btn_check_order);
        btn_check_order.setOnClickListener(this);

        mMapView=findViewById(R.id.mMapView);

        btnPersonalData=findViewById(R.id.btn_personal_data);
        btnPersonalData.setOnClickListener(this);

        myLocation=findViewById(R.id.my_location);
        myLocation.setOnClickListener(this);
        Log.e("FirstMapActivity","启动FirstMapActivity");
        /*个人资料界面的初始化*/
        tvOrders=(TextView) findViewById(R.id.tv_orders);
        tvOrders.setClickable(true);
        tvOrders.setOnClickListener(this);
        btnMessage=findViewById(R.id.btn_message);
        btnMessage.setOnClickListener(this);

        tvWallet=findViewById(R.id.tv_myWallet);
        tvWallet.setOnClickListener(this);
    }

    /**
     * 地图操作
     */
    private void initMap(){
        //隐藏缩放按钮
        mMapView.showZoomControls(false);
        //mMapView.showScaleControl(false);//不显示比例尺
        mBaiduMap=mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        /*在此处添加显示当前位置的图标*/
        mCurrentMarker = BitmapDescriptorFactory.fromResource(R.mipmap.arrow);
        //mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true
          //      , mCurrentMarker, accuracyCircleFillColor, accuracyCircleStrokeColor));
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,mCurrentMarker));
        //重新定位
        ReLocation(mCurrentLat,mCurrentLng);
        //定位选项
        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();

    }

    /**
     * 初始化侧边栏
     */
    private void initSlidingMenu(){
        mSlidingMenu=new SlidingMenu(this);
        mSlidingMenu.setMode(SlidingMenu.LEFT); //设置从左侧弹出/划出SlidingMenu
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);//设置取消手势滑动翻转侧边栏
        mSlidingMenu.attachToActivity(this,SlidingMenu.SLIDING_CONTENT);//绑定到哪一个activity对象
        mSlidingMenu.setMenu(R.layout.layout_left);
        mSlidingMenu.setBehindOffsetRes(R.dimen.sliding_menu_offset);

    }

    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_check_order://查看派单详情
                Intent intent=new Intent(this,OrderActivity.class);
                startActivity(intent);
                //this.finish();
                break;
            case R.id.btn_personal_data://查看个人资料
                mSlidingMenu.toggle();
                break;
            case R.id.my_location://重定位
                ReLocation(mCurrentLat,mCurrentLng);
                break;
            case R.id.tv_orders://查看个人接单历史
                startActivity(new Intent(getApplicationContext(),HistoryList.class));
                break;
            case R.id.btn_message:
                startActivity(new Intent(FirstMapActivity.this,MessageActivity.class));
                break;
            case R.id.tv_myWallet:
                startActivity(new Intent(getApplicationContext(),MyWalletActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * 接收乘客端发来的消息
     */
    private IoSession session;
    private void dealWithCustomBean(){
        session=((Data)getApplication()).getSession();
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

    }

    //处理接收到的订单信息的Handler
    private HandlerCustomBean2 handlerCustomBean = new HandlerCustomBean2();
    private class HandlerCustomBean2 extends Handler {
        @Override
        public void handleMessage(Message msg) {
            CustomBean custom = (CustomBean)msg.getData().getSerializable("custom");
            /*HashMap<String, Object> map = new HashMap<>();*/

           /* if (custom.isEvaluated()){
                final AlertDialog.Builder builder=new AlertDialog.Builder(getApplicationContext());

                builder.setTitle("乘客"+custom.getPhoneCustom()+"已经付款："+custom.getPrice());
                builder.setCancelable(true);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
            }*/

        }


    }


    /**
     * 初始化定位
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(60000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(true);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(true);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(true);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        //option.setAddrType("all");
        mLocationClient.setLocOption(option);
    }


    //接收到自己定位信息的处理函数
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            //mapView销毁后不再处理新接收的位置
            Log.e("FirstMapActivity","接收到百度地图服务器返回的数据");
            if (location == null || mMapView == null) {
                return;
            }

            mCurrentAccuracy = location.getRadius();
            mCurrentLat=location.getLatitude();
            mCurrentLng=location.getLongitude();
            //将获取的location信息给百度map
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    //此处设置开发者获取到的方向信息，顺时针0-360，mLastX就是获取到的方向传感器传来的X轴数值
                    .direction(mCurrentDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(locData);
            String city=location.getCity();
            ((Data)getApplication()).setCity(city);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            }
        }
    }


    //添加方向传感器，以根据手机方向改变定位方向
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //判断返回的传感器类型是不是方向传感器
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            //只获取x的值
            double x = sensorEvent.values[SensorManager.DATA_X];
            //为了防止经常性的更新
            if (Math.abs(x - mLastX) > 1.0) {
                mCurrentDirection = (int) x;
                locData = new MyLocationData.Builder().accuracy(mCurrentAccuracy)
                        .direction(mCurrentDirection)
                        .latitude(mCurrentLat)
                        .longitude(mCurrentLng)
                        .build();
                mBaiduMap.setMyLocationData(locData);

            }
            mLastX = x;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        //为系统的方向传感器注册监听器
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
        //        SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);

        //ReLocation();
    }

    /**
     * 重新定位
     */
    private void ReLocation(double lat,double lng){
        LatLng latLng=new LatLng(lat,lng);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(mapStatusUpdate);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        //取消传感器监听
        mSensorManager.unregisterListener((SensorEventListener) this);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
