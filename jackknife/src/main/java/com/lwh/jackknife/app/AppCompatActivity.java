/*
 * Copyright (C) 2017 The JackKnife Open Source Project
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

package com.lwh.jackknife.app;

import android.os.Bundle;

import com.lwh.jackknife.ioc.ViewInjector;

import java.lang.reflect.InvocationTargetException;

/**
 * Automatically inject a layout, bind views, and register events for activities.
 */
public abstract class AppCompatActivity extends android.support.v7.app.AppCompatActivity {

	/**
	 * 加入任务栈。
	 */
	private void joinTask(){
		if (getApplication() instanceof Application) {
			Application.getInstance().pushTask(this);
		}
	}

	/**
	 * 相当于finish，使用此方法会移除任务栈的缓存对象。
	 */
	public void finishTask(){
		if (getApplication() instanceof Application) {
			Application.getInstance().popTask();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			ViewInjector injector = new ViewInjector();
			injector.inject(this);
			if (getApplication() instanceof Application){
				joinTask();
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
