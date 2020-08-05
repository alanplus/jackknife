

JackKnife SDK官方文档
--------------------------------

 <a href="http://www.apache.org/licenses/LICENSE-2.0">    <img src="http://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square" alt="License"/>  <a href="https://www.jianshu.com/u/1d0c0bc634db">
    <img src="https://img.shields.io/badge/Author-JackWHLiu-orange.svg?style=flat-square" alt="Author" />
  </a> <a href="https://shang.qq.com/wpa/qunwpa?idkey=7e59e59145e6c7c68932ace10f52790636451f01d1ecadb6a652b1df234df753">
    <img src="https://img.shields.io/badge/QQ%E7%BE%A4-249919939-orange.svg?style=flat-square" alt="QQ Group" />
  </a> </a>

#### jackknife![Release](https://jitpack.io/v/JackWHLiu/jackknife.svg)

#### jackknife-mvvm[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)

#### jackknife-widget[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)

#### jackknife-av[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)



![avatar](http://47.111.72.9/jackknife/banner.jpg)

#### 一、环境配置

```groovy
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}

dependencies {
	implementation 'com.github.JackWHLiu.jackknife:jackknife-mvvm:4.10.1'//jackknife MVVM库
	implementation 'com.github.JackWHLiu.jackknife:jackknife-widget:4.10.1' //jackknife自定义控件库
	implementation 'com.github.JackWHLiu.jackknife:jackknife-av:4.10.1'// jackknife音视频库
}
```

#### 二、如何使用

##### MVVM架构（jackknife-mvvm）

##### 1、包含功能模块

- Android6.0+运行时权限申请
- 一键换肤
- WebSocket通信
- 生命周期配置
- 工具类库
  - AC：提供数组和ArrayList的转换
  - ApkUtils：与安装包相关的一些操作，可以读取当前app版本名、版本号等
  - AppProcessUtils：进程级别的操作
  - CacheUtils：清缓存工具
  - DensityUtils：像素单位转换
  - FragmentUtils：Fragment相关操作
  - GlobalContext：获取全局的ApplicationContext
  - ImageUtils：图形处理
  - IntentUtils：Intent的Extra读取
  - IoUtils：文件（夹）处理
  - LanguageUtils：语言切换
  - ManifestUtils：AndroidManifest.xml信息读取
  - Math：数学相关
  - NetworkUtils：检测网络
  - Number：定义了整数和分数
  - PinyinUtils：汉字或汉语句子转拼音
  - ReflectionUtils：反射相关
  - RegexUtils：使用正则表达式校验字符串
  - RomUtils：手机机型适配
  - SPUtils：SharedPreferences存取
  - ScreenUtils：读取屏幕宽高
  - SecurityUtils：对称加密（DES）、非对称加密(RSA)
  - ServiceUtils：系统服务获取
  - TextUtils：文字操作
  - TimeUtils：时间格式化

##### 2、MVVM使用初阶

Kotlin项目：https://github.com/JackWHLiu/jackknife_kotlin_samples （持续随版本更新的前沿案例）

Java项目：https://github.com/JackWHLiu/jackknife_java_samples （只提供基本使用，适合新手）



##### 数据库ORM模块（jackknife-orm），自4.10.0开始并入jackknife-mvvm

##### 1、初始化配置

> Orm.init(OrmConfig)完成初始化配置;//调用Orm的init方法

##### 2、完成实体类的编写

> 如果你想使用jackknife-orm自动创表，你只需要实现OrmTable接口再配置一些基本信息即可。
> 需要注意的是，在一个OrmTable的实现类中，必须有一个配置主键的属性，如果为id，一般命名为_id。

##### （1）@Table

> 配置你要映射的表名

##### （2）@Column

> 配置你要映射的列名，静态属性会被自动忽略，无需配置Ignore注解，例如Parcelable的CREATOR必须为static的，所以不需要配置Ignore

##### （3）@Ignore

> 配置你要跳过映射的列名

##### （4）@PrimaryKey

> 配置主键

##### （5）@Check

> 配置检查条件

##### （6）@Default

> 配置默认值

##### （7）@Unique

> 配置唯一约束

##### （8）@NotNull

> 配置非空约束

##### 3、创表

> 以User为例，TableManager.createTable(User.class);//创建OrmTable的实现类的表，创表一般在初始化配置时完成，因为这样可以在表结构改变时，自动更新。
> 如果在第一步中使用了OrmConfig的创表配置config.tables()，则不需要此步骤。

##### 4、事务提交

> 如果要保证事务提交，请使用Transaction#execute(Worker worker),然后使用OrmDao中带safety后缀的API

##### 5、表升级

> 从4.3.5版本开始支持数据库表字段重命名，更新OrmTable结构后，务必在初始化配置的时候提升db版本。

##### 6、常用API

> 首先要获取到操作该表的DAO对象，以User为例
> OrmDao&lt;User&gt; dao = DaoFactory.getDao(User.class);

| 名称                                                     |    所在类    | 描述                     |
| -------------------------------------------------------- | :----------: | ------------------------ |
| insert(T bean)                                           |    OrmDao    | 单条插入，插入一条数据   |
| insert(List&lt;T&gt; bean)                               |    OrmDao    | 多条插入，插入一些数据   |
| deleteAll()                                              |    OrmDao    | 删除所有数据             |
| delete(WhereBuilder builder)                             |    OrmDao    | 按条件删除数据           |
| delete(T bean)                                           |    OrmDao    | 删除特定数据             |
| update(WhereBuilder builder)                             |    OrmDao    | 按条件修改数据           |
| update(T bean)                                           |    OrmDao    | 更新特定数据             |
| selectOne()                                              |    OrmDao    | 查询第一条数据           |
| selectOne(QueryBuilder builder)                          |    OrmDao    | 查询最符合条件的一条数据 |
| select(QueryBuilder builder)                             |    OrmDao    | 按条件查询数据           |
| selectAll()                                              |    OrmDao    | 查询所有数据             |
| selectCount()                                            |    OrmDao    | 查询数据的条数           |
| selectCount(QueryBuilder builder)                        |    OrmDao    | 查询符合条件数据的条数   |
| createTable(Class&lt;? extends OrmTable&gt; tableClass)  | TableManager | 创建一张表               |
| dropTable(Class&lt;? extends OrmTable&gt; tableClass)    | TableManager | 删除一张表               |
| upgradeTable(Class&lt;? extends OrmTable&gt; tableClass) | TableManager | 升级一张表               |

##### 音视频开发（jackknife-av）

- codec：编解码
- ffmpeg：FFmpeg命令行
- live：直播推流
- player：直播拉流、视频播放
- util：音视频工具
- wallpaper：墙纸

##### 自定义View（jackknife-widget）

包

- animator：动画引擎
- bottom：底部导航栏
- calendar：日历日期选择
- floatingview：悬浮磁铁控件
- lrc：歌词滚动控件
- luckyview：幸运转盘
- popupdialog：底部弹出的菜单栏
- pull：ListView下拉刷新、上拉加载
- reader：电子书阅读
- recyclerview：RecyclerView下拉刷新、上拉加载
- refresh：布局容器刷新
- wheelview：级联滑轮选择

类

- 拖动广告动画：AnimatorDragger、AnimatorHorizontalScrollView、AnimatorLinearLayout、AnimatorRecycler、AnimatorScrollView、AnimatorViewWrapper
- 自动跳跃焦点EditText组：AutoEditText、AutoEditTextGroup、AutoScrollTextView、MacEditText、MacEditTextGroup、VerifyCodeEditText、VerifyCodeEditTextGroup
- 带删除按钮文本框：ClearEditText
- 变速环形进度条：CircularProgressBar
- 颜色选取：ColorPickerView
- 音频均衡器：EqualizerView
- 流式布局：FlowLayout、FlowRadioGroup
- 炫光文字：GradientTextView
- 标签页：HorizontalTabBar
- 字母导航：LetterView
- 正在加载...进度条：LoadingView
- 仿安卓微信底部导航条滑动颜色渐变：ShadeView
- 拖拽开关（带过程）：ToggleButton
- 语音消息录制：VoiceRecordView


star和fork https://github.com/JackWHLiu/jackknife！
