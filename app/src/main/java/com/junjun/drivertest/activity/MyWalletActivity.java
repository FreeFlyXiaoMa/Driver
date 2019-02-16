package com.junjun.drivertest.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.junjun.drivertest.R;


public class MyWalletActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar=getSupportActionBar();
        bar.hide();
        setContentView(R.layout.activity_wallet);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView(){
        tvBack=findViewById(R.id.tv_back_back);
        tvBack.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back_back:
                        this.finish();
                        break;

        }
    }
}
