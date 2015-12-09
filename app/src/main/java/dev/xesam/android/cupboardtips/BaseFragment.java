package dev.xesam.android.cupboardtips;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by xesamguo@gmail.com on 11/20/15.
 */
public class BaseFragment extends Fragment {

    protected CupboardSQLiteOpenHelper cupboardSQLiteOpenHelper;

    protected int getLayoutId() {
        return -1;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cupboardSQLiteOpenHelper = new CupboardSQLiteOpenHelper(getActivity());
    }
}
