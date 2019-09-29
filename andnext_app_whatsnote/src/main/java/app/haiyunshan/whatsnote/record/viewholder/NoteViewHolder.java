package app.haiyunshan.whatsnote.record.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;

public class NoteViewHolder extends RecordViewHolder {

    @Keep
    public NoteViewHolder(Callback callback, View itemView) {
        super(callback, itemView);
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);
    }

    @Override
    CharSequence formatSize(RecordEntity item) {
        StringBuilder sb = new StringBuilder();

        {
            long size = item.getSize();
            long c = 1024;
            long b = 1024 * c;
            long a = 1024 * b;
            if (size > a) {
                sb.append(String.format("%.1f", size * 1.f / a));
                sb.append(" GB");
            } else if (size > b) {
                sb.append(String.format("%.1f", size * 1.f / b));
                sb.append(" MB");
            } else if (size > c) {
                sb.append(String.format("%.1f", size * 1.f / c));
                sb.append(" KB");
            } else {
                sb.append(size);
                sb.append(" bytes");
            }
        }

        return sb;
    }

}
