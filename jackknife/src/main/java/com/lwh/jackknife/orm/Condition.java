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

package com.lwh.jackknife.orm;

public class Condition {

    private String selection;
    private String[] selectionArgs;

    public Condition() {
    }

    public Condition(String selection, String[] selectionArgs) {
        this.selection = selection;
        this.selectionArgs = selectionArgs;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        this.selectionArgs = selectionArgs;
    }

    public void appendSelection(String selection) {
        if (hasSelection()) {
            this.selection = new StringBuilder(this.selection).append(selection).toString();
        }
        setSelection(selection);
    }

    public String getSelection() {
        return selection;
    }

    public String[] getSelectionArgs() {
        return selectionArgs;
    }

    public boolean hasSelection() {
        return selection != null;
    }

    public boolean hasSelectionArgs() {
        return selectionArgs != null;
    }
}
