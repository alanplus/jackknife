package com.lwh.jackknife.xhttp;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;

public class XHttpTask implements Runnable {

    private XHttpService mHttpService;

    public XHttpTask(String url, XHttpRequest requestInfo, XHttpListener listener) {
        this.mHttpService = new JSONHttpService();
        this.mHttpService.setUrl(url);
        this.mHttpService.setHttpCallback(listener);
        String requestContent = JSON.toJSONString(requestInfo);
        try {
            this.mHttpService.setRequestData(requestContent.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.mHttpService.execute();
    }
}
