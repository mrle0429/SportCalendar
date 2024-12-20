# Sport Calendar App Overview

The **Sport Calendar** app is an innovative platform designed to enhance sports fans' experiences by seamlessly integrating sports events with personal schedules. The app offers a range of features that enable users to stay updated, plan effectively, and enjoy sports like never before.
<img src="./Screenshot_20241219_230453.png" alt="Screenshot_20241219_230453" style="zoom:10%;" />

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