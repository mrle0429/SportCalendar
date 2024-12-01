package com.test.sport.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.test.sport.utils.ToastUtils;

// TODO: 基类
public abstract class BaseFragment<T extends ViewBinding> extends Fragment {

    private T mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = onCreateViewBinding(inflater, container);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initClick();
    }

    protected void initData() {

    }

    protected void initClick() {

    }

    protected abstract int initLayout();//避免布局文件被删掉

    protected abstract T onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent);

    public T getBinding() {
        return mBinding;
    }

    public void showToast(String msg) {
        ToastUtils.getInstance().show(getActivity(), msg);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

}



