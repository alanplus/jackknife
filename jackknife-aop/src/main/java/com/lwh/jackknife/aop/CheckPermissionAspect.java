/*
 * Copyright (C) 2019 The JackKnife Open Source Project
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

package com.lwh.jackknife.aop;

import android.Manifest;
import android.app.Activity;
import android.widget.Toast;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.lwh.jackknife.aop.annotation.Permission;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.List;

@Aspect
public class CheckPermissionAspect {

    @Pointcut("execution(@com.lwh.jackknife.aop.annotation.Permission * *(..)) && @annotation(permission)")
    public void checkPermission(Permission permission) {
    }

    public Activity getCurActivity() {
        return null;
    }

    @Around("checkPermission(permission)")
    public void aroundJoinPoint(final ProceedingJoinPoint joinPoint, final Permission permission) throws Throwable {
        final Activity activity = getCurActivity();
        if (XXPermissions.isHasPermission(activity, permission.value())) {
            joinPoint.proceed();//获得权限，执行原方法
        } else {
            XXPermissions.with(activity)
                    .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                    .permission(permission.value()) //不指定权限则自动获取清单中的危险权限
                    .request(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean isAll) {
                            try {
                                joinPoint.proceed();//获得权限，执行原方法
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {
                            if (quick) {
                                //如果是被永久拒绝就跳转到应用权限系统设置页面
                                if (denied.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                    Toast.makeText(activity, "请允许应用获取[手机存储]的权限", Toast.LENGTH_LONG).show();
                                }
                                XXPermissions.gotoPermissionSettings(activity);
                            }
                        }
                    });
        }
    }
}
