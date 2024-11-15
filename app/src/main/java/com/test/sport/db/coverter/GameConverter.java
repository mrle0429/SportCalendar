package com.test.sport.db.coverter;

import com.alibaba.fastjson.JSON;
import com.test.sport.db.entity.Game;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

// TODO:转换
public class GameConverter implements PropertyConverter<List<Game>, String> {

    @Override
    public List<Game> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        return JSON.parseArray(databaseValue, Game.class);
    }

    @Override
    public String convertToDatabaseValue(List<Game> arrays) {
        if (arrays == null) {
            return null;
        } else {
            return JSON.toJSONString(arrays);
        }
    }
}