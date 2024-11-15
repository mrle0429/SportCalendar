package com.test.sport.http.bean;

import java.util.List;

public class Sport {

    private String generatedAt;
    private List<SummariesDTO> summaries;

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public List<SummariesDTO> getSummaries() {
        return summaries;
    }

    public void setSummaries(List<SummariesDTO> summaries) {
        this.summaries = summaries;
    }

    public static class SummariesDTO {
        private SportEventDTO sportEvent;
        private SportEventStatusDTO sportEventStatus;
        private StatisticsDTO statistics;

        public SportEventDTO getSportEvent() {
            return sportEvent;
        }

        public void setSportEvent(SportEventDTO sportEvent) {
            this.sportEvent = sportEvent;
        }

        public SportEventStatusDTO getSportEventStatus() {
            return sportEventStatus;
        }

        public void setSportEventStatus(SportEventStatusDTO sportEventStatus) {
            this.sportEventStatus = sportEventStatus;
        }

        public StatisticsDTO getStatistics() {
            return statistics;
        }

        public void setStatistics(StatisticsDTO statistics) {
            this.statistics = statistics;
        }

        public static class SportEventDTO {
            private String id;
            private String startTime;
            private Boolean startTimeConfirmed;
            private SportEventContextDTO sportEventContext;
            private CoverageDTO coverage;
            private List<CompetitorsDTO> competitors;
            private VenueDTO venue;
            private SportEventConditionsDTO sportEventConditions;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public Boolean getStartTimeConfirmed() {
                return startTimeConfirmed;
            }

            public void setStartTimeConfirmed(Boolean startTimeConfirmed) {
                this.startTimeConfirmed = startTimeConfirmed;
            }

            public SportEventContextDTO getSportEventContext() {
                return sportEventContext;
            }

            public void setSportEventContext(SportEventContextDTO sportEventContext) {
                this.sportEventContext = sportEventContext;
            }

            public CoverageDTO getCoverage() {
                return coverage;
            }

            public void setCoverage(CoverageDTO coverage) {
                this.coverage = coverage;
            }

            public List<CompetitorsDTO> getCompetitors() {
                return competitors;
            }

            public void setCompetitors(List<CompetitorsDTO> competitors) {
                this.competitors = competitors;
            }

            public VenueDTO getVenue() {
                return venue;
            }

            public void setVenue(VenueDTO venue) {
                this.venue = venue;
            }

            public SportEventConditionsDTO getSportEventConditions() {
                return sportEventConditions;
            }

            public void setSportEventConditions(SportEventConditionsDTO sportEventConditions) {
                this.sportEventConditions = sportEventConditions;
            }

            public static class SportEventContextDTO {
                private SportDTO sport;
                private CategoryDTO category;
                private CompetitionDTO competition;
                private SeasonDTO season;
                private StageDTO stage;
                private RoundDTO round;
                private List<GroupsDTO> groups;

                public SportDTO getSport() {
                    return sport;
                }

                public void setSport(SportDTO sport) {
                    this.sport = sport;
                }

                public CategoryDTO getCategory() {
                    return category;
                }

                public void setCategory(CategoryDTO category) {
                    this.category = category;
                }

                public CompetitionDTO getCompetition() {
                    return competition;
                }

                public void setCompetition(CompetitionDTO competition) {
                    this.competition = competition;
                }

                public SeasonDTO getSeason() {
                    return season;
                }

                public void setSeason(SeasonDTO season) {
                    this.season = season;
                }

                public StageDTO getStage() {
                    return stage;
                }

                public void setStage(StageDTO stage) {
                    this.stage = stage;
                }

                public RoundDTO getRound() {
                    return round;
                }

                public void setRound(RoundDTO round) {
                    this.round = round;
                }

                public List<GroupsDTO> getGroups() {
                    return groups;
                }

                public void setGroups(List<GroupsDTO> groups) {
                    this.groups = groups;
                }

                public static class SportDTO {
                    private String id;
                    private String name;

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }
                }

                public static class CategoryDTO {
                    private String id;
                    private String name;
                    private String countryCode;

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getCountryCode() {
                        return countryCode;
                    }

                    public void setCountryCode(String countryCode) {
                        this.countryCode = countryCode;
                    }
                }

                public static class CompetitionDTO {
                    private String id;
                    private String name;
                    private String gender;

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getGender() {
                        return gender;
                    }

                    public void setGender(String gender) {
                        this.gender = gender;
                    }
                }

                public static class SeasonDTO {
                    private String id;
                    private String name;
                    private String startDate;
                    private String endDate;
                    private String year;
                    private String competitionId;

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getStartDate() {
                        return startDate;
                    }

