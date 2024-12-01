package com.test.sport.db.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

// TODO:比赛
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sport_name;//Basketball
    private String start_time;//2024-11-01T00:00:00+00:00
    private String country_name;//USA
    private String competition_name;//NBA
    private String stage_start_date;//2024-10-22
    private String stage_end_date;//2024-10-22
    private String stage_type;//league 联盟
    private String stage_phase;//regular season 常规赛
    private List<Competitors> competitors;//竞争对手
    private String venue_name;//场馆名称
    private int venue_capacity;//场馆容量
    private String city_name;//城市
    private List<Score> Score;
    ;//分场得分
    private String status;//not_started live ended closed postponed
    private int away_score;//客场得分
    private int home_score;//主场得分
    private int recommendScore;

    public static class Score implements Serializable {
        private int away_score;//客场得分
        private int home_score;//主场得分
        private int number;//1

        public int getAway_score() {
            return away_score;
        }

        public void setAway_score(int away_score) {
            this.away_score = away_score;
        }

        public int getHome_score() {
            return home_score;
        }

        public void setHome_score(int home_score) {
            this.home_score = home_score;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }

    public static class Competitors implements Serializable {
        private String competitors_name;
        private String abbreviation;//缩写
        private String country_code;
        private String qualifier;//主客场

        public String getCompetitors_name() {
            return competitors_name;
        }

        public void setCompetitors_name(String competitors_name) {
            this.competitors_name = competitors_name;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public void setAbbreviation(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        public String getCountry_code() {
            return country_code;
        }

        public void setCountry_code(String country_code) {
            this.country_code = country_code;
        }

        public String getQualifier() {
            return qualifier;
        }

        public void setQualifier(String qualifier) {
            this.qualifier = qualifier;
        }
    }

    public int getVenue_capacity() {
        return venue_capacity;
    }

    public void setVenue_capacity(int venue_capacity) {
        this.venue_capacity = venue_capacity;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getVenue_name() {
        return venue_name;
    }

    public void setVenue_name(String venue_name) {
        this.venue_name = venue_name;
    }

    public List<Game.Score> getScore() {
        return Score;
    }

    public void setScore(List<Game.Score> score) {
        Score = score;
    }

    public String getSport_name() {
        return sport_name;
    }

    public void setSport_name(String sport_name) {
        this.sport_name = sport_name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCompetition_name() {
        return competition_name;
    }

    public void setCompetition_name(String competition_name) {
        this.competition_name = competition_name;
    }

    public String getStage_start_date() {
        return stage_start_date;
    }

    public void setStage_start_date(String stage_start_date) {
        this.stage_start_date = stage_start_date;
    }

    public String getStage_end_date() {
        return stage_end_date;
    }

    public void setStage_end_date(String stage_end_date) {
        this.stage_end_date = stage_end_date;
    }

    public String getStage_phase() {
        return stage_phase;
    }

    public void setStage_phase(String stage_phase) {
        this.stage_phase = stage_phase;
    }

    public List<Competitors> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(List<Competitors> competitors) {
        this.competitors = competitors;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAway_score() {
        return away_score;
    }

    public void setAway_score(int away_score) {
        this.away_score = away_score;
    }

    public int getHome_score() {
        return home_score;
    }

    public void setHome_score(int home_score) {
        this.home_score = home_score;
    }

    public String getStage_type() {
        return stage_type;
    }

    public void setStage_type(String stage_type) {
        this.stage_type = stage_type;
    }

    public int getRecommendScore() {
        return recommendScore;
    }

    public void setRecommendScore(int score) {
        recommendScore = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;
        Game game = (Game) o;
        return getVenue_capacity() == game.getVenue_capacity() && getAway_score() == game.getAway_score() && getHome_score() == game.getHome_score() && Objects.equals(getSport_name(), game.getSport_name()) && Objects.equals(getStart_time(), game.getStart_time()) && Objects.equals(getCountry_name(), game.getCountry_name()) && Objects.equals(getCompetition_name(), game.getCompetition_name()) && Objects.equals(getStage_start_date(), game.getStage_start_date()) && Objects.equals(getStage_end_date(), game.getStage_end_date()) && Objects.equals(getStage_type(), game.getStage_type()) && Objects.equals(getStage_phase(), game.getStage_phase()) && Objects.equals(getCompetitors(), game.getCompetitors()) && Objects.equals(getVenue_name(), game.getVenue_name()) && Objects.equals(getCity_name(), game.getCity_name()) && Objects.equals(getScore(), game.getScore()) && Objects.equals(getStatus(), game.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSport_name(), getStart_time(), getCountry_name(), getCompetition_name(), getStage_start_date(), getStage_end_date(), getStage_type(), getStage_phase(), getCompetitors(), getVenue_name(), getVenue_capacity(), getCity_name(), getScore(), getStatus(), getAway_score(), getHome_score());
    }
}
