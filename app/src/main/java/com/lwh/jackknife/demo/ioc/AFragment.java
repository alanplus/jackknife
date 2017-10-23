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
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lwh.jackknife.demo.R;
import com.lwh.jackknife.ioc.ViewId;

public class AFragment extends CustomFragment {

    @ViewId(R.id.textview_third_a)
    TextView textview_third_a;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toast.makeText(getActivity(), "The next page is BFragment, and you can click in the middle of the screen.", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.textview_third_a)
    public void onClick(View view) {
        textview_third_a.setTextColor(getResources().getColor(R.color.gainsboro));
    }
}
