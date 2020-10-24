package com.geng.clicktest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Id(R.id.btn)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InjectUtils.getView(this);
        InjectUtils.onClick(this);
    }

    @OnClick(R.id.btn)
    public void onClick(View v){
        Toast.makeText(this,"点击了button",Toast.LENGTH_SHORT).show();
    }
}