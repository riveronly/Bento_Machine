package com.soulocean.bento_machine_c.UnfoldableVIewActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.soulocean.bento_machine_c.IP_Port_Activity.Ip_Port_Settings;
import com.soulocean.bento_machine_c.LoginAndEnroll.LoginActivity;
import com.soulocean.bento_machine_c.LoginAndEnroll.User_Pic;
import com.soulocean.bento_machine_c.PayAcitvity;
import com.soulocean.bento_machine_c.PublicApi.BaseActivity;
import com.soulocean.bento_machine_c.PublicApi.GlideHelper;
import com.soulocean.bento_machine_c.PublicApi.SpannableBuilder;
import com.soulocean.bento_machine_c.PublicApi.UnfoldableView;
import com.soulocean.bento_machine_c.PublicApi.Views;
import com.soulocean.bento_machine_c.PublicApi.shading.GlanceFoldShading;
import com.soulocean.bento_machine_c.R;
import com.soulocean.bento_machine_c.SettleActivity;
import com.soulocean.bento_machine_c.UpLoadFaceID;
import com.soulocean.bento_machine_c.items_Adapter.Painting;
import com.soulocean.bento_machine_c.items_Adapter.PaintingsAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class UnfoldableDetailsActivity extends BaseActivity {


    @SuppressLint("StaticFieldLeak")
    public static UnfoldableDetailsActivity instance = null;
    private View listTouchInterceptor;
    private View detailsLayout;
    private UnfoldableView unfoldableView;
    private DrawerLayout drawer;
    private List<Integer> listc = new ArrayList<>();
    private boolean currentSettle = false;
    private boolean currentPay = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unfoldable_details);

        instance = this;


        ListView listView = Views.find(this, R.id.list_view);
        PaintingsAdapter paintingsAdapter = new PaintingsAdapter(this);

        listc = paintingsAdapter.getLists();


        paintingsAdapter.setOnItemClickLitener(new PaintingsAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                // Toast.makeText(getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();


            }
        });


        paintingsAdapter.setOnItemlongClickListener(new PaintingsAdapter.OnItemlongLitener() {
            @Override
            public void onItemlongClick(View view, int position) {
                //  Toast.makeText(getApplicationContext(), "long:" + position, Toast.LENGTH_SHORT).show();
            }
        });

        listView.setAdapter(paintingsAdapter);


        Init_UnfoldView();//初始化折叠动画

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_dashboard_black_24dp); //设置menu键得图标


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_ip:
                        Intent toMain = new Intent(UnfoldableDetailsActivity.this, Ip_Port_Settings.class);
                        startActivity(toMain);
                        break;

                    case R.id.nav_pay:
                        if (!currentPay) {
                            for (int i = 0; i < listc.size(); i++) {
                                if (listc.get(i).equals(1)) {
                                    currentPay = true;
                                    break;
                                }
                            }
                            if (currentPay) {
                                Intent toPay = new Intent(UnfoldableDetailsActivity.this, PayAcitvity.class);
                                startActivity(toPay);
                            } else {
                                Toast.makeText(getApplicationContext(), "您还未选择菜品", Toast.LENGTH_SHORT).show();
                                drawer.closeDrawer(GravityCompat.START);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "您已经支付完成", Toast.LENGTH_SHORT).show();
                            drawer.closeDrawer(GravityCompat.START);
                        }

                        break;
                    case R.id.nav_settle:
                        if (currentPay) {
                            currentSettle = true;
                        }
                        if (currentSettle) {
                            currentSettle = false;
                            Intent toSettle = new Intent(UnfoldableDetailsActivity.this, SettleActivity.class);
                            startActivity(toSettle);
                            currentPay = false;
                        } else {
                            Toast.makeText(getApplicationContext(), "您还未完成支付", Toast.LENGTH_SHORT).show();
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
                    case R.id.nav_faceId:

                        Intent toface = new Intent(UnfoldableDetailsActivity.this, UpLoadFaceID.class);
                        startActivity(toface);
                        break;

                    case R.id.nav_logout:
                        BmobUser.logOut();
                        Intent toLogin = new Intent(UnfoldableDetailsActivity.this, LoginActivity.class);
                        startActivity(toLogin);
                        finish();
                        break;


                }
                drawer.closeDrawers();
                return false;

            }
        });


        View HeadView = navigationView.getHeaderView(0);
        TextView username = HeadView.findViewById(R.id.nav_header_username);
        ImageView userpic = HeadView.findViewById(R.id.nav_header_userpic);


        User_Pic user = User_Pic.getCurrentUser(User_Pic.class);

        String name = user.getUsername();
        if (name != null) {
            username.setText(name);
        }

        String pic64 = (String) user.getUser_pic();
        if (pic64 != null && pic64.length() > 1) {
            //base64解码
            String str2 = new String(Base64.decode(pic64.getBytes(), Base64.DEFAULT));
            userpic.setImageURI(Uri.fromFile(new File(str2)));
        }


        Toast.makeText(getApplicationContext(), "欢迎 " + name, Toast.LENGTH_SHORT).show();


    }

    public List<Integer> getLists() {
        return listc;
    }

    private void Init_UnfoldView() {
        listTouchInterceptor = Views.find(this, R.id.touch_interceptor_view);
        listTouchInterceptor.setClickable(false);

        detailsLayout = Views.find(this, R.id.details_layout);
        detailsLayout.setVisibility(View.INVISIBLE);

        unfoldableView = Views.find(this, R.id.unfoldable_view);

        Bitmap glance = BitmapFactory.decodeResource(getResources(), R.drawable.unfold_glance);
        unfoldableView.setFoldShading(new GlanceFoldShading(glance));


        unfoldableView.setOnFoldingListener(new UnfoldableView.SimpleFoldingListener() {
            @Override
            public void onUnfolding(UnfoldableView unfoldableView) {
                listTouchInterceptor.setClickable(true);
                detailsLayout.setVisibility(View.VISIBLE);
                //  Toast.makeText(getApplicationContext(), "开打中", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnfolded(UnfoldableView unfoldableView) {
                listTouchInterceptor.setClickable(false);
                // Toast.makeText(getApplicationContext(), "打开完成", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFoldingBack(UnfoldableView unfoldableView) {
                listTouchInterceptor.setClickable(true);
                //   Toast.makeText(getApplicationContext(), "打开后返回", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFoldedBack(UnfoldableView unfoldableView) {
                listTouchInterceptor.setClickable(false);
                detailsLayout.setVisibility(View.INVISIBLE);
                //  Toast.makeText(getApplicationContext(), "打开后返回完成", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        // 返回键: 折叠开着就将其关闭
        if (unfoldableView != null
                && (unfoldableView.isUnfolded() || unfoldableView.isUnfolding())) {
            unfoldableView.foldBack();
        }
        // 返回键: 侧滑开着就将其关闭
        else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        //把程序推出栈顶
        else {
            super.onBackPressed();
        }


    }

    public void openDetails(View coverView, Painting painting) {
        final ImageView image = Views.find(detailsLayout, R.id.details_image);
        final TextView title = Views.find(detailsLayout, R.id.details_title);
        final TextView description = Views.find(detailsLayout, R.id.details_text);

        GlideHelper.loadPaintingImage(image, painting);
        title.setText(painting.getTitle());
        description.setText(painting.getLocation());

        SpannableBuilder builder = new SpannableBuilder(this);
        builder
                .createStyle().setFont(Typeface.DEFAULT_BOLD).apply()
                //.append(R.string.year).append(": ")
                .clearStyle()
                //  .append(painting.getYear()).append("\n")
                .createStyle().setFont(Typeface.DEFAULT_BOLD).apply()
                //.append(R.string.location).append(": ")
                .clearStyle()
                .append(painting.getLocation());
        //;
        description.setText(builder.build());

        unfoldableView.unfold(coverView, detailsLayout);
    }

}
