package app.haiyunshan.whatsandroid;

import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.widget.ActionLayout;

public class TestActionLayoutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_action_layout);

        ActionLayout layout = findViewById(R.id.action_buttons);
        layout.setOnClickListener(v -> {
            if (v instanceof TextView) {
                Log.w("AA", "button = " + ((TextView)v).getText().toString());
            }
        });

        findViewById(R.id.action_tape).setOnClickListener(this::onTapeActionClick);
    }

    void onTapeActionClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_action) {
            ImageView actionView = (ImageView)view;
            if (actionView.getBackground() != null) {
                actionView.setBackground(null);
                actionView.setImageResource(R.drawable.test_ic_tape_play);
                actionView.setImageTintList(getResources().getColorStateList(R.color.test_selector_tape, null));
            } else {
                actionView.setBackgroundResource(R.drawable.shape_tape_ring);
                actionView.setImageResource(R.drawable.test_ic_tape_stop);
                actionView.setImageTintList(getResources().getColorStateList(R.color.test_selector_tape_stop, null));
            }
        }
    }
}
