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

package com.lwh.jackknife.av.webrtc;

import android.os.Handler;
import android.util.Log;

import com.lwh.jackknife.av.webrtc.util.AsyncHttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import de.tavendo.autobahn.WebSocket.WebSocketConnectionObserver;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;

/**
 * WebSocket client implementation.
 * <p>
 * <p>All public methods should be called from a looper executor thread
 * passed in a constructor, otherwise exception will be thrown.
 * All events are dispatched on the same thread.
 */

public class WebSocketChannelClient {

    private static final String TAG = "WSChannelRTCClient";
    private static final int CLOSE_TIMEOUT = 1000;
    private final WebSocketChannelEvents events;
    private final Handler handler;
    private final Object closeEventLock = new Object();
    // WebSocket send queue. Messages are added to the queue when WebSocket
    // client is not registered and are consumed in register() call.
    private final LinkedList<String> wsSendQueue;
    private WebSocketConnection ws;
    private WebSocketObserver wsObserver;
    private String wsServerUrl;
    private String postServerUrl;
    private String roomID;
    private String clientID;
    private WebSocketConnectionState state;
    private boolean closeEvent;

    public WebSocketChannelClient(Handler handler, WebSocketChannelEvents events) {
        this.handler = handler;
        this.events = events;
        roomID = null;
        clientID = null;
        wsSendQueue = new LinkedList<>();
        state = WebSocketConnectionState.NEW;
    }

    public WebSocketConnectionState getState() {
        return state;
    }

    public void connect(String wsUrl, final String postUrl) {
        wsServerUrl = wsUrl;
        postServerUrl = postUrl;
        postServerUrl = postUrl;
        closeEvent = false;
        ws = new WebSocketConnection();
        wsObserver = new WebSocketObserver();
        try {
            Log.d(TAG, "connect wsUrl=" + wsUrl + ", postUrl=" + postUrl);
            ws.connect(new URI(wsServerUrl), wsObserver);
        } catch (URISyntaxException e) {
            reportError("URI error: " + e.getMessage());
        } catch (WebSocketException e) {
            reportError("WebSocket connection error: " + e.getMessage());
        }
    }

