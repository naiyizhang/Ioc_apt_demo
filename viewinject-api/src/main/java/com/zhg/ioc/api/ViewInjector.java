package com.zhg.ioc.api;

import android.app.Activity;
import android.view.View;

/**
 * Created by nyzhang on 2016/7/20.
 */
public class ViewInjector {
    public static final String SUFFIX = "$$ViewInject";

    public static void injectView(Object object, View view) {
        ViewInject viewInject=findProxyObject(object);
        viewInject.inject(object,view);
    }
    public static void injectView(Activity activity){
        ViewInject viewInject=findProxyObject(activity);
        viewInject.inject(activity,activity);
    }

    private static ViewInject findProxyObject(Object obj) {
        Class clazz = obj.getClass();
        String className = clazz.getName() + SUFFIX;
        try {
            Class newClass = Class.forName(className);
            return (ViewInject) newClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException(String.format("can not find %s,some errors occur when compile", obj.getClass().getName() + SUFFIX));

    }

}