                    public void setStartDate(String startDate) {
                        this.startDate = startDate;
                    }

                    public String getEndDate() {
                        return endDate;
                    }

                    public void setEndDate(String endDate) {
                        this.endDate = endDate;
                    }

                    public String getYear() {
                        return year;
                    }

                    public void setYear(String year) {
                        this.year = year;
                    }

                    public String getCompetitionId() {
                        return competitionId;
                    }

                    public void setCompetitionId(String competitionId) {
                        this.competitionId = competitionId;
                    }
                }

                public static class StageDTO {
                    private Integer order;
                    private String type;
                    private String phase;
                    private String startDate;
                    private String endDate;
                    private String year;

                    public Integer getOrder() {
                        return order;
                    }

                    public void setOrder(Integer order) {
                        this.order = order;
                    }

                    public String getType() {
                        return type;
                    }

                    public void setType(String type) {
                        this.type = type;
                    }

                    public String getPhase() {
                        return phase;
                    }

                    public void setPhase(String phase) {
                        this.phase = phase;
                    }

                    public String getStartDate() {
                        return startDate;
                    }

                    public void setStartDate(String startDate) {
                        this.startDate = startDate;
                    }

                    public String getEndDate() {
                        return endDate;
                    }

                    public void setEndDate(String endDate) {
                        this.endDate = endDate;
                    }

                    public String getYear() {
                        return year;
                    }

                    public void setYear(String year) {
                        this.year = year;
                    }
                }

                public static class RoundDTO {
                    private Integer number;

                    public Integer getNumber() {
                        return number;
                    }

                    public void setNumber(Integer number) {
                        this.number = number;
                    }
                }

                public static class GroupsDTO {
                    private String id;
                    private String name;

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }
                }
            }

            public static class CoverageDTO {
                private List<PropertiesDTO> properties;
                private Boolean live;

                public List<PropertiesDTO> getProperties() {
                    return properties;
                }

                public void setProperties(List<PropertiesDTO> properties) {
                    this.properties = properties;
                }

                public Boolean getLive() {
                    return live;
                }

                public void setLive(Boolean live) {
                    this.live = live;
                }

                public static class PropertiesDTO {
                    private String type;
                    private Boolean value;

                    public String getType() {
                        return type;
                    }

                    public void setType(String type) {
                        this.type = type;
                    }

                    public Boolean getValue() {
                        return value;
                    }

                    public void setValue(Boolean value) {
                        this.value = value;
                    }
                }
            }

            public static class VenueDTO {
                private String id;
                private String name;
                private Integer capacity;
                private String cityName;
                private String countryName;
                private String mapCoordinates;
                private String countryCode;
                private String timezone;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public Integer getCapacity() {
                    return capacity;
                }

                public void setCapacity(Integer capacity) {
                    this.capacity = capacity;
                }

                public String getCityName() {
                    return cityName;
                }

                public void setCityName(String cityName) {
                    this.cityName = cityName;
                }

                public String getCountryName() {
                    return countryName;
                }

                public void setCountryName(String countryName) {
                    this.countryName = countryName;
                }

                public String getMapCoordinates() {
                    return mapCoordinates;
                }

                public void setMapCoordinates(String mapCoordinates) {
                    this.mapCoordinates = mapCoordinates;
                }

                public String getCountryCode() {
                    return countryCode;
                }

                public void setCountryCode(String countryCode) {
                    this.countryCode = countryCode;
                }

                public String getTimezone() {
                    return timezone;
                }

                public void setTimezone(String timezone) {
                    this.timezone = timezone;
                }
            }

            public static class SportEventConditionsDTO {
                private GroundDTO ground;

                public GroundDTO getGround() {
                    return ground;
                }

                public void setGround(GroundDTO ground) {
                    this.ground = ground;
                }

                public static class GroundDTO {
                    private Boolean neutral;

                    public Boolean getNeutral() {
                        return neutral;
                    }

                    public void setNeutral(Boolean neutral) {
                        this.neutral = neutral;
                    }
                }
            }

            public static class CompetitorsDTO {
                private String id;
                private String name;
                private String country;
                private String countryCode;
                private String abbreviation;
                private String qualifier;
                private String gender;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getCountry() {
                    return country;
                }

                public void setCountry(String country) {
                    this.country = country;
                }

                public String getCountryCode() {
                    return countryCode;
                }

                public void setCountryCode(String countryCode) {
                    this.countryCode = countryCode;
                }

                public String getAbbreviation() {
                    return abbreviation;
                }

