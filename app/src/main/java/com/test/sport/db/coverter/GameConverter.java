package com.test.sport.db.coverter;

import com.alibaba.fastjson.JSON;
import com.test.sport.model.Game;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

// TODO:转换
// List<Game> 对象 ←→ String(JSON格式) 互相转换
public class GameConverter implements PropertyConverter<List<Game>, String> {

    // 从数据库中的字符串转换为 List<Game> 对象
    @Override
    public List<Game> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        return JSON.parseArray(databaseValue, Game.class);
    }

    // 将 List<Game> 对象转换为可以存储在数据库中的字符串
    @Override
    public String convertToDatabaseValue(List<Game> arrays) {
        if (arrays == null) {
            return null;
        } else {
            return JSON.toJSONString(arrays);
        }
    }
}