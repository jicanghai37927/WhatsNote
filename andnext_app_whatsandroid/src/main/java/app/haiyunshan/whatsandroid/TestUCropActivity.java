package app.haiyunshan.whatsandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.ucrop.UCrop;

import java.io.File;

public class TestUCropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ucrop);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == UCrop.RESULT_ERROR) {
            Throwable e = (Throwable)(data.getSerializableExtra(UCrop.EXTRA_ERROR));
            e.printStackTrace();
        }

        Log.w("AA", "resultCode = " + resultCode);
    }

    void onCropPortrait(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "ucrop.jpg");
        File target = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "portrait.png");

        UCrop uCrop = UCrop.of(Uri.fromFile(file), Uri.fromFile(target));
        uCrop.withAspectRatio(1, 1);

        {
            UCrop.Options options = new UCrop.Options();
            options.setCircleDimmedLayer(true);
            options.setHideBottomControls(true);
            options.setCompressionFormat(Bitmap.CompressFormat.PNG);
            options.setShowCropGrid(false);
            options.setShowCropFrame(false);

            uCrop.withOptions(options);

        }

        uCrop.start(this);
    }

    void onCropVisionClick(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "ucrop.jpg");
        File target = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "vision.png");

        UCrop uCrop = UCrop.of(Uri.fromFile(file), Uri.fromFile(target));
        uCrop.withAspectRatio(2688.f / 1242, 1);

        {
            UCrop.Options options = new UCrop.Options();
            options.setCircleDimmedLayer(false);
            options.setHideBottomControls(true);
            options.setCompressionFormat(Bitmap.CompressFormat.PNG);
            options.setShowCropGrid(false);
            options.setShowCropFrame(false);

            uCrop.withOptions(options);

        }

        uCrop.start(this);
    }

}
