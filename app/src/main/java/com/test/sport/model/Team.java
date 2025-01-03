package com.test.sport.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Objects;

public class Team {
    @JSONField(name = "id")
    private String id;          // 球队ID

    @JSONField(name = "sportType")
    private String sportType;   // 运动类型

    @JSONField(name = "name")
    private String name;        // 球队名称

    @JSONField(name = "alias")
    private String alias;       // 球队简称

    private transient boolean isFavorite;  // transient 表示不会被序列化

    // getter 和 setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}