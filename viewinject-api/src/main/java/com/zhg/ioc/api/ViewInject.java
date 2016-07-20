package com.zhg.ioc.api;

/**
 * Created by nyzhang on 2016/7/20.
 */
public interface ViewInject<T> {
    public void inject(T t,Object view);
}
