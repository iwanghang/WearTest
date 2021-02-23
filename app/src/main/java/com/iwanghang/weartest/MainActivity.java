package com.iwanghang.weartest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.iwanghang.weartest.whUtil.SystemInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static TextView text_info_01;
    private static TextView text_info_02;
    private static TextView text_info_03;
    private static Button text_app_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        text_info_01 = findViewById(R.id.text_info_01);
        text_info_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cpuTemp = SystemInfo.getCpuTemp();
                String batteryTemp = SystemInfo.getBatteryTemp();
                text_info_01.setText("Cpu温度 --> " + cpuTemp);
                text_info_02.setText("电池温度 --> " + batteryTemp);
            }
        });
        text_info_02 = findViewById(R.id.text_info_02);
        text_info_02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cpuTemp = SystemInfo.getCpuTemp();
                String batteryTemp = SystemInfo.getBatteryTemp();
                text_info_01.setText("Cpu温度 --> " + cpuTemp);
                text_info_02.setText("电池温度 --> " + batteryTemp);
            }
        });
        text_info_03 = findViewById(R.id.text_info_03);
        text_app_list = findViewById(R.id.text_app_list);
        text_app_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AppsActivity.class);
                startActivity(intent);
            }
        });

        String cpuTemp = SystemInfo.getCpuTemp();
        String batteryTemp = SystemInfo.getBatteryTemp();
        text_info_01.setText("Cpu温度 --> " + cpuTemp);
        text_info_02.setText("电池温度 --> " + batteryTemp);
        String cpuApi = SystemInfo.getCPUABI();
        text_info_03.setText("Cpu架构 --> " + cpuApi);

    }

}
