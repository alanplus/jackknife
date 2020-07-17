package com.lwh.jackknife;

import androidx.annotation.NonNull;

public interface App {

    @NonNull
    AppComponent getAppComponent();
}