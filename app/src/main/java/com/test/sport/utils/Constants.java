package com.test.sport.utils;

// TODO:常量 
public class Constants {

    // TODO:全球篮球API-每日摘要
    public static final String BASKET_BALL_URL = "https://api.sportradar.com/basketball/trial/v2/en/schedules/";
    public static final String BASKET_BALL_KEY = "XcLiGwSgTFJTA5XV5uqxzvmHzQ9qhRcHY11R9gbG";

    // TODO:全球美式足球 API-每日摘要
    public static final String FOOT_BALL_URL = "https://api.sportradar.com/americanfootball/trial/v2/en/schedules/";
    public static final String FOOT_BALL_KEY = "fgh3wnep3CbSG6gp9SCxQlVHTseft9swBPPbrl0O";

    // TODO:全球冰球 API冰球-每日摘要
    public static final String ICE_HOCKEY_URL = "https://api.sportradar.com/icehockey/trial/v2/en/schedules/";
    public static final String ICE_HOCKEY_KEY = "XcLiGwSgTFJTA5XV5uqxzvmHzQ9qhRcHY11R9gbG";

    // TODO:网球 API网球-每日摘要
    public static final String TENNIS_URL = "https://api.sportradar.com/tennis/trial/v3/en/schedules/";
    public static final String TENNIS_KEY = "XcLiGwSgTFJTA5XV5uqxzvmHzQ9qhRcHY11R9gbG";

    public static final String SUFFIX = "/summaries.json";

    // 足球
    public static final String SOCCER_URL = "https://api.sportradar.com/soccer/trial/v4/en/schedules/";
    public static final String SOCCER_KEY = "XcLiGwSgTFJTA5XV5uqxzvmHzQ9qhRcHY11R9gbG";
    // TODO:棒球
    public static final String BASE_BALL_URL = "https://api.sportradar.us/baseball/trial/v2/en/schedules/live/summaries.json";
    public static final String BASE_BALL_KEY = "b6qtVtA9ZIoTeXhVQp2FnENvG7pAbEbamCBkQ4bc";

    // TODO:橄榄球
    public static final String RUGBY_URL = "https://api.sportradar.com/rugby-league/trial/v3/en/schedules/live/summaries.json";
    public static final String RUGBY_KEY = "b6qtVtA9ZIoTeXhVQp2FnENvG7pAbEbamCBkQ4bc";

    // TODO:印地赛车
    public static final String INDY_CAR_URL = " https://api.sportradar.com/rugby-league/trial/v3/en/schedules/live/summaries.json";
    public static final String INDY_CAR_KEY = "b6qtVtA9ZIoTeXhVQp2FnENvG7pAbEbamCBkQ4bc";

    // 千帆大模型
    public static final String ACCESS_KEY = "ALTAKPscAps3SmNZHj7cSUJ5Qd";
    public static final String SECRET_KEY = "d6bf44c6a7b54fb6a3ea4c2b982c9940";
    public static final String SYSTEM_PROMPT =
            "You are a professional sports assistant, helping users understand game information, " +
                    "rules explanations, and match analysis. Please respond in English with a concise and " +
                    "friendly tone. Key response guidelines:\n" +
                    "1. Provide accurate and clear explanations of game rules\n" +
                    "2. Maintain objectivity in match analysis\n" +
                    "3. Support opinions with historical data when relevant\n" +
                    "4. Focus on game analysis and avoid sensitive topics like gambling\n" +
                    "5. Clearly indicate when information is uncertain\n" +
                    "6. Keep responses focused on sports-related topics\n" +
                    "7. Use appropriate sports terminology\n" +
                    "8. Provide context when discussing specific matches or players";

}
