/*
 * Copyright (C) 2019 The JackKnife Open Source Project
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

package com.lwh.jackknife.lrc;

import java.io.Serializable;
import java.util.List;

public class Lyric implements Serializable {

    private List<Sentence> mSentences;

    public Lyric(List<Sentence> sentences) {
        this.mSentences = sentences;
    }

    public List<Sentence> getSentences() {
        return mSentences;
    }

    /**
     * 得到当前正在播放的那一句的下标 不可能找不到，因为最开头要加一句 自己的句子 ，所以加了以后就不可能找不到了
     *
     * @return 下标
     */
    public int getCurSentenceIndex(long t) {
        for (int i = 0; i < mSentences.size(); i++) {
            if (mSentences.get(i).isInTime(t)) {
                return i;
            }
        }
        // throw new RuntimeException("竟然出现了找不到的情况！");
        return -1;
    }
}
