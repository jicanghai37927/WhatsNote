package app.haiyunshan.whatsandroid;

import android.animation.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;

public class TestLayoutTransitionActivity extends AppCompatActivity {

    ViewGroup swipeView;
    View iconView;
    View removeBtn;
    View dragBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_layout_transition);

        this.swipeView = findViewById(R.id.swipe_view);
        LayoutTransition t = swipeView.getLayoutTransition();
        customLayoutTransition(t);

        this.iconView = findViewById(R.id.iv_icon);
        this.removeBtn = findViewById(R.id.btn_remove);
        this.dragBtn = findViewById(R.id.btn_drag);

        findViewById(R.id.btn_toggle).setOnClickListener(v -> {
            if (removeBtn.getVisibility() == View.VISIBLE) {
                removeBtn.setVisibility(View.GONE);
                dragBtn.setVisibility(View.GONE);
            } else {
                removeBtn.setVisibility(View.VISIBLE);
                dragBtn.setVisibility(View.VISIBLE);
            }
        });

        swipeView.post(() -> {
            int right = removeBtn.getMeasuredWidth();

            Log.w("AA", "remove right = " + right);
            removeBtn.setTranslationX(-iconView.getRight());
            removeBtn.setAlpha(0);

//            int left = dragBtn.getLeft();
//            Log.w("AA", "drag left = " + left);
            dragBtn.setTranslationX(iconView.getRight());
        });
    }

    void customLayoutTransition(LayoutTransition transition) {
        {
//            transition.setAnimator(LayoutTransition.APPEARING, null);
//            transition.setAnimator(LayoutTransition.DISAPPEARING, null);

            transition.setInterpolator(LayoutTransition.APPEARING, transition.getInterpolator(LayoutTransition.CHANGE_APPEARING));
            transition.setInterpolator(LayoutTransition.DISAPPEARING, transition.getInterpolator(LayoutTransition.CHANGE_DISAPPEARING));
        }

        {
            transition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
            transition.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
            transition.setStartDelay(LayoutTransition.APPEARING, 0);

        }

        {
            transition.addTransitionListener(new LayoutTransition.TransitionListener() {

                @Override
                public void startTransition(LayoutTransition transition,
                                            ViewGroup container, View view, int transitionType) {

                    {
                        String text = "";
                        switch (view.getId()) {
                            case R.id.btn_remove: {
                                text = "remove";
                                break;
                            }
                            case R.id.btn_drag: {
                                text = "drag";
                                break;
                            }
                            case R.id.tv_name: {
                                text = "name";
                                break;
                            }
                            case R.id.iv_icon: {
                                text = "icon";
                                break;
                            }
                        }
                        Log.w("AA", "view = " + text);
                    }

                    if (view == removeBtn) {
                        Log.w("AA", "remove right = " + view.getRight());


                        Animator animator = null;
                        if (transitionType == LayoutTransition.APPEARING) {
//                            animator = ObjectAnimator.ofPropertyValuesHolder(view,
//                                    PropertyValuesHolder.ofFloat("translationX", 0.f),
//                                    PropertyValuesHolder.ofFloat("alpha", 1.f));

                            AnimatorSet set = new AnimatorSet();
                            set.playTogether(
                                    ObjectAnimator.ofFloat(view, "translationX", 0),
                                    ObjectAnimator.ofFloat(view, "alpha", view.getAlpha(), 1.f));
                            animator = set;

                        } else if (transitionType == LayoutTransition.DISAPPEARING) {
//                            animator = ObjectAnimator.ofPropertyValuesHolder(view,
//                                    PropertyValuesHolder.ofFloat("translationX", -view.getRight()),
//                                    PropertyValuesHolder.ofFloat("alpha", 0.f));

                            AnimatorSet set = new AnimatorSet();
                            set.playTogether(
                                    ObjectAnimator.ofFloat(view, "translationX", -view.getRight()),
                                    ObjectAnimator.ofFloat(view, "alpha", view.getAlpha(), 0.f));
                            animator = set;
                        }

                        transition.setAnimator(transitionType, animator);
                    }

                    if (view == dragBtn) {

                        ObjectAnimator animator = null;
                        if (transitionType == LayoutTransition.APPEARING) {
                            animator = ObjectAnimator.ofPropertyValuesHolder(view,
                                    PropertyValuesHolder.ofFloat("translationX", 0));

                        } else if (transitionType == LayoutTransition.DISAPPEARING) {
                            animator = ObjectAnimator.ofPropertyValuesHolder(view,
                                    PropertyValuesHolder.ofFloat("translationX", swipeView.getWidth() - view.getLeft()));
                        }

                        transition.setAnimator(transitionType, animator);
                    }
                }

                @Override
                public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                    if (view == removeBtn) {
                        if (transitionType == LayoutTransition.DISAPPEARING) {
                            view.setAlpha(0);
                        }
                    }
                }
            });
        }

    }
}
