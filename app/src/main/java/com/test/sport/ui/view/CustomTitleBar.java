package com.test.sport.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.test.nba.R;

// TODO: 标题栏
public class CustomTitleBar extends RelativeLayout {

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvFinish;
    private ImageView ivSave;
    private RelativeLayout rlTitle;

    public CustomTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    //初始化视图
    private void initView(final Context context, AttributeSet attributeSet) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_title_bar, this);
        ivBack = inflate.findViewById(R.id.iv_back);
        tvTitle = inflate.findViewById(R.id.tv_title);
        tvFinish = inflate.findViewById(R.id.tv_finish);
        ivSave = inflate.findViewById(R.id.iv_save);
        rlTitle = inflate.findViewById(R.id.rl_title);
        init(context, attributeSet);
    }

    //初始化资源文件
    public void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomTitleBar);
        String title = typedArray.getString(R.styleable.CustomTitleBar_title);//标题
        int leftIcon = typedArray.getResourceId(R.styleable.CustomTitleBar_left_icon, R.drawable.bt_back_white);//左边图片
        int rightIcon = typedArray.getResourceId(R.styleable.CustomTitleBar_right_icon, R.drawable.bt_back_white);//右边图片
        String rightText = typedArray.getString(R.styleable.CustomTitleBar_right_text);//右边文字
        int titleBarType = typedArray.getInt(R.styleable.CustomTitleBar_titleBar_type, 1);//标题栏类型,默认为1

        //赋值进去我们的标题栏
        tvTitle.setText(title);
        ivBack.setImageResource(leftIcon);
        tvFinish.setText(rightText);
        ivSave.setImageResource(rightIcon);

        //可以传入type值,可自定义判断值
        if (titleBarType == 0) {//全部不显示
            ivBack.setVisibility(INVISIBLE);
            tvFinish.setVisibility(View.INVISIBLE);
            ivSave.setVisibility(View.INVISIBLE);
        } else if (titleBarType == 1) {//右侧不显示
            tvFinish.setVisibility(View.INVISIBLE);
            ivSave.setVisibility(View.INVISIBLE);
        } else if (titleBarType == 2) {//右侧显示图标,隐藏文字
            tvFinish.setVisibility(View.INVISIBLE);
            ivSave.setVisibility(View.VISIBLE);
        } else if (titleBarType == 3) {//右侧显示文字,隐藏图标
            tvFinish.setVisibility(View.VISIBLE);
            ivSave.setVisibility(View.INVISIBLE);
        } else if (titleBarType == 4) {//左侧图标不显示，右侧显示文字
            ivBack.setVisibility(INVISIBLE);
            tvFinish.setVisibility(View.VISIBLE);
            ivSave.setVisibility(View.INVISIBLE);
        } else if (titleBarType == 5) {//左侧图标不显示，右侧显示图片
            ivBack.setVisibility(INVISIBLE);
            tvFinish.setVisibility(View.GONE);
            ivSave.setVisibility(View.VISIBLE);
        }

    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setBgColor(Context context, int colorId) {
        rlTitle.setBackgroundColor(ContextCompat.getColor(context, colorId));
    }

    public void setBgImg(Context context, int imgId) {
        rlTitle.setBackgroundResource(imgId);
    }

    public void setTitleColor(Context context, int colorId) {
        tvTitle.setTextColor(ContextCompat.getColor(context, colorId));
        tvFinish.setTextColor(ContextCompat.getColor(context, colorId));
    }

    public void setRightIconVisibility(boolean show) {
        if (show) {
            ivSave.setVisibility(View.VISIBLE);
        } else {
            ivSave.setVisibility(View.GONE);
        }
    }

    public void setRightTextVisibility(boolean show) {
        if (show) {
            tvFinish.setVisibility(View.VISIBLE);
        } else {
            tvFinish.setVisibility(View.GONE);
        }
    }

    public void setRightText(String title) {
        tvFinish.setText(title);
    }

    public String getRightText() {
        return tvFinish.getText().toString();
    }

    public void setLeftIcon(int iconId) {
        ivBack.setImageResource(iconId);
    }

    public void setRightIcon(int iconId) {
        ivSave.setImageResource(iconId);
    }

    public void setLeftIconOnClickListener(OnClickListener l) {
        ivBack.setOnClickListener(l);
    }

    public void setRightIconOnClickListener(OnClickListener l) {
        ivSave.setOnClickListener(l);
    }

    public void setRightTextOnClickListener(OnClickListener l) {
        tvFinish.setOnClickListener(l);

    }
}