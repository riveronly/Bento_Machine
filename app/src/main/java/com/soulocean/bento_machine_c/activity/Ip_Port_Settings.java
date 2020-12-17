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
public class Ip_Port_Settings extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_port_settings);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_home_black_24dp); //设置menu键得图标
        SharedPreferences shp = getSharedPreferences("ip_port", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = shp.edit();

        TextView msg_default_ip_port = findViewById(R.id.msg_default_ip_port);
        msg_default_ip_port.setTextSize(30);
        String ip = shp.getString("ip_string", "");
        String port = shp.getString("port_string", "");
        msg_default_ip_port.setText("ip:" + ip + "\n" + "port:" + port);

        Button button = findViewById(R.id.main_bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText ip = findViewById(R.id.msg_ip);
                EditText port = findViewById(R.id.msg_port);
                Intent intent = new Intent(Ip_Port_Settings.this, UnfoldableDetailsActivity.class);
                if (ip != null && ip.length() > 0) {
                    editor.putString("ip_string", ip.getText().toString());
                    editor.putString("port_string", port.getText().toString());
                    editor.apply();
                }
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(Ip_Port_Settings.this, UnfoldableDetailsActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
