package com.lwh.jackknife.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketManager implements IWebSocketManager {

    private final static int RECONNECT_INTERVAL = 10 * 1000;
    private final static long RECONNECT_MAX_TIME = 120 * 1000;
    private Context mContext;
    private String mWebSocketUrl;
    private WebSocket mWebSocket;
    private OkHttpClient mOkHttpClient;
    private Request mRequest;
    private int mCurrentStatus = WebSocketStatus.DISCONNECTED;
    private boolean mNeedReconnect;
    private boolean mManualClose = false;
    private WebSocketStatusListener mWebSocketStatusListener;
    private Lock mLock;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int reconnectCount = 0;
    private Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            if (mWebSocketStatusListener != null) {
                mWebSocketStatusListener.onSocketReconnect();
            }
            buildConnect();
        }
    };

    private WebSocketListener mWebSocketListener = new WebSocketListener() {

        @Override
        public void onOpen(WebSocket webSocket, final Response response) {
            mWebSocket = webSocket;
            setCurrentStatus(WebSocketStatus.CONNECTED);
            connected();
            if (mWebSocketStatusListener != null) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mWebSocketStatusListener.onSocketOpened(response);
                        }
                    });
                } else {
                    mWebSocketStatusListener.onSocketOpened(response);
                }
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, final ByteString bytes) {
            if (mWebSocketStatusListener != null) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mWebSocketStatusListener.onMessage(bytes);
                        }
                    });
                } else {
                    mWebSocketStatusListener.onMessage(bytes);
                }
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, final String text) {
            if (mWebSocketStatusListener != null) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mWebSocketStatusListener.onMessage(text);
                        }
                    });
                } else {
                    mWebSocketStatusListener.onMessage(text);
                }
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, final int code, final String reason) {
            if (mWebSocketStatusListener != null) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mWebSocketStatusListener.onSocketClosing(code, reason);
                        }
                    });
                } else {
                    mWebSocketStatusListener.onSocketClosing(code, reason);
                }
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, final int code, final String reason) {
            if (mWebSocketStatusListener != null) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mWebSocketStatusListener.onSocketClosed(code, reason);
                        }
                    });
                } else {
                    mWebSocketStatusListener.onSocketClosed(code, reason);
                }
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, final Throwable t, final Response response) {
            tryReconnect();
            if (mWebSocketStatusListener != null) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mWebSocketStatusListener.onFailure(t, response);
                        }
                    });
                } else {
                    mWebSocketStatusListener.onFailure(t, response);
                }
            }
        }
    };

    public WebSocketManager(Builder builder) {
        mContext = builder.context;
        mWebSocketUrl = builder.webSocketUrl;
        mNeedReconnect = builder.needReconnect;
        mOkHttpClient = builder.client;
        this.mLock = new ReentrantLock();
    }

    private void initWebSocket() {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .build();
        }
        if (mRequest == null) {
            mRequest = new Request.Builder()
                    .url(mWebSocketUrl)
                    .build();
        }
        mOkHttpClient.dispatcher().cancelAll();
        try {
            mLock.lockInterruptibly();
            try {
                mOkHttpClient.newWebSocket(mRequest, mWebSocketListener);
            } finally {
                mLock.unlock();
            }
        } catch (InterruptedException e) {
        }
    }

    @Override
    public WebSocket getWebSocket() {
        return mWebSocket;
    }

    public void setWebSocketStatusListener(WebSocketStatusListener webSocketStatusListener) {
        this.mWebSocketStatusListener = webSocketStatusListener;
    }

    @Override
    public synchronized boolean isWsConnected() {
        return mCurrentStatus == WebSocketStatus.CONNECTED;
    }

    @Override
    public synchronized int getCurrentStatus() {
        return mCurrentStatus;
    }

    @Override
    public synchronized void setCurrentStatus(int currentStatus) {
        this.mCurrentStatus = currentStatus;
    }

    @Override
    public void startConnect() {
        mManualClose = false;
        buildConnect();
    }

    @Override
    public void stopConnect() {
        mManualClose = true;
        disconnect();
    }

    private void tryReconnect() {
        if (!mNeedReconnect | mManualClose) {
            return;
        }

        if (!isNetworkConnected(mContext)) {
            setCurrentStatus(WebSocketStatus.DISCONNECTED);
            return;
        }

        setCurrentStatus(WebSocketStatus.RECONNECT);

        long delay = reconnectCount * RECONNECT_INTERVAL;
        mHandler.postDelayed(reconnectRunnable, delay > RECONNECT_MAX_TIME ? RECONNECT_MAX_TIME : delay);
        reconnectCount++;
    }

    private void cancelReconnect() {
        mHandler.removeCallbacks(reconnectRunnable);
        reconnectCount = 0;
    }

    private void connected() {
        cancelReconnect();
    }

    private void disconnect() {
        if (mCurrentStatus == WebSocketStatus.DISCONNECTED) {
            return;
        }
        cancelReconnect();
        if (mOkHttpClient != null) {
            mOkHttpClient.dispatcher().cancelAll();
        }
        if (mWebSocket != null) {
            boolean isClosed = mWebSocket.close(WebSocketStatus.CODE.NORMAL_CLOSE, WebSocketStatus.TIP.NORMAL_CLOSE);
            //非正常关闭连接
            if (!isClosed) {
                if (mWebSocketStatusListener != null) {
                    mWebSocketStatusListener.onSocketClosed(WebSocketStatus.CODE.ABNORMAL_CLOSE, WebSocketStatus.TIP.ABNORMAL_CLOSE);
                }
            }
        }
        setCurrentStatus(WebSocketStatus.DISCONNECTED);
    }

    private synchronized void buildConnect() {
        if (!isNetworkConnected(mContext)) {
            setCurrentStatus(WebSocketStatus.DISCONNECTED);
            return;
        }
        switch (getCurrentStatus()) {
            case WebSocketStatus.CONNECTED:
            case WebSocketStatus.CONNECTING:
                break;
            default:
                setCurrentStatus(WebSocketStatus.CONNECTING);
                initWebSocket();
        }
    }

    //发送消息
    @Override
    public boolean sendMessage(String msg) {
        return send(msg);
    }

    @Override
    public boolean sendMessage(ByteString byteString) {
        return send(byteString);
    }

    private boolean send(Object msg) {
        boolean isSend = false;
        if (mWebSocket != null && mCurrentStatus == WebSocketStatus.CONNECTED) {
            if (msg instanceof String) {
                isSend = mWebSocket.send((String) msg);
            } else if (msg instanceof ByteString) {
                isSend = mWebSocket.send((ByteString) msg);
            }
            //发送消息失败，尝试重连
            if (!isSend) {
                tryReconnect();
            }
        }
        return isSend;
    }

    //检查网络是否连接
    private boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static final class Builder {

        private Context context;
        private String webSocketUrl;
        private boolean needReconnect = true;
        private OkHttpClient client;

        public Builder(Context val) {
            context = val;
        }

        public Builder url(String val) {
            webSocketUrl = val;
            return this;
        }

        public Builder client(OkHttpClient val) {
            client = val;
            return this;
        }

        public Builder needReconnect(boolean val) {
            needReconnect = val;
            return this;
        }

        public WebSocketManager build() {
            return new WebSocketManager(this);
        }
    }
}
