package com.junjun.drivertest.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.junjun.bean.BasicDriverBean;
import com.junjun.bean.CustomBean;
import com.junjun.bean.LocationBean;
import com.junjun.drivertest.R;
import com.junjun.drivertest.entity.JourneyEntity;
import com.junjun.drivertest.global.Data;
import com.junjun.drivertest.overlayutil.DrivingRouteOverlay;

import org.apache.mina.core.session.IoSession;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MapActivity extends AppCompatActivity implements OnGetRoutePlanResultListener, View.OnClickListener,SensorEventListener {


    private MapView mMapView;//百度地图
    private double currentLat;//乘客当前纬度
    private double currentLong;//乘客当前经度

    /*异步更新UI*/
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                String distance= (String) msg.obj;
                tvDistance.setText(distance);
            }else if (msg.what==2){
                //btnReceive.setText("已接单");
                btnReceive.setVisibility(View.INVISIBLE);
            }else if (msg.what==3){
                btnMainMenu.setText("结束本订单");

            }else if (msg.what==4){
                mBaiduMap.addOverlay(new MarkerOptions().title("乘客")
                                                        /*在乘客纬度上增加0.01f的值*/
                                                        .position(new LatLng(currentLat+0.01,currentLong))
                                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding)));
            }

        }
    };

  MyLocationListener myListener=new MyLocationListener();

    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        //pref=getSharedPreferences("TAG",MODE_PRIVATE);
        initGlobal();
        initView();
        mSensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        initLocation();
    }

    //初始化全局变量
    private IoSession session;
    private String phoneDriver;
    private CustomBean custom;
    private String startName;
    private String endName;

    private void initGlobal() {

        phoneDriver = ((Data) getApplication()).getPhone();

            session = ((Data) getApplication()).getSession();
            Intent intent = getIntent();
            HashMap<String, Object> map = (HashMap<String, Object>) intent.getSerializableExtra("custom");
            String phoneCustom = (String) map.get("phoneCustom");

            currentLat= (double) map.get("currentLat");
            currentLong= (double) map.get("currentLong");
            //Toast.makeText(getApplicationContext(),"接收到乘客端的当前经纬度"+currentLat,Toast.LENGTH_SHORT).show();
            startName = (String) map.get("startName");
            double startLat = (double) map.get("startLat");
            double startLong = (double) map.get("startLong");
            endName = (String) map.get("endName");
            double endLat = (double) map.get("endLat");
            double endLong = (double) map.get("endLong");
            //distance=DistanceUtil.getDistance(new LatLng(startLat,startLong),new LatLng(endLat,endLong));
            int price=0;
            custom =new CustomBean(phoneCustom,currentLat,currentLong,startName,startLat,startLong,endName,endLat,endLong,price);
    }

    //初始化界面
    private BaiduMap mBaiduMap;
    private RoutePlanSearch search;
    private Button btnReceive;
    private ImageView ivCall;//打电话按钮
    private TextView tvDistance;
    private LinearLayout llPassengerDatil;//接单后显示的乘客详细信息
    private Button btnMainMenu;//乘客已上车、结束订单

    private void initView() {
         mMapView = (MapView) findViewById(R.id.baidumap);
        btnReceive = (Button) findViewById(R.id.btn_receive);//接单按钮
        btnReceive.setOnClickListener(this);
        mBaiduMap = mMapView.getMap();
        llPassengerDatil=findViewById(R.id.ll_passenger_detail);
        ivCall=findViewById(R.id.iv_call);
        ivCall.setOnClickListener(this);
        tvDistance=findViewById(R.id.tv_distance);

        btnMainMenu=findViewById(R.id.btn_main_menu);//乘客是否上车、下车
        btnMainMenu.setOnClickListener(this);

        search = RoutePlanSearch.newInstance();
        search.setOnGetRoutePlanResultListener(this);

        PlanNode startNode = PlanNode.withLocation(new LatLng(custom.getStartLat(), custom.getStartLong()));
        PlanNode endNode = PlanNode.withLocation(new LatLng(custom.getEndLat(), custom.getEndLong()));
        /*TransitRoutePlanOption option = new TransitRoutePlanOption();
        option.city(((Data) getApplication()).getCity());
        option.from(startNode);
        option.to(endNode);
        search.transitSearch(option);*/

        /*添加驾驶路线功能*/
        DrivingRoutePlanOption option1 = new DrivingRoutePlanOption();
        option1.currentCity(((Data) getApplication()).getCity());
        option1.from(startNode);
        option1.to(endNode);
        search.drivingSearch(option1);

        }


    //初始化定位相关
    BitmapDescriptor mCurrentMarker = null;
    private LocationClient mLocationClient;
    private float mCurrentAccuracy;
    private double mCurrentLat ;//司机当前的经纬度
    private double mCurrentLng ;
    private Double mLastX = 0.0;
    private int mCurrentDirection = 0;
    private boolean isFirstLoc = true;
    private MyLocationData locData;


    private void initLocation(){
        mBaiduMap.setMyLocationEnabled(true);
        /*在此处添加显示当前位置的图标*/
        mCurrentMarker = BitmapDescriptorFactory.fromResource(R.mipmap.arrow);
        //mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true
        //      , mCurrentMarker, accuracyCircleFillColor, accuracyCircleStrokeColor));
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,mCurrentMarker));
        //重新定位
        //ReLocation(mCurrentLat,mCurrentLng);
        //定位选项
        mLocationClient = new LocationClient(getApplicationContext());

        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
        /*-----------------------------------------------------------------------------------------*/
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(10000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
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

    //监听路线搜索结果的接口
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
      /*  List<TransitRouteLine> lines = transitRouteResult.getRouteLines();
        if (lines == null) {
            Toast.makeText(MapActivity.this, "没有合适的线路", Toast.LENGTH_SHORT).show();
        } else {
            TransitRouteLine line = lines.get(0);
            TransitRouteOverlay transitRoute = new TransitRouteOverlay(mBaiduMap);
            transitRoute.setData(line);
            transitRoute.addToMap();
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(
                    new LatLng(custom.getStartLat(), custom.getStartLong()));
            mBaiduMap.animateMapStatus(update);
        }*/
    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        } else {
            DrivingRouteLine routeLine = drivingRouteResult.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            //DrivingRouteOverlay overlay=new DrivingRouteOverlay(baiduMap);
            overlay.setData(routeLine);
            overlay.addToMap();
            overlay.zoomToSpan();
            Log.e("MapActivity", "从起点到终点距离：" + routeLine.getDistance());

        }

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
    }

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


    /**
     * 显示一条驾车路线的overlay，定制RouteOverlay
     */
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {
        /**
         * 构造函数
         *
         * @param baiduMap 该DrivingRouteOvelray引用的 BaiduMap
         */
        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.ic_me_history_startpoint);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.ic_me_history_finishpoint);
        }
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
            /*按照定位频率，每隔30秒计算一下与乘客的距离------------------------------------------*/
            double distance= DistanceUtil.getDistance(new LatLng(mCurrentLat,mCurrentLng),new LatLng(currentLat+0.01,currentLong));
            //Toast.makeText(getApplicationContext(),distance+"",Toast.LENGTH_SHORT).show();
            String DecDistance=new DecimalFormat("0000.0").format(distance);
            //int dis_int=Integer.parseInt(DecDistance);
            Message msg=new Message();
            msg.obj=DecDistance;
            msg.what=1;
            handler.sendMessage(msg);
            //Toast.makeText(getApplicationContext(),"距离"+distance,Toast.LENGTH_SHORT).show();
            /*每隔10s向乘客发送司机的经纬度-------------------------------------------------------*/
            sendLocationToPassenger(DecDistance);

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


    //监听按钮点击事件的接口
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_receive:
                        new Thread() {
                            @Override
                            public void run() {
                                /*向乘客端发送消息，告诉乘客司机已接单----------------------------*/
                                //session.write(new DriverBean(custom.getPhoneCustom(), phoneDriver));
                                session.write(new BasicDriverBean(custom.getPhoneCustom(),phoneDriver,"王师傅"
                                        ,"别克GL8","沪A2010",false,false));
                            }
                        }.start();

                        //sendLocationToPassenger();//向乘客发送司机位置

                        Animation slide_right_to_left = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_right_to_left);
                        llPassengerDatil.startAnimation(slide_right_to_left);
                        llPassengerDatil.setVisibility(View.VISIBLE);

                        /*在地图上显示司机到乘客的位置的路线--------------------------------------*/
                        handler.sendEmptyMessage(1);
                        handler.sendEmptyMessage(2);
                        handler.sendEmptyMessage(4);//地图上显示乘客位置
                        break;
            case R.id.iv_call://如果要给乘客打电话确认
                showCallPhoneDialog();
                break;
            case R.id.btn_main_menu:
                isPassengerGetIn();
                break;
            default:
                    break;
            }

        }

    /**
     * 给乘客发送司机位置
     */
    private void sendLocationToPassenger(final String distance){

        new Thread(){
            @Override
            public void run() {
                session.write(new LocationBean(phoneDriver,mCurrentLat,mCurrentLng,distance));
            }
        }.start();

    }


    /**
     * 司机接单后的处理逻辑
     */
    private  void showCallPhoneDialog(){
        AlertDialog dialog = new AlertDialog.Builder(MapActivity.this)
                .setCancelable(false)
                .setMessage("乘客手机号是：\n" + custom.getPhoneCustom()+"\n" +
                        "是否要拨打电话？")
                .setPositiveButton("打电话", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + custom.getPhoneCustom()));
                        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MapActivity.this, "用户未启用拨打电话权限", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startActivity(intent);
                    }
                }).setNegativeButton("不打电话", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        btnReceive.setText("订单已交易");
                        btnReceive.setClickable(false);

                    }
                }).create();
        dialog.show();


    }

    /**
     * 判断乘客是否已经上车，
     */
    private void isPassengerGetIn(){
        if (btnMainMenu.getText().equals("乘客已上车")){
            handler.sendEmptyMessageDelayed(3,1300);
            new Thread(){
                @Override
                public void run() {
                    session.write(new BasicDriverBean("",phoneDriver,((Data)getApplication()).getDriverName(),
                            ((Data)getApplication()).getCarType(),
                            ((Data)getApplication()).getPlateNumber(),
                            true,
                            false));
                }
            }.start();
        }else if (btnMainMenu.getText().equals("结束本订单")){
            /*通知乘客端，本次叫车结束*/
            new Thread(){
                @Override
                public void run() {
                    /*session.write(new BasicDriverBean("",phoneDriver,"王师傅","别克·银色GL8","沪A·2010",
                            true,true));*/
                    session.write(new BasicDriverBean("",phoneDriver,((Data)getApplication()).getDriverName(),
                            ((Data)getApplication()).getCarType(),
                            ((Data)getApplication()).getPlateNumber(),
                            true,
                            true));
                }
            }.start();

            /*将本次打车记录存储到本地数据库*/
            long l=System.currentTimeMillis();
            Date date=new Date(l);
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            if (startName != null && endName!=null){
                JourneyEntity table=new JourneyEntity();
                table.setJourneyTime(dateFormat.format(date));
                table.setStartAddress(startName.toString());
                table.setEndAddress(endName.toString());
                table.setChecked(false);
                table.save();
                Log.e("MapActivity","litePal存储到本地,时间："+dateFormat.format(date)+"，起点："+startName+"，终点"+endName);
            }
            Toast.makeText(getApplicationContext(),"本次叫车服务结束",Toast.LENGTH_SHORT).show();
            this.finish();

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);//注册传感器服务
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        search.destroy();
        session.close();
        mBaiduMap.clear();
        mSensorManager.unregisterListener(this);
    }


}
