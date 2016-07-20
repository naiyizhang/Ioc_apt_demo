package com.zhg.ioc;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by nyzhang on 2016/7/20.
 */
public final class ClassValidator {
    public static boolean isPrivate(Element element){
        return element.getModifiers().contains(Modifier.PRIVATE);
    }
    public static String getClassName(TypeElement element,String packageName){
        return element.getQualifiedName().toString().substring(packageName.length()+1).replace('.','$');
    }
}
