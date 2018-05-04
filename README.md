JackKnife帮助文档![Release](https://jitpack.io/v/JackWHLiu/jackknife.svg) - CSDN博客 http://blog.csdn.net/yiranaini_/
================================

一、关于如何配置环境
--------------------------------
如果要依赖jackknife的库，需要对号入座的加上以下两个配置。
#### //指定仓库的地址，在project的build.gradle加入加粗的代码。
<blockquote>
allprojects {
  repositories {
    jcenter()
    <b>maven { url "https://jitpack.io" }</b>
  }
}
</blockquote>

#### //依赖本库，在app模块的build.gradle加入加粗的代码。
<blockquote>
dependencies {
  <b>compile 'com.github.JackWHLiu:jackknife:1.0.0'</b>
}
</blockquote>

二、关于如何使用(参考https://github.com/JackWHLiu/JackKnifeDemo)
--------------------------------
### (一)基于IOC的自动注入视图、绑定控件和注册事件（jackknife-ioc）
#### 1、自动注入视图
##### （1）Activity继承com.lwh.jackknife.app.Activity,Fragment继承com.lwh.jackknife.app.Fragment
##### （2）保证布局的xml文件和Activity和Fragment的Java类的命名遵循一定的对应关系（Java类名必须以Activity或Fragment结尾）。
<blockquote>
    具体关系如下：
    <b>前缀+名字</b>
    例如：MainActivity.java映射的xml文件名就为activity_main.xml，TTSFragment.java映射的xml文件名就为fragment_t_t_s.xml。
    Java文件以大写字母分隔单词，xml以下划线分隔单词。
</blockquote>
 
#### 2、自动绑定控件
##### （1）不加注解
> 直接在Activity或Fragment声明控件（View及其子类）为成员变量，不加任何注解。它会以这个View的名字来绑定该控件在xml中的id的value，即@+id/后指定的内容。
##### （2）加@ViewId
> 优先级比不加注解高，简单的说，加上这个注解就不会使用默认的使用成员属性名来对应xml的控件id的方式，而是使用该注解指定的id与xml的控件id绑定。
##### （3）加@ViewIgnore
> 优先级最高，加上该注解，jackknife会直接跳过该控件的自动注入。一般使用在使用Java代码new出来的控件提取到全局的情况。
#### 3、自动注册事件
>  创建一个自定义的事件注解，在这个注解上配置@EventBase，并使用在你要实际回调的方法上，<b>注意保持参数列表跟原接口的某个回调方法的参数列表保持一致</b>。

### (二)数据库ORM模块（jackknife-orm）
#### 1、初始化配置
> 继承com.lwh.jackknife.app.Application，并在Application中完成初始化，2.0.15之后不再需要。可使用Orm.init(OrmConfig);//调用Orm的init方法
#### 2、完成实体类的编写
> 如果你想使用jackknife-orm自动创表，你只需要实现OrmTable接口再配置一些基本信息即可。
需要注意的是，在一个OrmTable的实现类中，至少要有一个配置主键或外键的属性。
##### （1）@Table
> 配置你要映射的表名
##### （2）@Column
> 配置你要映射的列名
##### （3）@PrimaryKey
> 配置主键
##### （4）@ForeignKey
> 配置外键
##### （5）@Unique
> 配置唯一约束
##### （6）@NotNull
> 配置非空约束
#### 3、创表
> 以User为例，TableManager.getInstance().createTable(User.class);//创建OrmTable的实现类的表
#### 4、数据的增删改查
> 首先要获取到操作该表的DAO对象，以User为例
OrmDao&lt;User&gt; dao = DaoFactory.getDao(User.class);
##### （1）单条插入
> dao.insert(User);
##### （2）多条插入
> dao.insert(List&lt;User&gt;);
##### （3）删除所有
> dao.deleteAll();
##### （4）按条件删除
> dao.delete(WhereBuilder);
##### （5）修改所有
> dao.updateAll();
##### （6）按条件修改
> dao.update(WhereBuilder);
##### （7）查询所有
> dao.selectAll();
##### （8）按条件查询
> dao.select(QueryBuilder);
##### （9）查询所有记录的条数
> dao.selectAllCount();
##### （10）查询满足条件记录的条数
> dao.selectCount(QueryBuilder);
##### （11）查询第一条记录
> dao.selectOne();
##### （12）查询最满足条件的一条记录
> dao.selectOne(QueryBuilder);

Name | Academy | score 
- | :-: | -: 
Harry Potter | Gryffindor| 90 
Hermione Granger | Gryffindor | 100 
Draco Malfoy | Slytherin | 90
Harry Potter | Gryffindor| 90 
Hermione Granger | Gryffindor | 100 
Draco Malfoy | Slytherin | 90
Harry Potter | Gryffindor| 90 
Hermione Granger | Gryffindor | 100 
Draco Malfoy | Slytherin | 90
Harry Potter | Gryffindor| 90 
Hermione Granger | Gryffindor | 100 
Draco Malfoy | Slytherin | 90


### (三)基于MVP设计理念的开发（jackknife-mvp）
#### 1、所需要依赖的类
##### （1）BaseModel（M层）
> 它是一个强大的数据筛选器，可以支持多条件筛选。
##### （2）IBaseView（V层）
> 在继承这个接口的接口中提供与界面显示相关的操作，比如显示某某数据，或获取从控件中获取用户输入的结果。建议继承这个
接口的接口也以I开头命名，避免与自定义View混淆。
##### （3）BasePresenter（P层）
> 在presenter中持有view和model的引用，它的职责是处理业务层的操作，如把model中的数据加载到view上显示、文件下载等。耗时操作务必在presenter中完成，此mvp框架可以有效防止activity的内存泄漏的。
##### （4）BaseActivity或BaseFragment（V层）
> 比如public class MainActivity extends BaseActivity<IMainView, MainPresenter> implements
IMainView。你可以用jackknife提供的com.lwh.jackknife.mvp.BaseActivity，也可以参考它自己来实现。
#### 2、一些思考
> 关于mvp这个架构，市面上众说纷纭，有赞赏的，也有批评的。总之，这个架构既有优点，又有缺点。先说优点，解除模型数据和UI显示的耦合，界面显示和业务操作逻辑分离，易于创建副本，提高可维护性。缺点也是显而易见的，Presenter和View类爆炸的问题很严重，也就是说，如果你只需要写一个很小的项目，是完全没有必要使用mvp的。
