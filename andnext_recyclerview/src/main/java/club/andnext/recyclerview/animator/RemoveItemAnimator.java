package club.andnext.recyclerview.animator;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;

public class RemoveItemAnimator extends FadeInDownAnimator {

    long removeDelayExpire;
    long addDelayExpire;

    @Override
    protected void animateRemoveImpl(RecyclerView.ViewHolder holder) {

        ViewCompat.animate(holder.itemView)
                .translationY(0)
                .alpha(1)
                .setDuration(getRemoveDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultRemoveVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder))
                .start();
    }

    @Override
    public long getRemoveDelay(RecyclerView.ViewHolder holder) {
        if (removeDelayExpire > System.currentTimeMillis()) {
            return 0;
        }

        return super.getRemoveDelay(holder);
    }

    @Override
    public long getAddDelay(final RecyclerView.ViewHolder holder) {
        if (addDelayExpire > System.currentTimeMillis()) {
            return 0;
        }

        return Math.abs(holder.getLayoutPosition() * getAddDuration() / 4);

//        return super.getAddDelay(holder);
    }

    public void setRemoveDelayExpire(long duration) {
        this.removeDelayExpire = System.currentTimeMillis() + duration;
    }

    public void setAddDelayExpire(long duration) {
        this.addDelayExpire = System.currentTimeMillis() + duration;
    }
}
