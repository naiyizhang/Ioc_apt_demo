package com.zhg.ioc;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by nyzhang on 2016/7/20.
 */
@AutoService(Processor.class)
public class ViewInjectProcessor extends AbstractProcessor {
    private Filer mFilerUtils;
    private Elements mElementsUtils;
    private Messager mMessager;
    private Map<String,ProxyInfo> mProxyMap=new HashMap<>();
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFilerUtils=processingEnv.getFiler();
        mElementsUtils=processingEnv.getElementUtils();
        mMessager=processingEnv.getMessager();

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes=new LinkedHashSet<>();
        annotationTypes.add(BindView.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mMessager.printMessage(Diagnostic.Kind.NOTE,"processing....");
        mProxyMap.clear();
        Set<? extends Element>elements=roundEnv.getElementsAnnotatedWith(BindView.class);
        for(Element element:elements){
            if(!checkAnnotationUseValid(element,BindView.class)){
                return false;
            }
            VariableElement variableElement= (VariableElement) element;
            TypeElement typeElement= (TypeElement) variableElement.getEnclosingElement();
            String qualifiedName=typeElement.getQualifiedName().toString();
            ProxyInfo proxyInfo=mProxyMap.get(qualifiedName);
            if(proxyInfo==null){
                proxyInfo=new ProxyInfo(mElementsUtils,typeElement);
                mProxyMap.put(qualifiedName,proxyInfo);
            }
            BindView bindView=variableElement.getAnnotation(BindView.class);
            int id=bindView.value();
            proxyInfo.injectVariables.put(id,variableElement);
        }


        for(String key:mProxyMap.keySet()){
            ProxyInfo proxyInfo=mProxyMap.get(key);
            try {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,proxyInfo.generateJavaCode());
                JavaFileObject javaFileObject= processingEnv.getFiler().createSourceFile(proxyInfo.getProxyClassFullName(),proxyInfo.getTypeElement());
                Writer writer=javaFileObject.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                error(proxyInfo.getTypeElement(),"unable to write inject for type %s:%s",proxyInfo.getTypeElement(),e.getMessage());
            }
        }

        return true;
    }

    private boolean checkAnnotationUseValid(Element annotatedElement, Class clazz) {
        if(annotatedElement.getKind()!= ElementKind.FIELD){
            error(annotatedElement,"%s must bo declared on field!",clazz.getSimpleName());
            return false;
        }
        if(ClassValidator.isPrivate(annotatedElement)){
            error(annotatedElement,"%s() must not be private",clazz.getSimpleName());
            return false;
        }
        return true;
    }
    private void error(Element element,String message,Object...args ){
        if(args.length>0){
            message=String.format(message,args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,message,element);
    }
}
