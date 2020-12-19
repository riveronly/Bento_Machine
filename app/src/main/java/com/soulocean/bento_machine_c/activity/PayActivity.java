package com.soulocean.bento_machine_c.activity;

import android.annotation.SuppressLint;
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

/**
 * @author soulo
 */
public class PayActivity extends AppCompatActivity {
    //使用Handler连接server端
    Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
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

        //获取actionbar
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        //设置返回主界面按钮
        actionBar.setDisplayHomeAsUpEnabled(true);

        //判断用户是否曾经成功登录过
        BmobUser bmobUser = BmobUser.getCurrentUser(BmobUser.class);
        String name = bmobUser.getUsername();

        //打开应用中存储型如map格式数据的xml文件
        SharedPreferences shp = getSharedPreferences("ip_port", Context.MODE_PRIVATE);
        String msgIp = shp.getString("ip_string", null);
        int msgPortInt = Integer.parseInt(shp.getString("port_string", "1111"));

        // 客户端启动ClientThread线程创建网络连接、读取来自服务器的数据
        clientThread = new ClientThread(handler, msgIp, msgPortInt);
        new Thread(clientThread).start();

        String[] foodNames = new String[]{"拉面", "火锅", "章鱼烧", "叉烧饭"};
        List<Integer> listx = UnfoldDetailsActivity.instance.getLists();

        int s = 0;
        String msgstring = name + "_";
        String dataString = "";
        String foodname = " ";
        for (int c : listx) {
            if (c == 1) {
                msgstring += s;
                dataString += s;
                foodname += foodNames[s] + " ";
                Log.d("listx" + s, ":" + c);
            }
            s++;
        }

        final String finalMsgstring = msgstring;
        final String finalFoodname = foodname;

        ImageView weChatPayBt = findViewById(R.id.pay_wx_bt);
        weChatPayBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通知栏进行通知
                SendFoodMsg_Notification(finalMsgstring, finalFoodname);
            }
        });

        ImageView zfbPayBt = findViewById(R.id.pay_zfb_bt);
        zfbPayBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通知栏进行通知
                SendFoodMsg_Notification(finalMsgstring, finalFoodname);
            }
        });
    }

    /**
     * 通知消息
     *
     * @param finalMsgstring
     * @param finalFoodname
     */
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

    /**
     * 当程序被销毁时
     */
    @Override
    protected void onDestroy() {
        try {
            //给服务器发送离线消息
            Message msg = new Message();
            msg.obj = "exit";
            clientThread.revHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * 菜单栏选中函数
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(PayActivity.this, UnfoldDetailsActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
