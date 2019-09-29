package club.andnext.recyclerview.decoration;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

public class SectionDividerDecoration extends MarginDividerDecoration {

    public SectionDividerDecoration(Context context) {
        super(context);
    }

    @Override
    int getMargin(RecyclerView.ViewHolder holder) {
        if (holder.getAdapterPosition() < 0) {
            return super.getMargin(holder);
        }

        if (holder instanceof Adapter) {
            return ((Adapter)holder).getMargin(this);
        }

        return super.getMargin(holder);
    }

    @Override
    boolean isVisible(RecyclerView.ViewHolder holder) {
        if (holder.getAdapterPosition() < 0) {
            return super.isVisible(holder);
        }

        if (holder instanceof Adapter) {
            return ((Adapter)holder).isVisible(this);
        }

        return super.isVisible(holder);
    }

    /**
     *
     */
    public interface Adapter {

        int getMargin(SectionDividerDecoration decoration);

        default boolean isVisible(SectionDividerDecoration decoration) {
            return true;
        }
    }
}
