package com.test.sport.ui.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.lxj.xpopup.XPopup;
import com.test.nba.R;
import com.test.nba.databinding.FragmentScheduleBinding;
import com.test.sport.base.BaseFragment;
import com.test.sport.db.DbScheduleController;
import com.test.sport.db.entity.Schedule;
import com.test.sport.event.ScheduleEvent;
import com.test.sport.ui.activity.SportActivity;
import com.test.sport.ui.adapter.ScheduleAdapter;
import com.test.sport.utils.CalendarReminderUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO:日程
public class ScheduleFragment extends BaseFragment<FragmentScheduleBinding> implements View.OnClickListener,
        CalendarView.OnCalendarSelectListener {

    private DbScheduleController dbScheduleController;
    private List<Schedule> scheduleList = new ArrayList<>();//今日
    private ScheduleAdapter scheduleAdapter;
    private List<Schedule> allScheduleList = new ArrayList<>();//全部
    private int year, month, day;
    private String date;

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);
        dbScheduleController = DbScheduleController.getInstance(getActivity());
        initCalendarTitle();
    }

    @Override
    protected void initClick() {
        super.initClick();
        getBinding().ivLeft.setOnClickListener(this);
        getBinding().ivRight.setOnClickListener(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected FragmentScheduleBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentScheduleBinding.inflate(inflater);
    }

    private void initCalendarTitle() {
        getBinding().calendarView.setOnCalendarSelectListener(this);

        year = getBinding().calendarView.getCurYear();
        month = getBinding().calendarView.getCurMonth();
        day = getBinding().calendarView.getCurDay();
        getBinding().tvCalendarTitle.setText(year + "-" + month);
        date = year + "-" + month + "-" + day;
        inAdapter();
        initSchedule();
    }

    private void inAdapter() {
        scheduleList.clear();
        scheduleList = dbScheduleController.searchByWhere(date);
        if (scheduleList.size() == 0) {
            getBinding().tvHint.setVisibility(View.VISIBLE);
        } else {
            getBinding().tvHint.setVisibility(View.GONE);
        }
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        scheduleAdapter = new ScheduleAdapter(getActivity(), scheduleList);
        getBinding().rvData.setLayoutManager(manager);
        getBinding().rvData.setAdapter(scheduleAdapter);
        scheduleAdapter.setListener(new ScheduleAdapter.OnClickListener() {
            @Override
            public void onDelete(int pos, View view) {
                showDialog(pos, view);
            }

            @Override
            public void onClick(int pos) {
                Intent intent = new Intent(getActivity(), SportActivity.class);
                intent.putExtra("game", scheduleList.get(pos).getGameList().get(0));
                startActivity(intent);
            }
        });
    }

    private void refreshAdapter() {
        scheduleAdapter.setList(scheduleList);
        scheduleAdapter.notifyDataSetChanged();
        if (scheduleList.size() == 0) {
            getBinding().tvHint.setVisibility(View.VISIBLE);
        } else {
            getBinding().tvHint.setVisibility(View.GONE);
        }
    }

    private void initSchedule() {
        allScheduleList.clear();
        allScheduleList = dbScheduleController.searchAll();
        Map<String, Calendar> map = new HashMap<>();
        for (Schedule schedule : allScheduleList) {
            String[] date = schedule.getDate().split("-");
            map.put(getSchemeCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]),
                            ContextCompat.getColor(getActivity(), R.color.flag_working)).toString(),
                    getSchemeCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]),
                            ContextCompat.getColor(getActivity(), R.color.flag_working)));
        }
        getBinding().calendarView.setSchemeDate(map);
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        return calendar;
    }

    // TODO: 删除
    private void showDialog(int pos, View view) {
        new XPopup.Builder(getActivity())
                .hasShadowBg(false)// 是否有半透明的背景，默认为true
                .isViewMode(true)
                .atView(view)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"Delete"},
                        new int[]{},
                        (position, text) -> {
                            dbScheduleController.delete(scheduleList.get(pos));
                            CalendarReminderUtils.deleteCalendarEvent(getActivity(), scheduleList.get(pos).getTitle());
                            scheduleList.remove(scheduleList.get(pos));
                            refreshAdapter();
                            initSchedule();
                            showToast("Delete Success");
                        })
                .show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                getBinding().calendarView.scrollToPre(true);
                break;
            case R.id.iv_right:
                getBinding().calendarView.scrollToNext(true);
                break;
        }
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        year = calendar.getYear();
        month = calendar.getMonth();
        day = calendar.getDay();
        getBinding().tvCalendarTitle.setText(year + "-" + month);
        date = year + "-" + month + "-" + day;
        scheduleList.clear();
        scheduleList = dbScheduleController.searchByWhere(date);
        refreshAdapter();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(ScheduleEvent event) {
        scheduleList.clear();
        scheduleList = dbScheduleController.searchByWhere(date);
        refreshAdapter();
        initSchedule();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
