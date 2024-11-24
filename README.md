Sport:MVC架构
主要功能:分类体育比赛信息展示，根据比赛时间添加日程提醒功能
技术点：
Viewbinding ：它会为该模块中存在的每个 XML 布局文件生成一个绑定类,代替findViewById()。
ShapeView：允许开发者在布局中直接定义形状相关的属性，从而无需额外的 XML 配置。
GreenDAO：是一款轻量且快速的 Android ORM，可以用面向对象的思维来和sqlite数据库进行交互。
OKhttp：一个网络请求开源项目，Android网络请求轻量级框架,支持文件上传与下载，支持https。
Fastjson：一个Java 库，可用于将Java对象转换为JSON表示形式。它还可用于将 JSON 字符串转换为等效的 Java 对象。
Calendarview:Android上一个优雅、万能自定义UI、性能高效的日历控件。
Eventbus：是用于Android开发的“事件发布—订阅总线”， 用来进行模块间通信、解藕。 它可以使用很少的代码，来实现多组件之间的通信。
XXPermissions：Android权限请求框架，简化权限请求操作。

现有功能：
1. 篮球，足球，网球，
2. 时区转换（支持自动选择和手动选择）
3. 获取用户当前位置
4. 添加赛事提醒至日历中

计划功能：
1. 添加视频源
2. 赛事新闻功能
3. 用户喜欢的球队推荐