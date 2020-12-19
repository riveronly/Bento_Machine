package com.soulocean.bento_machine_c.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.soulocean.bento_machine_c.R;
import com.soulocean.bento_machine_c.entity.User;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import io.github.tonnyl.light.Light;

/**
 * 用户注册类，提交给Bomb后端云进行用户管理
 *
 * @author soulo
 */
public class EnrollActivity extends AppCompatActivity {

    private String account, password;
    private RelativeLayout relLayout;
    private EditText accountEdit;
    private EditText passwordEdit;
    private ImageView enrollImage;
    private User user;
    private String imageBase64 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

        //初始化Bomb
        Bmob.initialize(this, "74efe07868de32b6414777370f0246a3");

        //绑定View控件实例化为全局变量
        relLayout = findViewById(R.id.rellayoutview);
        Button enrollButton = findViewById(R.id.EnrollButton);
        accountEdit = findViewById(R.id.Enraccount);
        passwordEdit = findViewById(R.id.Enrpassword);
        enrollImage = findViewById(R.id.EnrPic);

        //点击事件：从相册里获取图片作为用户头像。#注1#
        enrollImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(captureIntent, 1);
            }
        });

        //给注册按钮绑定单击Click监听器
        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                account = accountEdit.getText().toString();
                password = passwordEdit.getText().toString();
                int accountTextSize = accountEdit.getText().length();
                int passwordTextSize = passwordEdit.getText().length();

                //先进行简单的对账户密码格式判断
                if (accountTextSize <= 5) {
                    Light.error(relLayout, "用户名需大于5位", Light.LENGTH_SHORT).show();
                } else if (passwordTextSize <= 6) {
                    Light.error(relLayout, "密码需大于6位", Light.LENGTH_SHORT).show();
                } else {
                    //符合格式的话就提交给服务器
                    user = new User();
                    user.setUsername(account);
                    user.setPassword(password);
                    user.setUser_pic(imageBase64);
                    user.signUp(new SaveListener<User>() {
                        @Override
                        public void done(User s, BmobException e) {
                            //服务器返回结果，错误码为Null时即为合法注册
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "注册成功\n" + s.getUsername(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EnrollActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                if (e.getErrorCode() == 9016) {
                                    Light.error(relLayout, "网络链接失败", Light.LENGTH_SHORT).show();
                                } else if (e.getErrorCode() == 202) {
                                    Light.error(relLayout, "用户名已被注册", Light.LENGTH_SHORT).show();
                                } else {
                                    Light.error(relLayout, "注册失败\n" + e.toString(), Light.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * #注1#
     * 接受返回结果码Code，进行相应处理（此处为从相册中获取到用户个人上传的图片并显示在相应处）
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String imagePath = null;
        String[] pogo = {MediaStore.Images.Media.DATA};
        final Uri uri = data.getData();
        assert uri != null;
        CursorLoader cursorLoader = new CursorLoader(this, uri, pogo, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        assert cursor != null;
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(pogo[0]));
        if (path != null && path.length() > 0) {
            imagePath = path;
        }
        cursor.close();
        int PHOTO_REQUEST_GALLERY = 1;
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            // 得到图片的全路径
            String strBase64 = Base64.encodeToString(imagePath.getBytes(), Base64.DEFAULT);
            imageBase64 = strBase64;
            enrollImage.setImageURI(uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 重写按键按下时逻辑，跳转到登录界面并关闭本界面。
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EnrollActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
