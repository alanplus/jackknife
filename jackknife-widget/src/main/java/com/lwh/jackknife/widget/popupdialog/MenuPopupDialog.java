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

package com.lwh.jackknife.widget.popupdialog;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

public class MenuPopupDialog extends PopupDialog {

    private MenuPopupDialog(MenuBuilder builder) {
        Activity activity = (Activity) builder.getContext();
        mDialogView = builder.getDialogView();
        LayoutInflater inflater = LayoutInflater.from(builder.getContext());
        mDecorView = (FrameLayout) activity.getWindow().getDecorView()
                .findViewById(android.R.id.content);
        mContentView = (ViewGroup) mDialogView.getView(inflater, mDecorView);
        mContentView.setLayoutParams(mDialogView.getLayoutParams());
        applyAnimation(builder);
        mContentView.addView(createView(builder, inflater));
    }

    public static MenuBuilder newMenuBuilder(Context context) {
        return new MenuBuilder(context);
    }

    private View createView(MenuBuilder builder, LayoutInflater inflater) {
        if (mDialogView instanceof MenuDialogView) {
            MenuDialogView menuDialogView = (MenuDialogView) mDialogView;
            menuDialogView.setOnMenuClickListener(builder.getOnMenuClickListener());
            menuDialogView.setMenuItems(builder.getMenuItems());
            menuDialogView.setShowCancelMenu(builder.isShowCancel());
            menuDialogView.setCancelText(builder.getCancelText());
            menuDialogView.setMenuTextColor(builder.getMenuItemTextColor());
            menuDialogView.setMenuTextSize(builder.getMenuItemTextSize());
            menuDialogView.setMenuItemBackground(builder.getMenuItemBackground());
            menuDialogView.setCancelMenuBackground(builder.getCancelBackground());
        }
        return mDialogView.getView(inflater, mDecorView);
    }

    public static class MenuBuilder extends PopupDialog.Builder {

        private List<String> menuItems;
        private MenuDialogView.OnMenuClickListener mListener;
        private MenuDialogView mMenuDialogView;
        private int cancelBackground = INVALID_COLOR;
        private boolean isShowCancel = true;
        private String cancelText;
        private float menuItemTextSize = INVALID;
        private int menuItemTextColor = INVALID_COLOR;
        private int menuItemBackground = INVALID_COLOR;

        public MenuBuilder(Context context) {
            super(context);
        }

        public MenuBuilder setMenuDialogView(MenuDialogView view) {
            this.mMenuDialogView = view;
            return this;
        }

        public MenuDialogView getMenuDialogView() {
            return mMenuDialogView;
        }

        public List<String> getMenuItems() {
            return menuItems;
        }

        public MenuBuilder setMenuItems(List<String> itemNames) {
            this.menuItems = itemNames;
            return this;
        }

        public MenuDialogView.OnMenuClickListener getOnMenuClickListener() {
            return mListener;
        }

        public MenuBuilder setOnMenuClickListener(MenuDialogView.OnMenuClickListener
                                                          listener) {
            this.mListener = listener;
            return this;
        }


        public int getCancelBackground() {
            return cancelBackground;
        }

        public PopupDialog.Builder setCancelBackground(int cancelBackground) {
            this.cancelBackground = cancelBackground;
            return this;
        }

        public PopupDialog create() {
            if (mMenuDialogView == null) {
                throw new IllegalArgumentException("lack dialog view.");
            }
            return new PopupDialog(this);
        }

        public boolean isShowCancel() {
            return isShowCancel;
        }

        public PopupDialog.Builder setShowCancel(boolean isShowCancel) {
            this.isShowCancel = isShowCancel;
            return this;
        }

        public String getCancelText() {
            return cancelText;
        }

        public PopupDialog.Builder setCancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public int getMenuItemTextColor() {
            return menuItemTextColor;
        }

        public PopupDialog.Builder setMenuItemTextColor(int textColor) {
            this.menuItemTextColor = textColor;
            return this;
        }

        public float getMenuItemTextSize() {
            return menuItemTextSize;
        }

        public PopupDialog.Builder setMenuItemTextSize(float textSize) {
            this.menuItemTextSize = textSize;
            return this;
        }

        public int getMenuItemBackground() {
            return menuItemBackground;
        }

        public PopupDialog.Builder setMenuItemBackground(int background) {
            this.menuItemBackground = background;
            return this;
        }
    }
}
