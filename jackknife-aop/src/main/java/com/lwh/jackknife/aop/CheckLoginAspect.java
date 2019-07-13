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

import android.content.Context;
import android.widget.Toast;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CheckLoginAspect {

    @Around("execution(@com.lwh.jackknife.aop.annotation.CheckLogin * *(..))")
    public void aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        if (checkSession()) {
            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
            startLoginActivity();
            return;
        }
        joinPoint.proceed();
    }

    public boolean checkSession() {
        return false;
    }

    public Context getContext() {
        return null;
    }

    public void startLoginActivity() {
    }
}
