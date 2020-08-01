package com.lwh.jackknife.net;

import com.lwh.jackknife.util.NetworkUtils;

public interface NetworkChangeObserver {

    void onNetworkConnect(NetworkUtils.ApnType type);

    void onNetworkDisconnect();
}