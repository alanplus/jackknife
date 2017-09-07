package com.lwh.jackknife.mvp;

/**
 * 遵循MVP(Model-View-Presenter)设计理念。使用需要注意以下要点：
 * <ol>
 *     <li>仅用于界面的显示和改变操作。</li>
 *     <li>通过继承此接口来作为具体某个界面的抽象。</li>
 * </ol>
 *
 * @author lwh
 */
public interface IBaseView {

    /**
     * 显示加载进度，全局的界面加载进度，局部界面刷新慎用。
     */
    void showLoading();

    /**
     * 隐藏加载进度，全局的界面加载进度，局部界面刷新慎用。
     */
    void hideLoading();
}
