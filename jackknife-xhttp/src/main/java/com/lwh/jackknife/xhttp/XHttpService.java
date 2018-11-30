package com.lwh.jackknife.xhttp;

public interface XHttpService {

    void setUrl(String url);

    void execute();

    void setHttpCallback(XHttpListener httpListener);

    void setRequestData(byte[] requestData);
}
