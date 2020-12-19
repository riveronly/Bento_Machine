package com.soulocean.bento_machine_c.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.soulocean.bento_machine_c.R;

/**
 * @author soulo
 */
public class IpPortActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_port_settings);

        //获取actionbar
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        //设置返回主界面按钮，#注2
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView msgDefaultIpPort = findViewById(R.id.msg_default_ip_port);
        Button button = findViewById(R.id.main_bt);

        //打开应用中存储型如map格式数据的xml文件
        SharedPreferences shp = getSharedPreferences("ip_port", Context.MODE_PRIVATE);
        //从SharedPreferences中获取编辑editor
        editor = shp.edit();
        //从SharedPreferences中获取存储的数据
        String ip = shp.getString("ip_string", "");
        String port = shp.getString("port_string", "");
        msgDefaultIpPort.setText("ip:" + ip + "\n" + "port:" + port);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ip = findViewById(R.id.msg_ip);
                EditText port = findViewById(R.id.msg_port);
                Intent intent = new Intent(IpPortActivity.this, UnfoldableDetailsActivity.class);
                if (ip != null && ip.length() > 0) {
                    //将输入的IP和port存储
                    editor.putString("ip_string", ip.getText().toString());
                    editor.putString("port_string", port.getText().toString());
                    editor.apply();
                }
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * #注2
     * 当actionbar中的按钮被点击时的响应
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(IpPortActivity.this, UnfoldableDetailsActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
