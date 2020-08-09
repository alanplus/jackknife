/*
 * Copyright (C) 2020 The JackKnife Open Source Project
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

package com.lwh.jackknife.autosize.attr;

import android.view.View;
import android.view.ViewGroup;

public class WidthAttr extends AutoAttr {
    public WidthAttr(int pxVal, int baseWidth, int baseHeight) {
        super(pxVal, baseWidth, baseHeight);
    }

    public static WidthAttr generate(int val, int baseFlag) {
        WidthAttr widthAttr = null;
        switch (baseFlag) {
            case AutoAttr.BASE_WIDTH:
                widthAttr = new WidthAttr(val, Attrs.WIDTH, 0);
                break;
            case AutoAttr.BASE_HEIGHT:
                widthAttr = new WidthAttr(val, 0, Attrs.WIDTH);
                break;
            case AutoAttr.BASE_DEFAULT:
                widthAttr = new WidthAttr(val, 0, 0);
                break;
        }
        return widthAttr;
    }

    @Override
    protected int attrVal() {
        return Attrs.WIDTH;
    }

    @Override
    protected boolean defaultBaseWidth() {
        return true;
    }

    @Override
    protected void execute(View view, int val) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = val;
    }

}
