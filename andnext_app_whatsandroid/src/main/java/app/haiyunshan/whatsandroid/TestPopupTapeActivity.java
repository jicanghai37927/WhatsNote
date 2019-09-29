package app.haiyunshan.whatsandroid;

import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

public class TestPopupTapeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_popup_tape);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }

}
