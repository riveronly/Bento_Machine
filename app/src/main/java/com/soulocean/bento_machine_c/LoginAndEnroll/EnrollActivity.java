package com.soulocean.bento_machine_c.LoginAndEnroll;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.soulocean.bento_machine_c.R;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import io.github.tonnyl.light.Light;

public class EnrollActivity extends AppCompatActivity {

    String account, password, stunumber;

    private RelativeLayout relLayout;
    private EditText Enraccountedit, Enrpasswordedit, StuNumber;
    private ImageView pic;
    private int PHOTO_REQUEST_GALLERY = 1;
    private User_Pic bu;
    private String image_base64= null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

        Bmob.initialize(this, "74efe07868de32b6414777370f0246a3");


        relLayout = findViewById(R.id.rellayoutview);
        Button enrollbuttom = findViewById(R.id.EnrollButtom);
        Enraccountedit = findViewById(R.id.Enraccount);
        Enrpasswordedit = findViewById(R.id.Enrpassword);
        TextView enroll_tipText = findViewById(R.id.Enroll_tipText);
        pic = findViewById(R.id.EnrPic);


        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(captureIntent,1);

            }

        });


        //注册
        enrollbuttom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = Enraccountedit.getText().toString();
                password = Enrpasswordedit.getText().toString();

                int aeesize = Enraccountedit.getText().length();
                int peesize = Enrpasswordedit.getText().length();


                if (aeesize <= 5) {
                    Light.error(relLayout, "用户名需大于5位", Light.LENGTH_SHORT).show();
                } else if (peesize <= 6) {
                    Light.error(relLayout, "密码需大于6位", Light.LENGTH_SHORT).show();
                } else {

                    bu = new User_Pic();
                    bu.setUsername(account);
                    bu.setPassword(password);
                    bu.setUser_pic(image_base64);

                    bu.signUp(new SaveListener<User_Pic>() {
                        @Override
                        public void done(User_Pic s, BmobException e) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String picPath = null;


        String[] pojo = {MediaStore.Images.Media.DATA};
        final Uri uri = data.getData();
        assert uri != null;
        CursorLoader cursorLoader = new CursorLoader(this, uri, pojo, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        assert cursor != null;
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(pojo[0]));
        if (path != null && path.length() > 0) {
            picPath = path;
        }
        cursor.close();




        if (requestCode == PHOTO_REQUEST_GALLERY) {

            // 从相册返回的数据
            // 得到图片的全路径

            String strBase64 = Base64.encodeToString(picPath.getBytes(), Base64.DEFAULT);
            image_base64 = strBase64;
            pic.setImageURI(uri);

        }


        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EnrollActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }


}
