package com.lwh.jackknife.xhttp;

import java.io.InputStream;

public interface XHttpListener {

    void onSuccess(InputStream inputStream);

    void onError(Exception e);
}
