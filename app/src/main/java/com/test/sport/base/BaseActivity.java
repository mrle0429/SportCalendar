package com.test.sport.base;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.test.sport.utils.ToastUtils;

// TODO: 基类
public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity {

    private T mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAnimationAndStatusBar();
        mBinding = onCreateViewBinding(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        initClick();
    }

    protected void initAnimationAndStatusBar() {
    }

    protected void initData() {

    }

    protected void initClick() {

    }

    protected abstract int initLayout();//避免布局文件被删掉

    protected abstract T onCreateViewBinding(@NonNull LayoutInflater layoutInflater);

    public T getBinding() {
        return mBinding;
    }

    public void showToast(String msg){
        ToastUtils.getInstance().show(this,msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}



