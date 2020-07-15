/*
 * Copyright (C) 2020 The JackKnife Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
