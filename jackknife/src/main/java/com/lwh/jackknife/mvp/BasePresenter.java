package com.lwh.jackknife.mvp;

import android.app.Activity;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * 遵循MVP（Model-View-Presenter）设计理念。使用需要注意以下要点：
 * <ol>
 *     <li>presenter可以翻译成主导器/中介者，专门用来执行业务逻辑。特别地，耗时操作在Presenter中执行还可以防止
 *     内存泄漏</li>
 *     <li>presenter中持有view和model的引用，用来加载model中的数据显示到view上。</li>
 * </ol>
 *
 * @author lwh
 */
public abstract class BasePresenter<V extends IBaseView> {

    /**
     * 在presenter中持有V层（即Activity、Fragment以及Dialog等等）的弱引用{@link WeakReference}，这些V通常
     * 拥有生命周期方法（lifecycle method）。如果使用强引用{@link Reference}，当Activity被销毁的时候，
     * presenter中还持有Activity的引用，这种情况是可能的（possible），完全有可能在presenter中开子线程执行耗时
     * 操作（time-consuming operation）。子线程它跟主线程是单独执行的，在主线程android.app.ActivityThread中
     * 会执行销毁Activity的操作，此时发现还有类在引用这个类，所以无法完全销毁，它只销毁了本线程所管辖的那个。
     * 这样就导致了内存泄漏。
     *
     * <p>
     *      <b>Note:不同的线程访问逻辑上是同一个对象的对象，其实是不同的对象，如果要变成同一个对象，这个对象在声
     *      明时要加volatile关键字。</b>
     * </p>
     *
     * Java的四大引用类型，包括强引用、软引用、弱引用和虚引用{@link PhantomReference}，默认就是强引用，即什么
     * 都不写。强引用，JVM宁愿抛出OutOfMemory异常也不会去回收该对象，直到程序崩溃JVM也不会去回收这些对象。软引用，
     * 当内存使用过高时，JVM会回收掉一些软引用来保持内存资源的充足，内存够用了，会停止回收。弱引用，如果弱引用所引
     * 用的对象回收，JVM也会把这个弱引用加入引用队列{@link ReferenceQueue}中，等待被回收。虚引用，及其不稳定的，
     * 跟有没有引用差不多，随时都有可能被回收。另一方面（Otherwise），如果使用的是软引用{@link SoftReference}
     * 或弱引用。正常执行{@link Activity#onDestroy()}的话，在Activity被销毁的时候，会调用
     * {@link BasePresenter#detachView()}来解除这个弱引用。不能正常执行{@link Activity#onDestroy()}的话，
     * 弱引用最终会被Java虚拟机（JVM）的垃圾收集器（GC）扫描到，并回收掉，不会造成内存泄漏。
     */
    protected WeakReference<V> mViewRef;

    /**
     * 依附V视图层。
     *
     * @param view V层对象，如Activity。
     */
    public void attachView(V view){
        mViewRef = new WeakReference<V>(view);
    }

    /**
     * 解除对V视图层的依附。
     */
    public void detachView(){
        if (mViewRef != null){
            mViewRef.clear();
            mViewRef = null;
        }
    }

    /**
     * 得到弱引用中的V。
     *
     * @return V层对象，如Activity。
     */
    protected V getView(){
        return mViewRef.get();
    }

    /**
     * 得到弱引用中视图层的实例化对象。
     *
     * @param viewClass V层对象的类型的字节码。
     * @param <T> V层对象的类型。
     * @return 视图层的实例化对象。
     */
    protected  <T> T getView(Class<T> viewClass) {
        return (T) getView();
    }

    /**
     * 检测presenter是否依附上V层对象，在执行操作时需要检测。
     *
     * @return true表示依附上，false表示没有依附上。
     */
    public boolean isViewAttached(){
        return mViewRef != null && mViewRef.get() != null;
    }
}
