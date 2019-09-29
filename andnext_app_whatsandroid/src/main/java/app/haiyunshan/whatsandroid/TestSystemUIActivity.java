package app.haiyunshan.whatsandroid;

import android.graphics.Color;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class TestSystemUIActivity extends AppCompatActivity {

    ImageView pictureView;
    View toolbar;
    View bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.YELLOW);

        setContentView(R.layout.activity_test_system_ui);

        {

            View contentView = getContentView();

            int flags = View.SYSTEM_UI_FLAG_VISIBLE;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            contentView.setSystemUiVisibility(flags);

        }

        {
            pictureView = findViewById(R.id.iv_picture);

            pictureView.setOnClickListener(this::onPictureClick);
        }

        {
            bottomBar = findViewById(R.id.bottom_bar);
            toolbar = findViewById(R.id.toolbar);

        }

        {
            getWindow().getDecorView().setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    toolbar.setPadding(0, insets.getSystemWindowInsetTop(), 0, 0);
                    bottomBar.setPadding(0, 0, 0, insets.getSystemWindowInsetBottom());

                    return insets.consumeSystemWindowInsets();
                }
            });
        }

    }

    void onPictureClick(View view) {
        View contentView = getContentView();

        int flags = contentView.getSystemUiVisibility();
        if ((flags & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) {
            flags &= (~View.SYSTEM_UI_FLAG_FULLSCREEN);
            flags &= (~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            toolbar.setVisibility(View.INVISIBLE);
            bottomBar.setVisibility(View.INVISIBLE);
        } else {
            flags |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            flags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;


            toolbar.setVisibility(View.INVISIBLE);
            bottomBar.setVisibility(View.INVISIBLE);
        }

        contentView.setSystemUiVisibility(flags);

    }

    View getContentView() {
        View view = getWindow().getDecorView();
        return view;
    }
}
