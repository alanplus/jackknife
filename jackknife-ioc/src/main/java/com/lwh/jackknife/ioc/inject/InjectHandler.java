/*
 * Copyright (C) 2018 The JackKnife Open Source Project
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

package com.lwh.jackknife.ioc.inject;

import com.lwh.jackknife.ioc.bind.BindEvent;
import com.lwh.jackknife.ioc.bind.BindLayout;
import com.lwh.jackknife.ioc.bind.BindView;
import com.lwh.jackknife.ioc.SupportV;

public interface InjectHandler {

    int A_INDEX = 'A';
    int Z_INDEX = 'Z';
    String R_ID = ".R$id";
    String R_LAYOUT = ".R$layout";
    String UNDERLINE = "_";
    String METHOD_SET_CONTENT_VIEW = "setContentView";
    String METHOD_FIND_VIEW_BY_ID = "findViewById";
    String METHOD_INFLATE = "inflate";
    String METHOD_VALUE = "value";

    String generateLayoutName(SupportV v);
    void performInject(BindLayout bindLayout);
    void performInject(BindView bindView);
    void performInject(BindEvent bindEvent);
}
