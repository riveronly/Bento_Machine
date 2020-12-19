package com.soulocean.bento_machine_c.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.soulocean.bento_machine_c.util.QRCodeUtil;
import com.soulocean.bento_machine_c.R;
import com.soulocean.bento_machine_c.util.ClientThread;

import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * @author soulo
 */
public class SettleActivity extends AppCompatActivity {

    private ClientThread clientThread;
    //连接server
    Handler handler = new Handler()
    {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            // 如果消息来自于子线程
            // 将读取的内容追加显示在文本框中
            //tvServer.append(msg.obj.toString()+'\n');
        }

    };

    private RelativeLayout qrLayout;
    private RelativeLayout faceLayout;
    private TextView qr_show;
    private ImageView qRcode;
    private Button faceBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle);

        //获取actionbar
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        //设置返回主界面按钮
        actionBar.setDisplayHomeAsUpEnabled(true);

        //判断用户是否曾经成功登录过
        BmobUser bmobUser = BmobUser.getCurrentUser(BmobUser.class);
        final String name = bmobUser.getUsername();

        qrLayout = findViewById(R.id.QR_RelativeLayout);
        faceLayout = findViewById(R.id.Face_RelativeLayout);
        qr_show = findViewById(R.id.QRcode_tv);
        faceBt = findViewById(R.id.face_bt);
        qRcode = findViewById(R.id.settle_QRcode_iv);



        List<Integer> listx = UnfoldDetailsActivity.instance.getLists();
        String[] foodNames = new String[]{"拉面","火锅","章鱼烧","叉烧饭"};

        int s = 0;
        String msgString = name + "_";
        String dataString = "";
        String foodName=" ";
        for (int c : listx) {
            if (c == 1) {
                msgString += s;
                dataString += s;
                foodName+=foodNames[s]+" ";
                Log.d("listx" + s, ":" + c);
            }
            s++;
        }

        //打开应用中存储型如map格式数据的xml文件
        SharedPreferences shp = getSharedPreferences("ip_port", Context.MODE_PRIVATE);
        String msgIp = shp.getString("ip_string", null);
        int msgPortInt = Integer.parseInt(shp.getString("port_string", "1111"));

        // 客户端启动ClientThread线程创建网络连接、读取来自服务器的数据
        clientThread = new ClientThread(handler, msgIp, msgPortInt);
        new Thread(clientThread).start();

        //通知服务端使用人脸识别验证取餐
        faceBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Message msg = new Message();
                    msg.obj = name+"_face";
                    clientThread.revHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //设置对应取餐二维码
        Bitmap bitmap = QRCodeUtil.createQRImage(msgString, 400, 400, null, null);
        qRcode.setImageBitmap(bitmap);
        qr_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrLayout.setVisibility(View.VISIBLE);
                faceLayout.setVisibility(View.GONE);
                try {
                    Message msg = new Message();
                    msg.obj = name+"_QRcode";
                    clientThread.revHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(SettleActivity.this, UnfoldDetailsActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (qrLayout.getVisibility()==View.VISIBLE)
        {
            faceLayout.setVisibility(View.VISIBLE);
            qrLayout.setVisibility(View.GONE);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            Message msg = new Message();
            msg.obj = "exit";
            clientThread.revHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}