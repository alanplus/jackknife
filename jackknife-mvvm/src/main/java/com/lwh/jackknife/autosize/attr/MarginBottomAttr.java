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

public class MarginBottomAttr extends AutoAttr {
    public MarginBottomAttr(int pxVal, int baseWidth, int baseHeight) {
        super(pxVal, baseWidth, baseHeight);
    }

    public static MarginBottomAttr generate(int val, int baseFlag) {
        MarginBottomAttr attr = null;
        switch (baseFlag) {
            case AutoAttr.BASE_WIDTH:
                attr = new MarginBottomAttr(val, Attrs.MARGIN_BOTTOM, 0);
                break;
            case AutoAttr.BASE_HEIGHT:
                attr = new MarginBottomAttr(val, 0, Attrs.MARGIN_BOTTOM);
                break;
            case AutoAttr.BASE_DEFAULT:
                attr = new MarginBottomAttr(val, 0, 0);
                break;
        }
        return attr;
    }

    @Override
    protected int attrVal() {
        return Attrs.MARGIN_BOTTOM;
    }

    @Override
    protected boolean defaultBaseWidth() {
        return false;
    }

    @Override
    protected void execute(View view, int val) {
        if (!(view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)) {
            return;
        }
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.bottomMargin = val;
    }
}
