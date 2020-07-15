package com.lwh.jackknife.util;

import okhttp3.Response;
import okio.ByteString;

public abstract class WebSocketStatusListener {

  public void onSocketOpened(Response response) {
  }

  public void onMessage(String text) {
  }

  public void onMessage(ByteString bytes) {
  }

  public void onSocketReconnect() {
  }

  public void onSocketClosing(int code, String reason) {
  }

  public void onSocketClosed(int code, String reason) {
  }

  public void onFailure(Throwable t, Response response) {
  }
}
