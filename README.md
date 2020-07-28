JackKnife SDK官方文档
--------------------------------

 <a href="http://www.apache.org/licenses/LICENSE-2.0">    <img src="http://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square" alt="License"/>  <a href="https://www.jianshu.com/u/1d0c0bc634db">
    <img src="https://img.shields.io/badge/Author-JackWHLiu-orange.svg?style=flat-square" alt="Author" />
  </a> <a href="https://shang.qq.com/wpa/qunwpa?idkey=7e59e59145e6c7c68932ace10f52790636451f01d1ecadb6a652b1df234df753">
    <img src="https://img.shields.io/badge/QQ%E7%BE%A4-249919939-orange.svg?style=flat-square" alt="QQ Group" />
  </a> </a>

##### ![Release](https://jitpack.io/v/JackWHLiu/jackknife.svg)

[jackknife-mvvm][![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)

[jackknife-widget][![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)

[jackknife-orm][![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)

[jackknife-av][![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)

##### 

##### 

![avatar](http://47.111.72.9/jackknife/banner.jpg)

#### 一、环境配置

<pre>
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}	
</pre>


```
dependencies {
	implementation 'com.github.JackWHLiu.jackknife:jackknife-orm:4.9.14'   //jackknife数据存储库
	implementation 'com.github.JackWHLiu.jackknife:jackknife-widget:4.9.14' //jackknife自定义控件库
	implementation 'com.github.JackWHLiu.jackknife:jackknife-mvvm:4.9.14'//jackknife MVVM库
	implementation 'com.github.JackWHLiu.jackknife:jackknife-av:4.9.14'// jackknife音视频库
}
```

#### 二、如何使用

##### 数据库ORM模块（jackknife-orm）

##### 1、初始化配置

> 继承com.lwh.jackknife.app.Application，并在Application中完成初始化，如果你使用2.0.15+的版本，就当我没说。因为从v2.0.15开始不再需要使用继承的方式。最后调用Orm.init(OrmConfig)完成初始化配置;//调用Orm的init方法

##### 2、完成实体类的编写

> 如果你想使用jackknife-orm自动创表，你只需要实现OrmTable接口再配置一些基本信息即可。
> 需要注意的是，在一个OrmTable的实现类中，至少要有一个配置主键或外键的属性。

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
