JackKnife帮助文档
================================

一、关于如何配置环境
--------------------------------
如果要依赖jackknife的库，需要对号入座的加上以下两个配置。
### //指定仓库的地址，在project的build.gradle加入加粗的代码。
<blockquote>
allprojects {
  repositories {
    jcenter()
    <b>maven { url "https://jitpack.io" }</b>
  }
}
</blockquote>

### //依赖本库，在app模块的build.gradle加入加粗的代码。
<blockquote>
dependencies {
  <b>compile 'com.github.JackWHLiu:jackknife:1.0.0'</b>
}
</blockquote>

二、关于如何使用
--------------------------------
> (一)基于IOC的自动注入视图、绑定控件和注册事件（参考jackknife的app模块的Demo）
### 1、自动注入视图
（1）Activity继承com.lwh.jackknife.app.Activity,Fragment继承com.lwh.jackknife.app.Fragment
（2）保证布局的xml文件和Activity和Fragment的Java类的命名遵循一定的对应关系（Java类名必须以Activity或Fragment结尾）。
    具体关系如下：
    <b>前缀+名字</b>
    例如：MainActivity.java映射的xml文件名就为activity_main.xml，TTSFragment.java映射的xml文件名就为fragment_t_t_s.xml。
    Java文件以大写字母分隔单词，xml以下划线分隔单词。
### 2、自动绑定控件
（1）不加注解
    直接在Activity或Fragment声明控件（View及其子类）为成员变量，不加任何注解。它会以这个View的名字来绑定该控件在xml中的id的value，即@+id/后指定的内容。
（2）加@ViewId
    直接在Activity或Fragment声明控件（View及其子类）为成员变量，不加任何注解。它会以这个View的名字来绑定该控件在xml中的id的value，即@+id/后指定的内容。
    直接在Activity或Fragment声明控件（View及其子类）为成员变量，不加任何注解。它会以这个View的名字来绑定该控件在xml中的id的value，即@+id/后指定的内容。
    优先级比不加注解高，简单的说，加上这个注解就不会使用默认的使用成员属性名来对应xml的控件id的方式，而是使用该注解指定的id与xml的控件id绑定。
（3）加@ViewIgnore
    直接在Activity或Fragment声明控件（View及其子类）为成员变量，不加任何注解。它会以这个View的名字来绑定该控件在xml中的id的value，即@+id/后指定的内容。
    直接在Activity或Fragment声明控件（View及其子类）为成员变量，不加任何注解。它会以这个View的名字来绑定该控件在xml中的id的value，即@+id/后指定的内容。
    优先级最高，加上该注解，jackknife会直接跳过该控件的自动注入。一般使用在使用Java代码new出来的控件提取到全局的情况。
### 3、自动注册事件
  创建一个自定义的事件注解，在这个注解上配置@EventBase，并使用在你要实际回调的方法上，<b>注意保持参数列表跟原接口的某个回调方法的参数列表保持一致</b>。
