package app.haiyunshan.whatsnote.record.viewholder;

import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;

public class FolderViewHolder extends RecordViewHolder {

    @Keep
    public FolderViewHolder(Callback callback, View itemView) {
        super(callback, itemView);
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        {
            view.findViewById(R.id.iv_chevron).setVisibility(View.VISIBLE);
        }
    }

    @Override
    CharSequence formatSize(RecordEntity item) {
        return item.getSize() + " 项";
    }

}
