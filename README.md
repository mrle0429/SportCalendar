[English](#sport-calendar-app-overview)
[中文](#运动日历)

# Sport Calendar App Overview


The **Sport Calendar** app is an innovative platform designed to enhance sports fans' experiences by seamlessly integrating sports events with personal schedules. The app offers a range of features that enable users to stay updated, plan effectively, and enjoy sports like never before.
![Screenshot_20241230_141347.png](img%2FScreenshot_20241230_141347.png)

## Feature Detailas

**Event Calendar Integration**

Supports adding the user's followed sports events to the system calendar. Automatically syncs event information, including match times and competing teams. Integrated with the system calendar, making it easier for users to manage both their sports events and work-life schedule.

A XXPermissions framework to handle calendar permission requests, access the system calendar database via ContentProvider, and perform operations on the system calendar database. In particular, it also needs to handle the case of conflicting user event times to avoid users missing matches.

**Event Reminder System**

Flexible reminder time options (on-time reminder, 10 minutes in advance, 30 minutes in advance, or no reminder).

Use ContentProvider to remind the user to watch the game using the reminder mechanism of the system calendar. App notifications use NotificationManager for in-app reminders.

**User Preferences Settings**

Users can set the default sport, preferred viewing time, favourite team/athlete, favourite league. Different sports have different characteristics and the preferences related to sports are accompanied by the type of sport. These preferences are used for match recommendations.

A SharedPreferences to store preferences and use intents to notify the global when user preferences change.

**Smart Recommendation System**

Personalized event recommendations are made based on the user's followed teams, preferred viewing times, and key matches.

The project designed a score weighting algorithm, focusing on team participation +50 points, preferred time period +20 points, popular events +20 points. At the same time add some weight perturbation mechanism, a small amount of recommended to the user has never been concerned about the tournament, to avoid falling into the information cocoon. In the future, I also consider combining my recent research interests (aspect graph conv recommendation algorithms for emotion perception) with this project.

**Time zone switching**

Users can set the time zone in the setting interface, using the Java standard library TimeZoneId to get the user's default time zone (determined by the user's system), and users can also manually select the time zone from all the time zones. Using Broadcast, any change will be notified to all components to achieve global time zone conversion.

**AI Sports Assistant**

An AI-powered sports assistant based on a large language model (Wenxin-Baidu). Predefined personalized sports event prompts, offering professional sports news and information consultations. It supports multi-domain sports knowledge Q&A.

**Share Game**

Users can share the details of any match with their friends and tell them to watch the match together. Use Bitmap to convert the layout to image format and use Intent to complete the Android native sharing function. User can share the race image to his friends via (WeiXin, Email).

**Map of Sports Venues**

Nearby sports venues will be displayed on the map to encourage users to be active in their favorite sports. The map component uses the Baidu SDK component to implement the map display, user positioning, and POI retrieval functions.


# 运动日历

一个集成了多种体育赛事信息的日历应用，支持篮球、足球、网球等多种运动的赛程查询和实时比分追踪。
## 功能特点

- 支持多种体育赛事：
 - 篮球
 - 美式足球
 - 冰球
 - 网球
 - 足球
 - 棒球
 - 橄榄球
 - 印地赛车
 实时比分更新
 赛程分享
 赛程提醒
 赛事日历视图
 AI 助手解答体育相关问题
 地图显示周边运动场馆

## 快速使用
下载APP
```
app\release\app-release.apk
```


## 配置步骤

1. 下载api_keys.properties.example文件，并将其重命名为api_keys.properties。
2. 在api_keys.properties文件中，将每个API密钥替换为实际的API密钥。
3. 保存api_keys.properties文件。


