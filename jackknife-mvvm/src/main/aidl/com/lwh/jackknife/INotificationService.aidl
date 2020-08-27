// INotificationService.aidl
package com.lwh.jackknife;

interface INotificationService {

    void updateNotification(String title, String content);
    void cancelNotification();
}
