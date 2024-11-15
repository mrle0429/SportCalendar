package com.test.sport.db.entity;

import android.print.PageRange;

import com.test.sport.db.coverter.GameConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

import java.util.List;

// TODO:日程
@Entity
public class Schedule {
    private static final long serialVersionUID = -3206065828593806735L;
    @Id(autoincrement = true)//设置自增长
    @Index(unique = true)//设置唯一性
    private Long id;
    private String date;//日期
    private String time;//时间
    private String title;//标题
    private String location;//位置
    private String remindTime;//提醒时间

    @Convert(columnType = String.class,converter = GameConverter.class)
    private List<Game> gameList;

    @Generated(hash = 1013265494)
    public Schedule(Long id, String date, String time, String title,
            String location, String remindTime, List<Game> gameList) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.title = title;
        this.location = location;
        this.remindTime = remindTime;
        this.gameList = gameList;
    }
    @Generated(hash = 729319394)
    public Schedule() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getRemindTime() {
        return this.remindTime;
    }
    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getLocation() {
        return this.location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public List<Game> getGameList() {
        return this.gameList;
    }
    public void setGameList(List<Game> gameList) {
        this.gameList = gameList;
    }
}
