package com.lwh.jackknife.xhttp;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONHttpListener<M extends String> implements XHttpListener {

    private Class<M> mResponseClass;

    private JSONDataListener mJsonListener;
    Handler handler = new Handler(Looper.getMainLooper());

    public JSONHttpListener(Class<M> reponceClass, JSONDataListener jsonLIstener) {
        this.mResponseClass = reponceClass;
        this.mJsonListener = jsonLIstener;
    }

    @Override
    public void onSuccess(InputStream inputStream) {
        String content = getContent(inputStream);
        final M responece;
        responece = JSON.parseObject(content, mResponseClass);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mJsonListener != null) {
                    mJsonListener.onSuccess(responece);
                }
            }
        });
    }

    private String getContent(InputStream inputStream) {
        String content = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                System.out.println("Error=" + e.toString());
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.out.println("Error=" + e.toString());
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    @Override
    public void onError(Exception e) {
    }
}
