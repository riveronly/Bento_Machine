package com.soulocean.bento_machine_c.activity;

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
import com.soulocean.bento_machine_c.R;
import com.soulocean.bento_machine_c.adapter.PaintingsAdapter;
import com.soulocean.bento_machine_c.entity.Painting;
import com.soulocean.bento_machine_c.entity.User;
import com.soulocean.bento_machine_c.foldablelayoutapi.BaseActivity;
import com.soulocean.bento_machine_c.foldablelayoutapi.GlideHelper;
import com.soulocean.bento_machine_c.foldablelayoutapi.SpannableBuilder;
import com.soulocean.bento_machine_c.foldablelayoutapi.UnfoldableView;
import com.soulocean.bento_machine_c.foldablelayoutapi.Views;
import com.soulocean.bento_machine_c.foldablelayoutapi.shading.GlanceFoldShading;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * 主界面，可折叠布局
 * @author soulo
 */
public class UnfoldDetailsActivity extends BaseActivity {

    @SuppressLint("StaticFieldLeak")
    public static UnfoldDetailsActivity instance = null;
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
            }
        });

        paintingsAdapter.setOnItemlongClickListener(new PaintingsAdapter.OnItemlongLitener() {
            @Override
            public void onItemlongClick(View view, int position) {
            }
        });

        //初始化折叠动画
        listView.setAdapter(paintingsAdapter);
        Init_UnfoldView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_ip:
                        Intent toMain = new Intent(UnfoldDetailsActivity.this, IpPortActivity.class);
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
                                Intent toPay = new Intent(UnfoldDetailsActivity.this, PayActivity.class);
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
                            Intent toSettle = new Intent(UnfoldDetailsActivity.this, SettleActivity.class);
                            startActivity(toSettle);
                            currentPay = false;
                        } else {
                            Toast.makeText(getApplicationContext(), "您还未完成支付", Toast.LENGTH_SHORT).show();
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
                    case R.id.nav_faceId:
                        Intent toface = new Intent(UnfoldDetailsActivity.this, FaceIDActivity.class);
                        startActivity(toface);
                        break;
                    case R.id.nav_logout:
                        BmobUser.logOut();
                        Intent toLogin = new Intent(UnfoldDetailsActivity.this, LoginActivity.class);
                        startActivity(toLogin);
                        finish();
                        break;
                    default:
                }
                drawer.closeDrawers();
                return false;
            }
        });
        View headView = navigationView.getHeaderView(0);
        TextView userName = headView.findViewById(R.id.nav_header_username);
        ImageView userPic = headView.findViewById(R.id.nav_header_userpic);
        User user = User.getCurrentUser(User.class);
        String name = user.getUsername();
        if (name != null) {
            userName.setText(name);
        }
        String pic64 = (String) user.getUser_pic();
        if (pic64 != null && pic64.length() > 1) {
            //base64解码
            String str2 = new String(Base64.decode(pic64.getBytes(), Base64.DEFAULT));
            userPic.setImageURI(Uri.fromFile(new File(str2)));
        }
        Toast.makeText(getApplicationContext(), "欢迎 " + name, Toast.LENGTH_SHORT).show();
    }

    public List<Integer> getLists() {
        return listc;
    }

    /**
     * 初始化折叠视图
     */
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

    /**
     * 打开折叠布局后的详细界面初始化
     * @param coverView
     * @param painting
     */
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
