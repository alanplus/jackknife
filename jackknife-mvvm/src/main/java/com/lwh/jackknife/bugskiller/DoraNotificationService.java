/*
 * Copyright (C) 2020 The Dora Open Source Project
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

package com.lwh.jackknife.bugskiller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.lwh.jackknife.INotificationService;
import com.lwh.jackknife.R;

/**
 * Writes log information to the notification bar.
 * 向通知栏写入日志信息。
 */
public class DoraNotificationService extends Service {

    private static final int NOTIFICATION_ID = 0x01;
    private IBinder mBinder;
    private RemoteViews mRemoteViews;
    private NotificationManager mNotificationManager;
    private PendingIntent mPendingIntent;

    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new DoraNotificationServiceImpl();
        return mBinder;
    }

    private void updateNotification(String title, String content) {
        String channelId = "dora.bugskiller";
        String channelName = "Dora";
        NotificationChannel channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(false);
            channel.setSound(null, null);
            channel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(channel);
        }
        Notification notification;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.dora_logo)
                    .setTicker(title)
                    .setContentIntent(mPendingIntent)
                    .setCustomContentView(mRemoteViews)
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.dora_logo)
                    .setTicker(title)
                    .setContentIntent(mPendingIntent)
                    .setCustomContentView(mRemoteViews)
                    .setChannelId(channelId).build();
        }
        notification.flags = Notification.FLAG_NO_CLEAR;
        mRemoteViews.setTextViewText(R.id.tv_dora_title, title);
        mRemoteViews.setTextViewText(R.id.tv_dora_content, content);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void cancelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            stopForeground(true);
        }
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.jk_bugskiller_notification);
    }

    private class DoraNotificationServiceImpl extends INotificationService.Stub {

        @Override
        public void updateNotification(String title, String content) throws RemoteException {
            DoraNotificationService.this.updateNotification(title, content);
        }

        @Override
        public void cancelNotification() throws RemoteException {
            DoraNotificationService.this.cancelNotification();
        }
    }
}
