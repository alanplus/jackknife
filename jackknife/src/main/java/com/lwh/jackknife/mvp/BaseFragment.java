package com.lwh.jackknife.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lwh.jackknife.app.Fragment;

/**
 * Fragment的通用基类，本项目所有Fragment必须继承此类，通过把自己的子类绑定在Presenter上，来实现Presenter层对
 * View层的代理，Fragment被销毁的时候，绑定了此Fragment的Presenter也会被自动销毁。所以凡是生命周期可能比
 * Fragment长的操作都应该放在Presenter中实现，比如在子线程中执行的操作。
 *
 * @param <V> 视图，Activity、Fragment等
 * @param <P> 主导器
 */
public abstract class BaseFragment<V extends IBaseView, P extends BasePresenter<V>> extends Fragment {

    /**
     * 主导器，在其子类可以使用它处理相关业务逻辑
     */
    protected P mPresenter;

    protected final String TAG = getClass().getSimpleName();

    public void onActivityCreated(Bundle savedInstanceState) {
        mPresenter = createPresenter();
        mPresenter.attachView((V)this);
        super.onActivityCreated(savedInstanceState);
        Log.w(TAG, "onActivityCreated()");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.w(TAG, "onAttach()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.w(TAG, "onDetach()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.w(TAG, "onStart()");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.w(TAG, "onCreateView()");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        Log.w(TAG, "onDestroy()");
    }

    /**
     * 创建出相关业务逻辑的主导器
     *
     * @return 具体业务逻辑主导器
     */
    protected abstract P createPresenter();
}
