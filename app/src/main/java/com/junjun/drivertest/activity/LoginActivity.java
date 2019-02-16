package com.junjun.drivertest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.junjun.bean.CodeBean;
import com.junjun.bean.GetCodeBean;
import com.junjun.bean.LoginBean;
import com.junjun.bean.LoginResultBean;
import com.junjun.drivertest.R;
import com.junjun.drivertest.global.Data;
import com.junjun.drivertest.iohandler.MyIoHandler;
import com.junjun.drivertest.iohandler.ReCode;
import com.junjun.drivertest.iohandler.ReLoginResult;
import com.junjun.drivertest.service.SocketService;

import org.apache.mina.core.session.IoSession;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_login);

        Intent intent = new Intent(LoginActivity.this, SocketService.class);
        startService(intent);
        initSharedPreference();
        pref=getSharedPreferences("LoginActivity",MODE_PRIVATE);
        isFirstLogin=pref.getBoolean("isFirstLogin",true);
        initView();

        if (!isFirstLogin){
            etDriverName.setVisibility(View.GONE);
            etCarType.setVisibility(View.GONE);
            etDriverLicense.setVisibility(View.GONE);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    private EditText etPhone;
    private EditText etCode;
    private Button btnGetCode;
    private Button btnLogin;
    private EditText etDriverName;
    private EditText etCarType;
    private EditText etDriverLicense;
    private SharedPreferences pref;
    private boolean isFirstLogin;
    private void initView() {
        etPhone = (EditText) findViewById(R.id.et_phone);
        etCode = (EditText) findViewById(R.id.et_code);
        btnGetCode = (Button) findViewById(R.id.btn_get_code);
        btnGetCode.setOnClickListener(this);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        etDriverName=findViewById(R.id.et_driverName);
        etCarType=findViewById(R.id.et_carType);
        etDriverLicense=findViewById(R.id.et_driverLicense);

    }

    private void initSharedPreference() {
        SharedPreferences preferences = getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (!preferences.contains("token")) {
            editor.putBoolean("token", false);
            editor.apply();
        }
        if (preferences.getBoolean("token", false)) {
            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
            intent.putExtra("phone", preferences.getString("phone", ""));
            startActivity(intent);
        }
    }

    private boolean isPhone(String phone) {
        boolean isPhone = false;
        String YD = "^1((3[4-9])|(5[012789])|(8[2378])|(47))[0-9]{8}$";
        String LT = "^1((3[0-2])|(5[56])|(8[56]))[0-9]{8}$";
        String DX = "^1((33)|(53)|(8[09]))[0-9]{8}$";
        if (phone.matches(YD) || phone.matches(LT) || phone.matches(DX)) {
            isPhone = true;
        }
        return isPhone;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_code:
               /* if (!isPhone(etPhone.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "请输入有效的手机号", Toast.LENGTH_SHORT).show();
                } else {*/
                    getCode();
               // }
                break;
            case R.id.btn_login:
                if (/*isPhone(etPhone.getText().toString()) &&*/ !etCode.getText().toString().equals("")) {
                    login();
                }
                break;
        }
    }

    private void getCode() {
        final Handler h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                CodeBean code = (CodeBean) msg.getData().getSerializable("code");
                assert code != null;
                Toast.makeText(LoginActivity.this, "验证码是：" + code.getCode(), Toast.LENGTH_SHORT).show();
            }
        };

        final IoSession session = ((Data) getApplication()).getSession();
        MyIoHandler ioHandler = (MyIoHandler) session.getHandler();
        ioHandler.setReCodeListener(new ReCode() {
            @Override
            public void showCode(CodeBean code) {
                Message msg = new Message();
                Bundle b = new Bundle();
                b.putSerializable("code", code);
                msg.setData(b);
                h.sendMessage(msg);
            }
        });
        new Thread() {
            /**/
            @Override
            public void run() {
                session.write(new GetCodeBean(etPhone.getText().toString()));
            }
        }.start();
    }

    private LocationClient mLocationClient;

    /**
     * 登录操作
     */
    private void login() {
        final Handler h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                LoginResultBean result = (LoginResultBean) msg.getData().getSerializable("result");
                assert result != null;
                if (result.isSuccess()) {
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor=pref.edit();
                    editor.putBoolean("isFirstLogin",false);//第一次登陆过后，将isFirstLogin设置为false
                    editor.commit();
                    ((Data) getApplication()).setPhone(etPhone.getText().toString());
                    /*mLocationClient = new LocationClient(getApplicationContext());
                    initLocation();
                    mLocationClient.registerLocationListener(new MyLocationListener());
                    mLocationClient.start();*/
                    if (etDriverName.getText().equals("") || etCarType.getText().equals("") || etDriverLicense.getText().equals("")){
                        Toast.makeText(getApplicationContext(),"司机基本信息不能为空",Toast.LENGTH_SHORT).show();
                    }else{
                        ((Data)getApplication()).setDriverName("王师傅");
                        ((Data)getApplication()).setCarType(etCarType.getText().toString());
                        ((Data)getApplication()).setPlateNumber(etDriverLicense.getText().toString());
                    }



                    startActivity(new Intent(LoginActivity.this, FirstMapActivity.class));
                }else {
                    Toast.makeText(LoginActivity.this,"校验码错误",Toast.LENGTH_SHORT).show();
                }
            }
        };

        final IoSession session = ((Data) getApplication()).getSession();
        MyIoHandler ioHandler = (MyIoHandler) session.getHandler();
        ioHandler.setReLoginResultListener(new ReLoginResult() {
            @Override
            public void showLoginResult(LoginResultBean result) {
                Message msg = new Message();
                Bundle b = new Bundle();
                b.putSerializable("result", result);
                msg.setData(b);
                h.sendMessage(msg);
            }
        });

        new Thread() {
            @Override
            public void run() {
                session.write(new LoginBean(false, etPhone.getText().toString(), etCode.getText().toString()));
            }
        }.start();
    }

    /*private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            ((Data) getApplication()).setCity(location.getCity());
            final IoSession session = ((Data) getApplication()).getSession();
            new Thread() {
                @Override
                public void run() {
                    session.write(new LocationBean(etPhone.getText().toString(), location.getLongitude(), location.getLatitude()));
                }
            }.start();
        }
    }*/
}