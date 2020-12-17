package com.soulocean.bento_machine_c.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.soulocean.bento_machine_c.R;
import com.soulocean.bento_machine_c.util.ClientThread;
import com.soulocean.bento_machine_c.util.NotificationChannel;

import java.util.List;

import cn.bmob.v3.BmobUser;

public class PayAcitvity extends AppCompatActivity {
    //连接server
    Handler handler = new Handler() //①
    {
        public void handleMessage(Message msg) {
            // 如果消息来自于子线程
            // 将读取的内容追加显示在文本框中
            //tvServer.append(msg.obj.toString()+'\n');
        }

    };
    private ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_acitvity);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_home_black_24dp); //设置menu键得图标


        BmobUser bmobUser = BmobUser.getCurrentUser(BmobUser.class);
        String name = bmobUser.getUsername();

        SharedPreferences shp = getSharedPreferences("ip_port", Context.MODE_PRIVATE);

        String msg_ip = shp.getString("ip_string", null);
        int msg_port_int = Integer.parseInt(shp.getString("port_string", "1111"));

        clientThread = new ClientThread(handler, msg_ip, msg_port_int);
        // 客户端启动ClientThread线程创建网络连接、读取来自服务器的数据
        new Thread(clientThread).start();


        String[] foodnames = new String[]{"拉面", "火锅", "章鱼烧", "叉烧饭"};
        List<Integer> listx = UnfoldableDetailsActivity.instance.getLists();

        int s = 0;
        String msgstring = name + "_";
        String datastring = "";
        String foodname = " ";
        for (int c : listx) {
            if (c == 1) {
                msgstring += s;
                datastring += s;
                foodname += foodnames[s] + " ";
                Log.d("listx" + s, ":" + c);
            }
            s++;
        }

        final String finalMsgstring = msgstring;
        final String finalFoodname = foodname;

        ImageView button = findViewById(R.id.pay_wx_bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendFoodMsg_Notification(finalMsgstring, finalFoodname);
            }
        });

        ImageView button1 = findViewById(R.id.pay_zfb_bt);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendFoodMsg_Notification(finalMsgstring,finalFoodname);
            }
        });


    }

    private void SendFoodMsg_Notification(String finalMsgstring, String finalFoodname) {
        try {
            Message msg = new Message();
            msg.obj = "" + finalMsgstring;
            clientThread.revHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }


        NotificationChannel notificationChannel = new NotificationChannel(getApplicationContext());
        notificationChannel.createNotificationChannel();
        notificationChannel.sendNotificationMsg("订单完成", "您已下单:" + finalFoodname + ",请到便当机验证取餐");


        Toast.makeText(getApplicationContext(), "支付成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        //socket MSG:exit
        try {
            Message msg = new Message();
            msg.obj = "exit";
            clientThread.revHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(PayAcitvity.this, UnfoldableDetailsActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

}
