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

package com.lwh.jackknife.widget.floatingview;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.core.view.ViewCompat;

import com.lwh.jackknife.widget.R;
import com.lwh.jackknife.widget.util.DensityUtils;
import com.lwh.jackknife.widget.util.GlobalContext;

import java.lang.ref.WeakReference;


public class FloatingWindow implements IFloatingWindow {

    private FloatingMagnetView mFloatingMagnetView;
    private static volatile FloatingWindow mInstance;
    private WeakReference<FrameLayout> mContainer;
    @LayoutRes
    private int mLayoutId = R.layout.jknf_floating_magnet_view;
    private ViewGroup.LayoutParams mLayoutParams = getDefaultLayoutParams();
    MagnetViewListener mMagnetViewListener;
    private int mIconRes;

    private FloatingWindow() {
    }

    public static FloatingWindow get() {
        if (mInstance == null) {
            synchronized (FloatingWindow.class) {
                if (mInstance == null) {
                    mInstance = new FloatingWindow();
                }
            }
        }
        return mInstance;
    }

    @Override
    public FloatingWindow remove() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mFloatingMagnetView == null) {
                    return;
                }
                if (ViewCompat.isAttachedToWindow(mFloatingMagnetView) && getContainer() != null) {
                    getContainer().removeView(mFloatingMagnetView);
                }
                mFloatingMagnetView = null;
            }
        });
        return this;
    }

    @Override
    public FloatingWindow add(int drawableResId) {
        createFloatingView(drawableResId);
        return this;
    }

    private void createFloatingView(@DrawableRes int drawableResId) {
        synchronized (this) {
            if (mFloatingMagnetView != null) {
                return;
            }
            icon(drawableResId);
            mFloatingMagnetView = new FloatingMagnetView(GlobalContext.get());
            LayoutInflater.from(GlobalContext.get()).inflate(mLayoutId, mFloatingMagnetView, true);
            View iconView = mFloatingMagnetView.findViewById(R.id.iv_floating_magnet_view_icon);
            iconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMagnetViewListener.onClick(mFloatingMagnetView);
                }
            });
            iconView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mFloatingMagnetView.onTouchEvent(event);
                    return false;
                }
            });
            mFloatingMagnetView.setLayoutParams(mLayoutParams);
            addViewToWindow(mFloatingMagnetView);
        }
    }

    @Override
    public FloatingWindow attach(Activity activity) {
        attach(getContentView(activity));
        return this;
    }

    @Override
    public FloatingWindow attach(FrameLayout container) {
        if (container == null || mFloatingMagnetView == null) {
            mContainer = new WeakReference<>(container);
            return this;
        }
        if (mFloatingMagnetView.getParent() == container) {
            return this;
        }
        if (getContainer() != null && mFloatingMagnetView.getParent() == getContainer()) {
            getContainer().removeView(mFloatingMagnetView);
        }
        mContainer = new WeakReference<>(container);
        container.addView(mFloatingMagnetView);
        return this;
    }

    @Override
    public FloatingWindow detach(Activity activity) {
        detach(getContentView(activity));
        return this;
    }

    @Override
    public FloatingWindow detach(FrameLayout container) {
        if (mFloatingMagnetView != null && container != null && ViewCompat.isAttachedToWindow(mFloatingMagnetView)) {
            container.removeView(mFloatingMagnetView);
        }
        if (getContainer() == container) {
            mContainer = null;
        }
        return this;
    }

    @Override
    public FloatingMagnetView getView() {
        return mFloatingMagnetView;
    }

    @Override
    public FloatingWindow icon(@DrawableRes int resId) {
        mIconRes = resId;
        return this;
    }

    @Override
    public FloatingWindow customView(FloatingMagnetView magnetView) {
        this.mFloatingMagnetView = magnetView;
        return this;
    }

    @Override
    public FloatingWindow customView(@LayoutRes int resource) {
        mLayoutId = resource;
        return this;
    }

    @Override
    public FloatingWindow layoutParams(ViewGroup.LayoutParams params) {
        mLayoutParams = params;
        if (mFloatingMagnetView != null) {
            mFloatingMagnetView.setLayoutParams(params);
        }
        return this;
    }

    @Override
    public FloatingWindow listener(MagnetViewListener magnetViewListener) {
        mMagnetViewListener = magnetViewListener;
        if (mFloatingMagnetView != null) {
            mFloatingMagnetView.setMagnetViewListener(magnetViewListener);
        }
        return this;
    }

    private void addViewToWindow(View view) {
        if (getContainer() == null) {
            return;
        }
        getContainer().addView(view);
    }

    private void removeViewFromWindow(View view) {
        if (getContainer() == null) {
            return;
        }
        getContainer().removeView(view);
    }

    private FrameLayout getContainer() {
        if (mContainer == null) {
            return null;
        }
        return mContainer.get();
    }

    public int getIconRes() {
        return mIconRes;
    }

    private FrameLayout.LayoutParams getDefaultLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        params.setMargins(FloatingMagnetView.MARGIN_EDGE, params.topMargin, FloatingMagnetView.MARGIN_EDGE, (int) DensityUtils.dp2px(200, GlobalContext.get()));
        return params;
    }

    private FrameLayout getContentView(Activity activity) {
        if (activity == null) {
            return null;
        }
        try {
            return (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}