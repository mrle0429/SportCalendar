package com.test.sport.base;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.test.sport.utils.ToastUtils;


public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity {

    private T mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAnimationAndStatusBar();       // 初始化动画和状态栏
        mBinding = onCreateViewBinding(getLayoutInflater());   // 初始化ViewBinding
        setContentView(mBinding.getRoot());            // 设置根视图
        initData();                                  // 初始化数据
        initClick();                                 // 初始化点击事件
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

    public void showToast(String msg) {
        ToastUtils.getInstance().show(this, msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}



