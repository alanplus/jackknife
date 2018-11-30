package com.lwh.jackknife.xhttp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class JSONHttpService implements XHttpService {

    XHttpListener mHttpListener;
    private HttpClient mHttpClient = new DefaultHttpClient();
    String mUrl;
    private byte[] mRequestData;
    private HttpRequestBase httpRequestBase;
    private HttpResponseHandler mHttpResponseHandler = new HttpResponseHandler();

    @Override
    public void setUrl(String url) {
        this.mUrl = url;
    }

    @Override
    public void setRequestData(byte[] requestData) {
        this.mRequestData = requestData;
    }

    @Override
    public void execute() {
        httpRequestBase = new HttpGet(mUrl);
        try {
            this.mHttpClient.execute(httpRequestBase, mHttpResponseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setHttpCallback(XHttpListener httpListener) {
        this.mHttpListener = httpListener;
    }

    private class HttpResponseHandler extends BasicResponseHandler {

        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException {
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                HttpEntity httpEntity = response.getEntity();
                if (mHttpListener != null) {
                    try {
                        InputStream inputStream = httpEntity.getContent();
                        mHttpListener.onSuccess(inputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (mHttpListener != null) {
                    mHttpListener.onError(null);
                }

            }
            return null;
        }
    }
}
