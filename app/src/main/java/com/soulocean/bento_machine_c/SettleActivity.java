package com.soulocean.bento_machine_c;

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

import com.soulocean.bento_machine_c.QR_Code.QRCodeUtil;
import com.soulocean.bento_machine_c.Socket.ClientThread;
import com.soulocean.bento_machine_c.UnfoldableVIewActivity.UnfoldableDetailsActivity;

import java.util.List;

import cn.bmob.v3.BmobUser;

public class SettleActivity extends AppCompatActivity {

    private ClientThread clientThread;
    //连接server
    Handler handler = new Handler() //①
    {
        public void handleMessage(Message msg) {
            // 如果消息来自于子线程
            // 将读取的内容追加显示在文本框中
            //tvServer.append(msg.obj.toString()+'\n');
        }

    };

    private RelativeLayout qr_layout;
    private RelativeLayout face_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_home_black_24dp); //设置menu键得图标


        BmobUser bmobUser = BmobUser.getCurrentUser(BmobUser.class);
        final String name = bmobUser.getUsername();


        List<Integer> listx = UnfoldableDetailsActivity.instance.getLists();



        String[] foodnames = new String[]{"拉面","火锅","章鱼烧","叉烧饭"};

        int s = 0;
        String msgstring = name + "_";
        String datastring = "";
        String foodname=" ";

        for (int c : listx) {
            if (c == 1) {
                msgstring += s;
                datastring += s;
                foodname+=foodnames[s]+" ";
                Log.d("listx" + s, ":" + c);
            }
            s++;
        }



        SharedPreferences shp = getSharedPreferences("ip_port", Context.MODE_PRIVATE);

        String msg_ip = shp.getString("ip_string", null);
        int msg_port_int = Integer.parseInt(shp.getString("port_string", "1111"));

        clientThread = new ClientThread(handler, msg_ip, msg_port_int);
        // 客户端启动ClientThread线程创建网络连接、读取来自服务器的数据
        new Thread(clientThread).start();

       // Toast.makeText(getApplicationContext(), ""+msgstring, Toast.LENGTH_SHORT).show();

        Button button = findViewById(R.id.face_bt);
        final String finalMsgstring = msgstring;
        button.setOnClickListener(new View.OnClickListener() {
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



        ImageView QRcode = findViewById(R.id.settle_QRcode_iv);
        Bitmap bitmap = QRCodeUtil.createQRImage(msgstring, 400, 400, null, null);
        QRcode.setImageBitmap(bitmap);

/*

        NotificationCannel notificationCannel = new NotificationCannel(this);
        notificationCannel.CreateNotificationChannel();
        notificationCannel.sendNotificationMsg("订单完成", "您已下单:" + foodname + ",请到便当机验证取餐");
*/


        qr_layout = findViewById(R.id.QR_RelativeLayout);
        face_layout = findViewById(R.id.Face_RelativeLayout);

        TextView QR_show = findViewById(R.id.QRcode_tv);
        QR_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qr_layout.setVisibility(View.VISIBLE);
                face_layout.setVisibility(View.GONE);
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
            Intent intent = new Intent(SettleActivity.this, UnfoldableDetailsActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (qr_layout.getVisibility()==View.VISIBLE)
        {
            face_layout.setVisibility(View.VISIBLE);
            qr_layout.setVisibility(View.GONE);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        //socket MSG:exit
        try {
            Message msg = new Message();
            msg.obj = "exit";
            clientThread.revHandler.sendMessage(msg);
            //input_msg.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}


  /*  File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        //启动相机程序
        Uri imageUri = FileProvider.getUriForFile(SettleActivity.this, "tiantian", outputImage);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 设置action
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);//指定图片的输出地址
        intent.putExtra("camerasensortype", 1); // 调用前置摄像头
        intent.putExtra("autofocus", true); // 自动对焦
        intent.putExtra("fullScreen", false); // 全屏
        intent.putExtra("showActionIcons", false);
        startActivityForResult(intent, 0);*/
