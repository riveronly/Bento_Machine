package com.soulocean.bento_machine_c.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.soulocean.bento_machine_c.R;

import java.util.Objects;

/**
 * 启动界面，过度界面
 *
 * @author soulo
 */
public class SplashActivity extends AppCompatActivity {

    private String sharedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();
        //隐藏标题栏

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        //获取分享的文字
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                //文字
                sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    Toast.makeText(getApplicationContext(), sharedText, Toast.LENGTH_SHORT).show();
                }
            }
        }

        //启动线程进行休眠和跳转
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);//使程序休眠1秒
                    Intent it = new Intent(getApplicationContext(), LoginActivity.class);//启动MainActivity
                    it.putExtra("one_step_string", sharedText);
                    startActivity(it);
                    finish();//关闭当前活动
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //启动线程
        myThread.start();
    }
}
