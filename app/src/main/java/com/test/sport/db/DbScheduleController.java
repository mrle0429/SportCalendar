package com.test.sport.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.test.sport.db.entity.Schedule;
import com.test.sport.db.gen.DaoMaster;
import com.test.sport.db.gen.DaoSession;
import com.test.sport.db.gen.ScheduleDao;


import java.util.List;

// TODO:日程管理类 
public class DbScheduleController {
    /**
     * Helper
     */
    private DaoMaster.DevOpenHelper mHelper;//获取Helper对象
    /**
     * 数据库
     */
    private SQLiteDatabase db;
    /**
     * DaoMaster
     */
    private DaoMaster mDaoMaster;
    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     * 上下文
     */
    private Context context;
    /**
     * dao
     */
    private ScheduleDao scheduleDao;

    private static DbScheduleController mDbController;

    /**
     * 获取单例
     */
    public static DbScheduleController getInstance(Context context) {
        if (mDbController == null) {
            synchronized (DbScheduleController.class) {
                if (mDbController == null) {
                    mDbController = new DbScheduleController(context);
                }
            }
        }
        return mDbController;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public DbScheduleController(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context, "schedule.db", null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
        scheduleDao = mDaoSession.getScheduleDao();
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (mHelper == null) {
            mHelper = new DaoMaster.DevOpenHelper(context, "schedule.db", null);
        }
        SQLiteDatabase db = mHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     *
     * @return
     */
    private SQLiteDatabase getWritableDatabase() {
        if (mHelper == null) {
            mHelper = new DaoMaster.DevOpenHelper(context, "schedule.db.db", null);
        }
        SQLiteDatabase db = mHelper.getWritableDatabase();
        return db;
    }

    /**
     * 会自动判定是插入还是替换
     *
     * @param video
     */
    public void insertOrReplace(Schedule video) {
        scheduleDao.insertOrReplace(video);
    }

    /**
     * 插入一条记录，表里面要没有与之相同的记录
     *
     * @param video
     */
    public long insert(Schedule video) {
        return scheduleDao.insert(video);
    }

    /**
     * 更新数据
     *
     * @param video
     */
    public void update(Schedule video) {
      /*  Schedule mOldPersonInfor = scheduleDao.queryBuilder().where(ScheduleDao.Properties.Id.eq(video.getId())).build().unique();//拿到之前的记录
        if(mOldPersonInfor !=null){*/
        scheduleDao.update(video);
        // }
    }


    /**
     * 按条件查询数
     */
    public List<Schedule> searchByWhere(String date) {
        List<Schedule> schedules = scheduleDao.queryBuilder().
                where(ScheduleDao.Properties.Date.eq(date)).list();
        return schedules;
    }

    /**
     * 查询所有数据
     */
    public List<Schedule> searchAll() {
        List<Schedule> videos = scheduleDao.queryBuilder().list();
        return videos;
    }

    /**
     * @param title
     * @return
     */
    public boolean isExist(String title) {
        Schedule video = scheduleDao.queryBuilder().where(ScheduleDao.Properties.Title.eq(title)).build().unique();
        return video == null ? false : true;
    }

    /**
     * 删除数据
     */
    public void delete(String title) {
        scheduleDao.queryBuilder().where(ScheduleDao.Properties.Title.eq(title)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 删除数据
     */
    public void delete(Schedule schedule) {
        Schedule schedule1 = scheduleDao.queryBuilder().where(ScheduleDao.Properties.Id.eq(schedule.getId())).build().unique();//拿到之前的记录
        if (schedule1 != null) {
            scheduleDao.delete(schedule1);
        }
    }

    public void deleteAllData() {
        scheduleDao.queryBuilder().buildDelete().executeDeleteWithoutDetachingEntities();
    }
}