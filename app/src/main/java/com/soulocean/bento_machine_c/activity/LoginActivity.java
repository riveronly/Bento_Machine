package com.soulocean.bento_machine_c.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.soulocean.bento_machine_c.R;

import java.util.Objects;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author soulo
 */
public class LoginActivity extends AppCompatActivity {
    String account, password;
    private EditText accountEdit, passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();

        //绑定控件
        final TextView textView = findViewById(R.id.loginErrtext);
        TextView newAccount = findViewById(R.id.newaccountc);
        Button loginBottom = findViewById(R.id.loginbuttom);
        accountEdit = findViewById(R.id.account);
        passwordEdit = findViewById(R.id.password);
        account = accountEdit.getText().toString();
        password = passwordEdit.getText().toString();

        //初始化Bomb
        Bmob.initialize(this, "74efe07868de32b6414777370f0246a3");

        //判断用户是否曾经成功登录过
        BmobUser bmobUser = BmobUser.getCurrentUser(BmobUser.class);
        if (bmobUser != null) {
            // 允许用户使用应用
            Intent intent = new Intent(LoginActivity.this, UnfoldableDetailsActivity.class);
            startActivity(intent);
            finish();
        }

        //跳转到注册界面
        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, EnrollActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //简单的登陆判断
        loginBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = accountEdit.getText().toString();
                password = passwordEdit.getText().toString();
                textView.setText("");
                int aesize = accountEdit.getText().length();
                int pesize = passwordEdit.getText().length();
                if (aesize == 0 || pesize == 0) {
                    Toast.makeText(getApplicationContext(), "请输入用户名或密码", Toast.LENGTH_SHORT).show();
                } else if (aesize < 5) {
                    Toast.makeText(getApplicationContext(), "用户名需大于5位", Toast.LENGTH_SHORT).show();
                } else if (pesize < 6) {
                    Toast.makeText(getApplicationContext(), "密码需大于6位", Toast.LENGTH_SHORT).show();
                } else {
                    BmobUser bu2 = new BmobUser();
                    bu2.setUsername(account);
                    bu2.setPassword(password);
                    bu2.login(new SaveListener<BmobUser>() {
                        @Override
                        public void done(BmobUser bmobUser, BmobException e) {
                            if (e == null) {
                                //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                                //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                                Intent intent = new Intent(LoginActivity.this, UnfoldableDetailsActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                if (e.getErrorCode() == 101) {
                                    Toast.makeText(getApplicationContext(), "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                                } else if (e.getErrorCode() == 9016) {
                                    Toast.makeText(getApplicationContext(), "请连接网络", Toast.LENGTH_SHORT).show();
                                }
                                textView.setText(e.toString());
                            }
                        }
                    });
                }
            }
        });
    }
}