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

public interface Attrs {

    int WIDTH = 1;
    int HEIGHT = WIDTH << 1;
    int TEXT_SIZE = HEIGHT << 1;
    int PADDING = TEXT_SIZE << 1;
    int MARGIN = PADDING << 1;
    int MARGIN_LEFT = MARGIN << 1;
    int MARGIN_TOP = MARGIN_LEFT << 1;
    int MARGIN_RIGHT = MARGIN_TOP << 1;
    int MARGIN_BOTTOM = MARGIN_RIGHT << 1;
    int PADDING_LEFT = MARGIN_BOTTOM << 1;
    int PADDING_TOP = PADDING_LEFT << 1;
    int PADDING_RIGHT = PADDING_TOP << 1;
    int PADDING_BOTTOM = PADDING_RIGHT << 1;
    int MIN_WIDTH = PADDING_BOTTOM << 1;
    int MAX_WIDTH = MIN_WIDTH << 1;
    int MIN_HEIGHT = MAX_WIDTH << 1;
    int MAX_HEIGHT = MIN_HEIGHT << 1;
}
