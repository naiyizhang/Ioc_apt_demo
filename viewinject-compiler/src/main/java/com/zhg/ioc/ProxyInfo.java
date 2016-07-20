package com.zhg.ioc;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by nyzhang on 2016/7/20.
 */
public class ProxyInfo {
    private String packageName;
    private String proxyClassName;
    private TypeElement typeElement;
    public Map<Integer,VariableElement> injectVariables=new HashMap<>();
    public static final String PROXY="ViewInject";
    public ProxyInfo(Elements elementUtils,TypeElement typeElement){
        this.typeElement=typeElement;
        PackageElement packageElement=elementUtils.getPackageOf(typeElement);
        String packageName=packageElement.getQualifiedName().toString();
        String className=ClassValidator.getClassName(typeElement,packageName);
        this.packageName=packageName;
        this.proxyClassName=className+"$$"+PROXY;
    }

    public String generateJavaCode(){
        StringBuilder sb=new StringBuilder();
        sb.append("//generate code!Do not modify!!!\n\n");
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("import com.zhg.ioc.api.*;\n");
        sb.append("public class ").append(proxyClassName).append(" implements ")
                .append(PROXY).append("<").append(typeElement.getQualifiedName()).append(">")
                .append("{\n\n");
        generateMethodsCode(sb);
        sb.append("\n\n}");
        return sb.toString();

    }

    private void generateMethodsCode(StringBuilder sb) {
        sb.append("@Override\n");
        sb.append("public void inject(").append(typeElement.getQualifiedName()).append(" host,")
                .append("Object source){");
        for(Integer id:injectVariables.keySet()){
            VariableElement variableElement=injectVariables.get(id);
            String name=variableElement.getSimpleName().toString();
            String type=variableElement.asType().toString();
            sb.append("if( source instanceof android.app.Activity)")
                    .append("{\n").append("host.").append(name).append("=")
                    .append("(").append(type).append(")")
                    .append("(((android.app.Activity)source).findViewById(")
                    .append(id).append("));\n}");
            sb.append("else if(source instanceof android.support.v7.app.AppCompatActivity){")
                    .append("\nhost.").append(name).append("=")
                    .append("(").append(type).append(")")
                    .append("(((android.support.v7.app.AppCompatActivity)source)")
                    .append(".findViewById(").append(id).append("));\n").append("}");
            sb.append("else{\n").append("host.").append(name).append("=")
                    .append("(").append(type).append(")")
                    .append("(((android.view.View)source)")
                    .append(".findViewById(").append(id).append("));\n")
                    .append("}");



        }
        sb.append("\n}");
    }

    public String getProxyClassFullName(){
        return packageName+"."+proxyClassName;
    }
    public TypeElement getTypeElement() {
        return typeElement;
    }
}
