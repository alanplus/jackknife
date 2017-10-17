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

package com.lwh.jackknife.demo.ioc;

import android.os.Bundle;
import android.widget.TextView;

import com.lwh.jackknife.demo.R;
import com.lwh.jackknife.ioc.ViewId;
import com.lwh.jackknife.mvp.BaseActivity;

public class SecondActivity extends BaseActivity<ISecondView, SecondPresenterV2> implements ISecondView{

    @ViewId(R.id.textview_second_firstuser)
    TextView textview_second_firstuser;

    TextView textview_second_lastuser;

    @Override
    protected SecondPresenterV2 createPresenter() {
        return new SecondPresenterV2();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.fetchUsers();
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showFirstUser(User user) {
        textview_second_firstuser.setText(user.getName()+":"+user.getAge());
    }

    @Override
    public void showLastUser(User user) {
        textview_second_lastuser.setText(user.getName()+":"+user.getAge());
    }
}
