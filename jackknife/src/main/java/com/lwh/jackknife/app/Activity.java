package com.lwh.jackknife.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lwh.jackknife.ioc.ViewInjector;

import java.lang.reflect.InvocationTargetException;

/**
 * 自动注入的Activity。
 *
 * @auther lwh
 */
public abstract class Activity extends FragmentActivity {

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
