package com.test.sport.ui.fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.test.nba.R;
import com.test.nba.databinding.FragmentMapBinding;
import com.test.sport.base.BaseFragment;

public class MapFragment extends BaseFragment<FragmentMapBinding> {
    @Override
    protected int initLayout() {
        return R.layout.fragment_map;
    }

    @Override
    protected FragmentMapBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentMapBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void initData() {
        super.initData();
    }
}
