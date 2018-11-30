package com.lwh.jackknife.xhttp;

import java.util.concurrent.FutureTask;

public class XHttp {

    public static void sendPostRequest(XHttpRequest requestInfo, String url, Class responce,
                                       JSONDataListener dataListener) {
        XHttpListener listener = new JSONHttpListener(responce, dataListener);
        XHttpTask httpTask = new XHttpTask(url, requestInfo, listener);
        ThreadPoolManager.getInstance().execute(new FutureTask<>(httpTask, null));
    }
}
