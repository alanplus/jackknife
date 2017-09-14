package com.lwh.jackknife.mvp;

import android.os.Bundle;
import android.util.Log;

import com.lwh.jackknife.app.Activity;

/**
 * Activity的通用基类，本项目所有Activity必须继承此类，通过把自己的子类绑定在Presenter上，来实现Presenter层对
 * View层的代理，Activity被销毁的时候，绑定了此Activity的Presenter也会被自动销毁。所以凡是生命周期可能比
 * Activity长的操作都应该放在Presenter中实现，比如在子线程中执行的操作。
 *
 * @param <V> 视图，Activity、Fragment等。
 * @param <P> 主导器。
 */
public abstract class BaseActivity<V extends IBaseView, P extends BasePresenter<V>> extends Activity {

    /**
     * 业务逻辑主导器。
     */
    protected P mPresenter;

    protected final String TAG = getClass().getSimpleName();

    /**
     * 创建出相关业务逻辑的主导器。
     *
     * @return 具体业务逻辑主导器。
     */
    protected abstract P createPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate()");
        mPresenter = createPresenter();
        mPresenter.attachView((V)this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(TAG, "onStop()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(TAG, "onStart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "onDestroy()");
        mPresenter.detachView();
    }
}
