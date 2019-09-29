package app.haiyunshan.whatsandroid;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.dialog.PopupMenuDialogFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onPopupClick(View view) {
        PopupMenuDialogFragment f = new PopupMenuDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("menuRes", R.menu.menu_popup);
        f.setArguments(bundle);

        f.show(getSupportFragmentManager(), "dialog");
    }
}
