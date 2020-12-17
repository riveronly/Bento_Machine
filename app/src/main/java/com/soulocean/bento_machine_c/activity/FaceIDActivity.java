package com.soulocean.bento_machine_c.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.soulocean.bento_machine_c.entity.User_Pic;
import com.soulocean.bento_machine_c.R;
import com.soulocean.bento_machine_c.util.Sample;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * @author soulo
 */
public class FaceIDActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO = 111;
    private ImageView picture = null;
    private String imageBase = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        picture = findViewById(R.id.image);
        takePhoto();
    }

    public void takePhoto() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(captureIntent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PHOTO) {
                User_Pic user = User_Pic.getCurrentUser(User_Pic.class);
                String name = user.getUsername();

                String mess = Sample.uploadFace(imageBase, name);
                Toast toast = null;
                if ("success".equals(mess)) {
                    toast = Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast = Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT);
                    toast.show();
                }

                Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                picture.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                assert bitmap != null;
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytes = baos.toByteArray();
                try {
                    imageBase = Base64.encodeToString(bytes, Base64.NO_WRAP);
                } catch (Exception ignored) {
                }
            }
        }
    }
}
