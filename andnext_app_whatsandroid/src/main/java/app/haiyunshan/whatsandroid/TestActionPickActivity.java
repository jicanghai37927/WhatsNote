package app.haiyunshan.whatsandroid;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.utils.ContentUtils;
import club.andnext.utils.PackageUtils;
import club.andnext.utils.UriUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TestActionPickActivity extends AppCompatActivity {

    TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_action_pick);

        this.resultView = findViewById(R.id.tv_result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            ArrayList<Uri> list = new ArrayList<>();

            {
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);

                        Log.w("AA", "text = " + item.getText());
                        Log.w("AA", "html = " + item.getHtmlText());
                        Uri uri = item.getUri();

                        list.add(uri);
                    }
                }

                if (list.isEmpty()) {

                    Log.w("AA", "type = " + data.getType());

                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        Log.w("AA", bundle.toString());
                    } else {
                        Log.w("AA", "bundle = null");
                    }


                    Uri uri = data.getData();
                    if (uri != null) {
                        list.add(uri);
                    }
                }
            }

            for (Uri uri: list) {
                Log.w("AA", "uri = " + uri.toString());
            }

            String text = Arrays.toString(list.toArray());
            text = text.replace(", ", "\n");

            resultView.setText(text.substring(1, text.length() - 1));

            openImage(list.get(0));

        } else {
            resultView.setText("取消选择");
        }
    }

    void onVideoClick(View view) {
        choose("video/*", null, 10);
    }

    void onAudioClick(View view) {
        choose("audio/*",
                new String[] {
                    "audio/*"
                },
                100
        );
    }

    void onImageClick(View view) {
        choose("image/*", null, 100);
    }

    void onImageVideoClick(View view) {
        choose("*/*",
                new String[] {
                        "video/*",
                        "image/*"
                },
                100
        );
    }

    void onImageVideoAudioClick(View view) {
        choose("*/*",
                new String[] {
                        "video/*",
                        "image/*",
                        "audio/*"
                },
                100
        );
    }

    private void choose(String type, String[] mimeTypes, int requestCode) {
        PackageUtils.pick(this, null,
                type, mimeTypes,
                false, requestCode);
    }

    private void openImage(Uri uri) {

        Context context = this;

        File file = context.getExternalCacheDir();
        file = new File(file, "test_picture.pic");
        file.getAbsoluteFile().getParentFile().mkdirs();

        ContentUtils.copy(context, uri, file);
        if (file.exists()) {
            uri = UriUtils.fromFile(context, file);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");

//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //注意加上这句话

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Log.w("AA", e.toString());
            e.printStackTrace();
        }
    }
}