    public void register(final String roomID, final String clientID) {
        checkIfCalledOnValidThread();
        this.roomID = roomID;
        this.clientID = clientID;
        if (state != WebSocketConnectionState.CONNECTED) {
            Log.w(TAG, "WebSocket register() in state " + state);
            return;
        }
        Log.d(TAG, "Registering WebSocket for room " + roomID + ". ClientID: " + clientID);
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "register");
            json.put("roomid", roomID);
            json.put("clientid", clientID);
            Log.d(TAG, "C->WSS: " + json.toString());
            ws.sendTextMessage(json.toString());
            state = WebSocketConnectionState.REGISTERED;
            // Send any previously accumulated messages.
            for (String sendMessage : wsSendQueue) {
                send(sendMessage);
            }
            wsSendQueue.clear();
        } catch (JSONException e) {
            reportError("WebSocket register JSON error: " + e.getMessage());
        }
    }

    public void send(String message) {
        checkIfCalledOnValidThread();
        switch (state) {
            case NEW:
            case CONNECTED:
                // Store outgoing messages and send them after websocket client
                // is registered.
                Log.d(TAG, "WS ACC: " + message);
                wsSendQueue.add(message);
                return;
            case ERROR:
            case CLOSED:
                Log.e(TAG, "WebSocket send() in error or closed state : " + message);
                return;
            case REGISTERED:
                JSONObject json = new JSONObject();
                try {
                    json.put("cmd", "send");
                    json.put("msg", message);
                    message = json.toString();
                    Log.d(TAG, "C->WSS: " + message);
                    ws.sendTextMessage(message);
                } catch (JSONException e) {
                    reportError("WebSocket send JSON error: " + e.getMessage());
                }
                break;
        }
    }

    // This call can be used to send WebSocket messages before WebSocket
    // connection is opened.
    public void post(String message) {
        checkIfCalledOnValidThread();
        sendWSSMessage("POST", message);
    }

    public void disconnect(boolean waitForComplete) {
        checkIfCalledOnValidThread();
        Log.d(TAG, "Disconnect WebSocket. State: " + state);
        if (state == WebSocketConnectionState.REGISTERED) {
            // Send "bye" to WebSocket server.
            send("{\"type\": \"bye\"}");
            state = WebSocketConnectionState.CONNECTED;
            // Send http DELETE to http WebSocket server.
            sendWSSMessage("DELETE", "");
        }
        // Close WebSocket in CONNECTED or ERROR states only.
        if (state == WebSocketConnectionState.CONNECTED || state == WebSocketConnectionState.ERROR) {
            ws.disconnect();
            state = WebSocketConnectionState.CLOSED;

            // Wait for websocket close event to prevent websocket library from
            // sending any pending messages to deleted looper thread.
            if (waitForComplete) {
                synchronized (closeEventLock) {
                    while (!closeEvent) {
                        try {
                            closeEventLock.wait(CLOSE_TIMEOUT);
                            break;
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Wait error: " + e.toString());
                        }
                    }
                }
            }
        }
        Log.d(TAG, "Disconnecting WebSocket done.");
    }

    private void reportError(final String errorMessage) {
        Log.e(TAG, errorMessage);
        handler.post(() -> {
            if (state != WebSocketConnectionState.ERROR) {
                state = WebSocketConnectionState.ERROR;
                events.onWebSocketError(errorMessage);
            }
        });
    }

    // Asynchronously send POST/DELETE to WebSocket server.
    private void sendWSSMessage(final String method, final String message) {
        String postUrl = postServerUrl + "/" + roomID + "/" + clientID;
        Log.d(TAG, "WS " + method + " : " + postUrl + " : " + message);
        AsyncHttpURLConnection httpConnection =
                new AsyncHttpURLConnection(method, postUrl, message, new AsyncHttpURLConnection.AsyncHttpEvents() {
                    @Override
                    public void onHttpError(String errorMessage) {
                        reportError("WS " + method + " error: " + errorMessage);
                    }

                    @Override
                    public void onHttpComplete(String response) {
                    }
                });
        httpConnection.send();
    }

    // Helper method for debugging purposes. Ensures that WebSocket method is
    // called on a looper thread.
    private void checkIfCalledOnValidThread() {
        if (Thread.currentThread() != handler.getLooper().getThread()) {
            throw new IllegalStateException("WebSocket method is not called on valid thread");
        }
    }

    /**
     * Possible WebSocket connection states.
     */
    public enum WebSocketConnectionState {
        NEW, CONNECTED, REGISTERED, CLOSED, ERROR
    }

    /**
     * Callback interface for messages delivered on WebSocket.
     * All events are dispatched from a looper executor thread.
     */
    public interface WebSocketChannelEvents {
        void onWebSocketMessage(final String message);

        void onWebSocketClose();

        void onWebSocketError(final String description);
    }

    private class WebSocketObserver implements WebSocketConnectionObserver {
        @Override
        public void onOpen() {
            Log.d(TAG, "WebSocket connection opened to: " + wsServerUrl);
            handler.post(() -> {
                state = WebSocketConnectionState.CONNECTED;
                // Check if we have pending register request.
                if (roomID != null && clientID != null) {
                    register(roomID, clientID);
                }
            });
        }

        @Override
        public void onClose(WebSocketCloseNotification code, String reason) {
            Log.d(TAG, "WebSocket connection closed. Code: " + code + ". Reason: " + reason + ". State: "
                    + state);
            synchronized (closeEventLock) {
                closeEvent = true;
                closeEventLock.notify();
            }
            handler.post(() -> {
                if (state != WebSocketConnectionState.CLOSED) {
                    state = WebSocketConnectionState.CLOSED;
                    events.onWebSocketClose();
                }
            });
        }

        @Override
        public void onTextMessage(String payload) {
            Log.d(TAG, "WSS->C: " + payload);
            final String message = payload;
            handler.post(() -> {
                if (state == WebSocketConnectionState.CONNECTED
                        || state == WebSocketConnectionState.REGISTERED) {
                    events.onWebSocketMessage(message);
                }
            });
        }

        @Override
        public void onRawTextMessage(byte[] payload) {
        }

        @Override
        public void onBinaryMessage(byte[] payload) {
        }
    }
}
