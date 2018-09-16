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

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.lwh.jackknife.widget.R;

public class PopupDialog {

    protected AbstractDialogView mDialogView;
    protected FrameLayout mDecorView;
    protected ViewGroup mContentView;
    private Animation mPushOutAnim;
    private Animation mPushInAnim;
    private boolean mDismissing;

    public PopupDialog() {
    }

    public PopupDialog(Builder builder) {
        this.mDialogView = builder.dialogView;
        applyAnimation(builder);
    }

    protected void applyAnimation(Builder builder) {
        mPushOutAnim = builder.getPushOutAnimation();
        mPushInAnim = builder.getPushInAnimation();
    }

    public void show() {
        onAttached(mContentView);
    }

    public void dismiss() {
        if (mDismissing) {
            return;
        }
        mPushOutAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDecorView.post(new Runnable() {
                    @Override
                    public void run() {
                        mDecorView.removeView(mContentView);
                        mDismissing = false;
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mContentView.startAnimation(mPushOutAnim);
        mDismissing = true;
    }

    private void onAttached(ViewGroup viewRoot) {
        mDecorView.addView(viewRoot);
        mContentView.startAnimation(mPushInAnim);
        mContentView.requestFocus();
        mDialogView.setOnBackListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (event.getAction()) {
                    case KeyEvent.ACTION_UP:
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dismiss();
                            return true;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        mDialogView.setOnCancelListener(new AbstractDialogView.OnCancelListener() {
            @Override
            public void onCancel() {
                dismiss();
            }
        });
    }

    public static class Builder {

        protected static final int INVALID = -1;
        protected static final int INVALID_COLOR = 0;
        private Animation pushInAnim;
        private Animation pushOutAnim;
        private Context context;
        private AbstractDialogView dialogView;

        public Builder(Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        public AbstractDialogView getDialogView() {
            return dialogView;
        }

        public Animation getPushInAnimation() {
            return (pushInAnim == null) ? AnimationUtils.loadAnimation(context,
                    R.anim.jknf_push_in) : pushInAnim;
        }

        public Builder setPushInAnimation(int animResId) {
            this.pushInAnim = AnimationUtils.loadAnimation(context, animResId);
            return this;
        }

        public Builder setPushInAnimation(Animation in) {
            this.pushInAnim = in;
            return this;
        }

        private Animation getPushOutAnimation() {
            return (pushOutAnim == null) ? AnimationUtils.loadAnimation(context,
                    R.anim.jknf_push_out) : pushOutAnim;
        }

        public Builder setPushOutAnimation(int animResId) {
            this.pushOutAnim = AnimationUtils.loadAnimation(context, animResId);
            return this;
        }

        public Builder setPushOutAnimation(Animation out) {
            this.pushOutAnim = out;
            return this;
        }

        public Builder setDialogView(DialogView dialogView) {
            this.dialogView = dialogView;
            return this;
        }
    }
}

