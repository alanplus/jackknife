package com.lwh.jackknife.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lwh.jackknife.ioc.ViewInjector;

import java.lang.reflect.InvocationTargetException;

public abstract class Activity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			ViewInjector injector = new ViewInjector();
			injector.inject(this);
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