                public void setAbbreviation(String abbreviation) {
                    this.abbreviation = abbreviation;
                }

                public String getQualifier() {
                    return qualifier;
                }

                public void setQualifier(String qualifier) {
                    this.qualifier = qualifier;
                }

                public String getGender() {
                    return gender;
                }

                public void setGender(String gender) {
                    this.gender = gender;
                }
            }
        }

        public static class SportEventStatusDTO {
            private String status;
            private String matchStatus;
            private Integer homeScore;
            private Integer awayScore;
            private List<PeriodScoresDTO> periodScores;
            private ClockDTO clock;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getMatchStatus() {
                return matchStatus;
            }

            public void setMatchStatus(String matchStatus) {
                this.matchStatus = matchStatus;
            }

            public Integer getHomeScore() {
                return homeScore;
            }

            public void setHomeScore(Integer homeScore) {
                this.homeScore = homeScore;
            }

            public Integer getAwayScore() {
                return awayScore;
            }

            public void setAwayScore(Integer awayScore) {
                this.awayScore = awayScore;
            }

            public List<PeriodScoresDTO> getPeriodScores() {
                return periodScores;
            }

            public void setPeriodScores(List<PeriodScoresDTO> periodScores) {
                this.periodScores = periodScores;
            }

            public ClockDTO getClock() {
                return clock;
            }

            public void setClock(ClockDTO clock) {
                this.clock = clock;
            }

            public static class ClockDTO {
                private String played;
                private String remaining;

                public String getPlayed() {
                    return played;
                }

                public void setPlayed(String played) {
                    this.played = played;
                }

                public String getRemaining() {
                    return remaining;
                }

                public void setRemaining(String remaining) {
                    this.remaining = remaining;
                }
            }

            public static class PeriodScoresDTO {
                private Integer homeScore;
                private Integer awayScore;
                private String type;
                private Integer number;

                public Integer getHomeScore() {
                    return homeScore;
                }

                public void setHomeScore(Integer homeScore) {
                    this.homeScore = homeScore;
                }

                public Integer getAwayScore() {
                    return awayScore;
                }

                public void setAwayScore(Integer awayScore) {
                    this.awayScore = awayScore;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public Integer getNumber() {
                    return number;
                }

                public void setNumber(Integer number) {
                    this.number = number;
                }
            }
        }

        public static class StatisticsDTO {
            private TotalsDTO totals;

            public TotalsDTO getTotals() {
                return totals;
            }

            public void setTotals(TotalsDTO totals) {
                this.totals = totals;
            }

            public static class TotalsDTO {
                private List<CompetitorsDTO> competitors;

                public List<CompetitorsDTO> getCompetitors() {
                    return competitors;
                }

                public void setCompetitors(List<CompetitorsDTO> competitors) {
                    this.competitors = competitors;
                }

                public static class CompetitorsDTO {
                    private String id;
                    private String name;
                    private String abbreviation;
                    private String qualifier;
                    private StatisticsDTO2 statistics;

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getAbbreviation() {
                        return abbreviation;
                    }

                    public void setAbbreviation(String abbreviation) {
                        this.abbreviation = abbreviation;
                    }

                    public String getQualifier() {
                        return qualifier;
                    }

                    public void setQualifier(String qualifier) {
                        this.qualifier = qualifier;
                    }

                    public StatisticsDTO2 getStatistics() {
                        return statistics;
                    }

                    public void setStatistics(StatisticsDTO2 statistics) {
                        this.statistics = statistics;
                    }

                    public static class StatisticsDTO2 {
                        private Integer ballPossession;
                        private Integer biggestLead;
                        private Integer fouls;
                        private Integer freeThrowAttemptsSuccessful;
                        private Integer freeThrowAttemptsTotal;
                        private Integer leaderAssists;
                        private String leaderAssistsPlayer;
                        private Integer leaderPoints;
                        private String leaderPointsPlayer;
                        private Integer leaderRebounds;
                        private String leaderReboundsPlayer;
                        private Integer rebounds;
                        private Integer shotsBlocked;
                        private Integer teamLeads;
                        private Integer threePointAttemptsSuccessful;
                        private Integer threePointAttemptsTotal;
                        private Integer timeSpentInLead;
                        private Integer timeouts;
                        private Integer twoPointAttemptsSuccessful;
                        private Integer twoPointAttemptsTotal;

                        public Integer getBallPossession() {
                            return ballPossession;
                        }

                        public void setBallPossession(Integer ballPossession) {
                            this.ballPossession = ballPossession;
                        }

                        public Integer getBiggestLead() {
                            return biggestLead;
                        }

                        public void setBiggestLead(Integer biggestLead) {
                            this.biggestLead = biggestLead;
                        }

                        public Integer getFouls() {
                            return fouls;
                        }

                        public void setFouls(Integer fouls) {
                            this.fouls = fouls;
                        }

                        public Integer getFreeThrowAttemptsSuccessful() {
                            return freeThrowAttemptsSuccessful;
                        }

                        public void setFreeThrowAttemptsSuccessful(Integer freeThrowAttemptsSuccessful) {
                            this.freeThrowAttemptsSuccessful = freeThrowAttemptsSuccessful;
                        }

                        public Integer getFreeThrowAttemptsTotal() {
                            return freeThrowAttemptsTotal;
                        }

                        public void setFreeThrowAttemptsTotal(Integer freeThrowAttemptsTotal) {
                            this.freeThrowAttemptsTotal = freeThrowAttemptsTotal;
                        }

                        public Integer getLeaderAssists() {
                            return leaderAssists;
                        }

                        public void setLeaderAssists(Integer leaderAssists) {
                            this.leaderAssists = leaderAssists;
                        }

                        public String getLeaderAssistsPlayer() {
                            return leaderAssistsPlayer;
                        }

                        public void setLeaderAssistsPlayer(String leaderAssistsPlayer) {
                            this.leaderAssistsPlayer = leaderAssistsPlayer;
                        }

                        public Integer getLeaderPoints() {
                            return leaderPoints;
                        }

                        public void setLeaderPoints(Integer leaderPoints) {
                            this.leaderPoints = leaderPoints;
                        }

                        public String getLeaderPointsPlayer() {
                            return leaderPointsPlayer;
                        }

                        public void setLeaderPointsPlayer(String leaderPointsPlayer) {
                            this.leaderPointsPlayer = leaderPointsPlayer;
                        }

                        public Integer getLeaderRebounds() {
                            return leaderRebounds;
                        }

                        public void setLeaderRebounds(Integer leaderRebounds) {
                            this.leaderRebounds = leaderRebounds;
                        }

                        public String getLeaderReboundsPlayer() {
                            return leaderReboundsPlayer;
                        }

                        public void setLeaderReboundsPlayer(String leaderReboundsPlayer) {
                            this.leaderReboundsPlayer = leaderReboundsPlayer;
                        }

                        public Integer getRebounds() {
                            return rebounds;
                        }

                        public void setRebounds(Integer rebounds) {
                            this.rebounds = rebounds;
                        }

                        public Integer getShotsBlocked() {
                            return shotsBlocked;
                        }

                        public void setShotsBlocked(Integer shotsBlocked) {
                            this.shotsBlocked = shotsBlocked;
                        }

                        public Integer getTeamLeads() {
                            return teamLeads;
                        }

                        public void setTeamLeads(Integer teamLeads) {
                            this.teamLeads = teamLeads;
                        }

                        public Integer getThreePointAttemptsSuccessful() {
                            return threePointAttemptsSuccessful;
                        }

                        public void setThreePointAttemptsSuccessful(Integer threePointAttemptsSuccessful) {
                            this.threePointAttemptsSuccessful = threePointAttemptsSuccessful;
                        }

                        public Integer getThreePointAttemptsTotal() {
                            return threePointAttemptsTotal;
                        }

                        public void setThreePointAttemptsTotal(Integer threePointAttemptsTotal) {
                            this.threePointAttemptsTotal = threePointAttemptsTotal;
                        }

                        public Integer getTimeSpentInLead() {
                            return timeSpentInLead;
                        }

                        public void setTimeSpentInLead(Integer timeSpentInLead) {
                            this.timeSpentInLead = timeSpentInLead;
                        }

                        public Integer getTimeouts() {
                            return timeouts;
                        }

                        public void setTimeouts(Integer timeouts) {
                            this.timeouts = timeouts;
                        }

                        public Integer getTwoPointAttemptsSuccessful() {
                            return twoPointAttemptsSuccessful;
                        }

                        public void setTwoPointAttemptsSuccessful(Integer twoPointAttemptsSuccessful) {
                            this.twoPointAttemptsSuccessful = twoPointAttemptsSuccessful;
                        }

                        public Integer getTwoPointAttemptsTotal() {
                            return twoPointAttemptsTotal;
                        }

                        public void setTwoPointAttemptsTotal(Integer twoPointAttemptsTotal) {
                            this.twoPointAttemptsTotal = twoPointAttemptsTotal;
                        }
                    }
                }
            }
        }
    }
}
