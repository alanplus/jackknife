/*
 * Copyright (C) 2017 The JackKnife Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
