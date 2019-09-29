package app.haiyunshan.whatsnote.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.base.OnFragmentInteractionListener;

public abstract class BaseOptionFragment extends BasePreferenceFragment {

    public BaseOptionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_option, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            recyclerView.removeItemDecoration(dividerDecoration);
        }
    }

    @CallSuper
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected OnFragmentInteractionListener getInteraction() {
        Fragment parent = getParentFragment();
        if (parent == null || !(parent instanceof OnFragmentInteractionListener)) {
            return null;
        }

        return (OnFragmentInteractionListener)parent;
    }
}
